package com.jhr.datasource.operation.hw.component.summoner.impl;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.component.summoner.AbstractHiveSummoner;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.hw.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.SUMMONER_BEAN_NAME_HIVE)
public class HiveSummoner extends AbstractHiveSummoner {

    @Autowired
    private LocalFileTools localFileTools;

    @Override
    protected void authenticate(DatasourceConnectionInfo connectionInfo) {
        String krb5FilePath = localFileTools.getLocalConfPath(connectionInfo.getConfList(), Constants.CONF_FILE_TYPE_KRB5_CONF);
        AuthUtils.initHive(krb5FilePath);
    }

    @Override
    protected void releaseAuthenticateEnv() {
        AuthUtils.releaseKerberos();
    }
}
