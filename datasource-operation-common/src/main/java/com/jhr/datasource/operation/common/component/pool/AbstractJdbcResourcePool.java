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
            // ??????????????????????????????
            datasource.setInitialSize(jdbcPoolConfig.getInitialSize());
            // ????????????????????????
            datasource.setMinIdle(jdbcPoolConfig.getMinIdle());
            // ??????????????????????????????
            datasource.setMaxActive(jdbcPoolConfig.getMaxActive());
            // ????????????????????????
            datasource.setRemoveAbandoned(true);
            // ???????????????????????????????????????
            datasource.setMaxWait(60000);
            // ?????????????????????????????????????????????????????????????????????????????????????????????
            datasource.setTimeBetweenEvictionRunsMillis(60000);
            // ?????????????????????????????????????????????????????????????????????5??????
            datasource.setMinEvictableIdleTimeMillis(300000);
            // ????????????????????????????????????3??????
            datasource.setTimeBetweenConnectErrorMillis(180000);
            // ??????
            datasource.setTestWhileIdle(true);
            // ????????????
            datasource.setTestOnBorrow(false);
            // ????????????
            datasource.setTestOnReturn(false);
            // hive???????????????statement
            if (!Constants.DATA_CONN_TYPE_HIVE.equals(connectionInfo.getDsourceType())) {
                datasource.setPoolPreparedStatements(true);
                // ??????????????????????????????SQL
                datasource.setMaxPoolPreparedStatementPerConnectionSize(20);
            }
            // Mysql?????????ping????????????
            System.setProperty("druid.mysql.usePingMethod", "false");
            // ???????????????
            Properties prop = jdbcPoolConfig.getProp();
            if (prop == null) {
                prop = new Properties();
            }
            if (Constants.DATA_CONN_TYPE_ORACLE.equals(connectionInfo.getDsourceType())) {
                prop.setProperty("druid.connectProperties", "remarksReporting=true");
            }
            datasource.configFromPropety(prop);
            // ?????????????????????
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
        // ??????
        ds.close();
    }

    /**
     * ??????????????????????????????
     *
     * @param accessibleIds   ????????????id
     * @param unAccessibleIds ???????????????id
     */
    @Override
    public void generateAllConnectId(List<String> accessibleIds, List<String> unAccessibleIds) {
        for (Map.Entry<String, DataSource> entry : resourceMap.entrySet()) {
            try (Connection ignored1 = entry.getValue().getConnection()) {
                accessibleIds.add(entry.getKey());
            } catch (SQLException e) {
                LOGGER.error("??????jdbc??????????????????", e);
                // ?????????????????????id
                unAccessibleIds.add(entry.getKey());
            }
        }
    }

    @PreDestroy
    public void destroy() {
        // ???????????????
        for (DataSource ds : resourceMap.values()) {
            destroy(ds);
        }
    }
}
