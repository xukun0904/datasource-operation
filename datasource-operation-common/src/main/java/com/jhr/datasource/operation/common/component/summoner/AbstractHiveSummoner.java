package com.jhr.datasource.operation.common.component.summoner;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author xukun
 * @since 1.0
 */
public abstract class AbstractHiveSummoner extends JdbcSummoner {

    @Autowired
    private LocalFileTools localFileTools;

    @Override
    public Connection getConnection(DatasourceConnectionInfo connectionInfo) throws ClassNotFoundException, SQLException {
        try {
            Class.forName(Constants.DATASOURCE_DRIVER_MAP.get(Constants.DATA_CONN_TYPE_HIVE));
            if (Constants.DATA_SOURCE_AUTH_TYPE_KERBEROS.equals(connectionInfo.getAuthType())) {
                authenticate(connectionInfo);
            }
            return DriverManager.getConnection(getJdbcUrl(connectionInfo), connectionInfo.getUsername(), connectionInfo.getPassword());
        } finally {
            releaseAuthenticateEnv();
        }
    }

    protected void authenticate(DatasourceConnectionInfo connectionInfo) {
    }

    protected void releaseAuthenticateEnv() {
    }

    @Override
    public String getJdbcUrl(DatasourceConnectionInfo connectionInfo) {
        StringBuilder jdbcUrlBuilder = new StringBuilder(
                "jdbc:hive2://").append(connectionInfo.getIpAndPort()).append(StrUtil.SLASH).append(connectionInfo.getDbName());
        if (StrUtil.isNotBlank(connectionInfo.getServiceDiscoveryMode())) {
            jdbcUrlBuilder.append(";serviceDiscoveryMode=").append(connectionInfo.getServiceDiscoveryMode());
        }
        if (StrUtil.isNotBlank(connectionInfo.getZookeeperNamespace())) {
            jdbcUrlBuilder.append(";zooKeeperNamespace=").append(connectionInfo.getZookeeperNamespace());
        }
        if (Constants.DATA_SOURCE_AUTH_TYPE_KERBEROS.equals(connectionInfo.getAuthType())) {
            String userKeytabFilePath = localFileTools.getLocalConfPath(connectionInfo.getConfList(), Constants.CONF_FILE_TYPE_USER_KEYTAB);
            jdbcUrlBuilder.append(";sasl.qop=").append(connectionInfo.getSaslQop())
                    .append(";auth=").append(Constants.DATA_SOURCE_AUTH_KERBEROS)
                    .append(";principal=").append(connectionInfo.getPrincipal())
                    .append(";user.principal=").append(connectionInfo.getUserPrincipal())
                    .append(";user.keytab=").append(userKeytabFilePath).append(Constants.SEMICOLON);
        } else {
            //普通模式
            jdbcUrlBuilder.append(";auth=none");
        }
        return jdbcUrlBuilder.toString();
    }

    @Override
    public void batchExecuteSqlMap(DatasourceConnectionInfo connectionInfo) {
        try {
            Map<String, Object[]> sqlMap = connectionInfo.getSqlMap();
            if (CollectionUtil.isNotEmpty(sqlMap)) {
                JdbcTemplate jdbcTemplate = getJdbcTemplate(connectionInfo);
                for (String sql : sqlMap.keySet()) {
                    jdbcTemplate.execute(sql);
                }
            }
        } catch (Exception e) {
            LOGGER.error("执行SQL失败！", e);
            ExceptionCast.cast(DatasourceCode.DATASOURCE_BATCH_EXECUTE_SQL_MAP_FAILED);
        }
    }
}
