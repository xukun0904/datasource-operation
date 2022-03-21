package com.jhr.datasource.operation.hw.component.pool.kafka;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.component.pool.kafka.KafkaAdminFactory;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.hw.util.AuthUtils;

import java.io.IOException;

/**
 * @author xukun
 * @since 1.0
 */
public class HwKafkaAdminFactory extends KafkaAdminFactory {

    private final LocalFileTools localFileTools;

    public HwKafkaAdminFactory(DatasourceConnectionInfo connectionInfo, LocalFileTools localFileTools) {
        super(connectionInfo);
        this.localFileTools = localFileTools;
    }

    @Override
    protected void authenticate(DatasourceConnectionInfo connectionInfo) throws IOException {
        String keytabFilePath = localFileTools.getLocalConfPath(connectionInfo.getConfList(), Constants.CONF_FILE_TYPE_USER_KEYTAB);
        String userPrincipal = connectionInfo.getUserPrincipal();
        String krb5FilePath = localFileTools.getLocalConfPath(connectionInfo.getConfList(), Constants.CONF_FILE_TYPE_KRB5_CONF);
        AuthUtils.initKafka(krb5FilePath, keytabFilePath, userPrincipal);
    }

    @Override
    protected void releaseAuthenticateEnv() {
        super.releaseAuthenticateEnv();
    }
}
