package com.jhr.datasource.operation.hw.component.pool.hdfs;

import com.jhr.datasource.operation.api.domain.dto.ConfFile;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.component.pool.hdfs.HdfsClient;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.hw.util.AuthUtils;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.util.List;

/**
 * HDFS 连接实例
 *
 * @author xukun
 * @since 1.0
 */
public class HwHdfsClient extends HdfsClient {

    public HwHdfsClient(DatasourceConnectionInfo connectionInfo, LocalFileTools localFileTools) {
        super(connectionInfo, localFileTools);
    }

    @Override
    protected void authenticate(List<ConfFile> confList, Configuration conf) throws IOException {
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
