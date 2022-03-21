package com.jhr.datasource.operation.common.component.pool;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.summoner.DatasourceSummoner;
import com.jhr.datasource.operation.common.component.summoner.DatasourceSummonerStrategy;
import com.jhr.datasource.operation.common.config.JdbcPoolConfig;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xukun
 * @since 1.0
 */
public abstract class AbstractJdbcResourcePool extends ResourcePool {

    private final Map<String, DataSource> resourceMap = new ConcurrentHashMap<>();

    @Autowired
    private JdbcPoolConfig jdbcPoolConfig;

    @Autowired
    private DatasourceSummonerStrategy summonerStrategy;

    @Override
    protected DataSource getResource(DatasourceConnectionInfo connectionInfo) {
        try {
            DruidDataSource datasource = getDruidDataSource();
            if (Constants.DATA_SOURCE_AUTH_TYPE_KERBEROS.equals(connectionInfo.getAuthType()) &&
                    CollectionUtil.isNotEmpty(connectionInfo.getConfList())) {
                authenticate(connectionInfo, datasource);
            }
            datasource.setUrl(getJdbcUrl(connectionInfo));
            datasource.setUsername(connectionInfo.getUsername());
            datasource.setPassword(connectionInfo.getPassword());
            String validationQuery = "select 1";
            if (Constants.DATA_CONN_TYPE_ORACLE.equals(connectionInfo.getDsourceType())) {
                validationQuery = "select 1 from dual";
            }
            datasource.setValidationQuery(validationQuery);
            // 连接池初始化连接数量
            datasource.setInitialSize(jdbcPoolConfig.getInitialSize());
            // 连接池最小空闲数
            datasource.setMinIdle(jdbcPoolConfig.getMinIdle());
            // 连接池最大活跃连接数
            datasource.setMaxActive(jdbcPoolConfig.getMaxActive());
            // 是否回收闲置连接
            datasource.setRemoveAbandoned(true);
            // 配置获取连接等待超时的时间
            datasource.setMaxWait(60000);
            // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
            datasource.setTimeBetweenEvictionRunsMillis(60000);
            // 配置一个连接在池中最小生存的时间，单位是毫秒，5分钟
            datasource.setMinEvictableIdleTimeMillis(300000);
            // 设置多久重试，单位毫秒，3分钟
            datasource.setTimeBetweenConnectErrorMillis(180000);
            // 高效
            datasource.setTestWhileIdle(true);
            // 影响性能
            datasource.setTestOnBorrow(false);
            // 影响性能
            datasource.setTestOnReturn(false);
            // hive不支持缓存statement
            if (!Constants.DATA_CONN_TYPE_HIVE.equals(connectionInfo.getDsourceType())) {
                datasource.setPoolPreparedStatements(true);
                // 每个连接最多缓存多个SQL
                datasource.setMaxPoolPreparedStatementPerConnectionSize(20);
            }
            // Mysql不适用ping检查连接
            System.setProperty("druid.mysql.usePingMethod", "false");
            // 自定义属性
            Properties prop = jdbcPoolConfig.getProp();
            if (prop == null) {
                prop = new Properties();
            }
            if (Constants.DATA_CONN_TYPE_ORACLE.equals(connectionInfo.getDsourceType())) {
                prop.setProperty("druid.connectProperties", "remarksReporting=true");
            }
            datasource.configFromPropety(prop);
            // 手动进行初始化
            datasource.init();
            return datasource;
        } catch (SQLException e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        } finally {
            releaseAuthenticateEnv();
        }
        return null;
    }

    private String getJdbcUrl(DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = summonerStrategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        return summoner.getJdbcUrl(connectionInfo);
    }

    protected void authenticate(DatasourceConnectionInfo connectionInfo, DruidDataSource datasource) {
    }

    protected void releaseAuthenticateEnv() {
    }

    protected DruidDataSource getDruidDataSource() {
        return new DruidDataSource();
    }

    public JdbcTemplate getJdbcTemplate(DatasourceConnectionInfo connectionInfo) {
        DataSource dataSource = this.get(connectionInfo);
        if (dataSource != null) {
            return new JdbcTemplate(dataSource);
        }
        return null;
    }

    @Override
    public <T> Map<String, T> getResourceMap() {
        return (Map<String, T>) resourceMap;
    }

    @Override
    public <T> void destroy(T resource) {
        DruidDataSource ds = (DruidDataSource) resource;
        // 销毁
        ds.close();
    }

    /**
     * 根据连接状态进行分类
     *
     * @param accessibleIds   可用连接id
     * @param unAccessibleIds 不可用连接id
     */
    @Override
    public void generateAllConnectId(List<String> accessibleIds, List<String> unAccessibleIds) {
        for (Map.Entry<String, DataSource> entry : resourceMap.entrySet()) {
            try (Connection ignored1 = entry.getValue().getConnection()) {
                accessibleIds.add(entry.getKey());
            } catch (SQLException e) {
                LOGGER.error("连接jdbc数据源失败！", e);
                // 连接异常的主键id
                unAccessibleIds.add(entry.getKey());
            }
        }
    }

    @PreDestroy
    public void destroy() {
        // 销毁连接池
        for (DataSource ds : resourceMap.values()) {
            destroy(ds);
        }
    }
}
