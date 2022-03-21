package com.jhr.datasource.operation.hw.component.summoner.impl;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.summoner.AbstractKafkaSummoner;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.hw.util.AuthUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.SUMMONER_BEAN_NAME_KAFKA)
public class KafkaSummoner extends AbstractKafkaSummoner {

    @Override
    protected void authenticate(DatasourceConnectionInfo connectionInfo) throws IOException {
        String krb5FilePath = localFileTools.getLocalConfPath(connectionInfo.getConfList(), Constants.CONF_FILE_TYPE_KRB5_CONF);
        String keytabFilePath = localFileTools.getLocalConfPath(connectionInfo.getConfList(), Constants.CONF_FILE_TYPE_USER_KEYTAB);
        String userPrincipal = connectionInfo.getUserPrincipal();
        AuthUtils.initKafka(krb5FilePath, keytabFilePath, userPrincipal);
    }

    @Override
    protected void releaseAuthenticateEnv() {
        AuthUtils.releaseKerberos();
    }
}
