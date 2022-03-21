package com.jhr.datasource.operation.hw.component.summoner.impl;

import com.jhr.datasource.operation.api.domain.dto.ConfFile;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.summoner.AbstractHdfsSummoner;
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
@Component(Constants.SUMMONER_BEAN_NAME_HDFS)
public class HdfsSummoner extends AbstractHdfsSummoner {

    @Override
    protected void authenticate(DatasourceConnectionInfo connectionInfo, Configuration conf) throws IOException {
        List<ConfFile> confList = connectionInfo.getConfList();
        // KERBEROS认证
        String krb5FilePath = localFileTools.getLocalConfPath(confList, Constants.CONF_FILE_TYPE_KRB5_CONF);
        String userKeytabFilePath = localFileTools.getLocalConfPath(confList, Constants.CONF_FILE_TYPE_USER_KEYTAB);
        AuthUtils.initHdfs(conf, krb5FilePath, userKeytabFilePath, connectionInfo.getUsername());
    }

    @Override
    protected void releaseAuthenticateEnv() {
        AuthUtils.releaseKerberos();
    }
}
