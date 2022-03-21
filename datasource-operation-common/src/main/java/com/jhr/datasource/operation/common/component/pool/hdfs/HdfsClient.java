package com.jhr.datasource.operation.common.component.pool.hdfs;

import cn.hutool.core.util.StrUtil;
import com.jhr.datasource.operation.api.domain.dto.ConfFile;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * HDFS 连接实例
 *
 * @author xukun
 * @since 1.0
 */
public class HdfsClient {

    public static final Logger LOGGER = LoggerFactory.getLogger(HdfsClient.class);

    protected final DatasourceConnectionInfo connectionInfo;

    protected final LocalFileTools localFileTools;

    private FileSystem fileSystem;

    public HdfsClient(DatasourceConnectionInfo connectionInfo, LocalFileTools localFileTools) {
        this.connectionInfo = connectionInfo;
        this.localFileTools = localFileTools;
    }

    public void createFileSystem() {
        try {
            List<ConfFile> confList = connectionInfo.getConfList();
            Short authType = connectionInfo.getAuthType();
            String ip = connectionInfo.getIp();
            List<String> siteXmlPaths = localFileTools.getLocalConfPaths(confList, Constants.CONF_FILE_TYPE_HDFS_XML);
            Configuration conf = DatasourceOperationUtils.getHdfsConfiguration(siteXmlPaths, ip);
            if (Constants.DATA_SOURCE_AUTH_TYPE_KERBEROS.equals(authType)) {
                authenticate(confList, conf);
            }
            fileSystem = FileSystem.get(conf);
            LOGGER.debug("HDFS创建实例成功");
        } catch (IOException e) {
            LOGGER.error("HDFS创建实例失败", e);
        } finally {
            releaseAuthenticateEnv();
        }
    }

    protected void authenticate(List<ConfFile> confList, Configuration conf) throws IOException {
    }

    protected void releaseAuthenticateEnv() {
    }

    public void close() {
        try {
            if (null != fileSystem) {
                fileSystem.close();
                LOGGER.debug("HDFS关闭实例成功");
            }
        } catch (IOException e) {
            LOGGER.error("HDFS关闭实例失败", e);
        }
    }

    public boolean isConnected() throws IOException {
        return fileSystem.exists(new Path(StrUtil.SLASH));
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void borrowObjectPreProcess() {
        Short authType = connectionInfo.getAuthType();
        if (Constants.DATA_SOURCE_AUTH_TYPE_KERBEROS.equals(authType)) {
            UserGroupInformation.setConfiguration(fileSystem.getConf());
        }
    }

    public void returnObjectPostProcess() {
        Short authType = connectionInfo.getAuthType();
        if (Constants.DATA_SOURCE_AUTH_TYPE_KERBEROS.equals(authType)) {
            UserGroupInformation.reset();
        }
    }
}
