package com.jhr.datasource.operation.hw.component.pool.impl;

import com.jhr.datasource.operation.api.domain.dto.ConfFile;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.pool.AbstractHBaseResourcePool;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.hw.util.AuthUtils;
import org.apache.hadoop.conf.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.RESOURCE_POOL_BEAN_NAME_HBASE)
public class HBaseResourcePool extends AbstractHBaseResourcePool {

    @Override
    protected void authenticate(Configuration conf, DatasourceConnectionInfo connectionInfo) throws IOException {
        List<ConfFile> confList = connectionInfo.getConfList();
        // KERBEROS认证
        String userPrincipal = connectionInfo.getUserPrincipal();
        String krb5FilePath = localFileTools.getLocalConfPath(confList, Constants.CONF_FILE_TYPE_KRB5_CONF);
        String userKeytabFilePath = localFileTools.getLocalConfPath(confList, Constants.CONF_FILE_TYPE_USER_KEYTAB);
        AuthUtils.initHBase(conf, userPrincipal, krb5FilePath, userKeytabFilePath);
    }

    @Override
    protected void releaseAuthenticateEnv() {
        AuthUtils.releaseKerberos();
    }
}
