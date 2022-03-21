package com.jhr.datasource.operation.hw.component.pool.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.component.pool.AbstractJdbcResourcePool;
import com.jhr.datasource.operation.common.config.JdbcPoolConfig;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.hw.component.pool.DruidDataSourceWrapper;
import com.jhr.datasource.operation.hw.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.RESOURCE_POOL_BEAN_NAME_JDBC)
@EnableConfigurationProperties(JdbcPoolConfig.class)
public class JdbcResourcePool extends AbstractJdbcResourcePool {

    @Autowired
    private LocalFileTools localFileTools;

    @Override
    protected void authenticate(DatasourceConnectionInfo connectionInfo, DruidDataSource datasource) {
        String krb5FilePath = localFileTools.getLocalConfPath(connectionInfo.getConfList(), Constants.CONF_FILE_TYPE_KRB5_CONF);
        datasource.addConnectionProperty(Constants.KRB_5_FILE_PATH, krb5FilePath);
    }

    @Override
    protected DruidDataSource getDruidDataSource() {
        return new DruidDataSourceWrapper();
    }

    @Override
    protected void releaseAuthenticateEnv() {
        AuthUtils.releaseKerberos();
    }
}
