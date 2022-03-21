package com.jhr.datasource.operation.common.component.summoner;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jhr.datasource.operation.api.domain.dto.ConfFile;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.component.pool.ResourcePool;
import com.jhr.datasource.operation.common.component.pool.hdfs.HdfsClient;
import com.jhr.datasource.operation.common.component.pool.hdfs.HdfsPool;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.CustomException;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author xukun
 * @since 1.0
 */
public abstract class AbstractHdfsSummoner extends DatasourceSummoner {

    @Autowired
    protected LocalFileTools localFileTools;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHdfsSummoner.class);

    public FileSystem getFileSystem(DatasourceConnectionInfo connectionInfo) throws IOException {
        List<ConfFile> confList = connectionInfo.getConfList();
        List<String> siteXmlPaths = localFileTools.getLocalConfPaths(confList, Constants.CONF_FILE_TYPE_HDFS_XML);
        Configuration conf = DatasourceOperationUtils.getHdfsConfiguration(siteXmlPaths, connectionInfo.getIp());
        if (Constants.DATA_SOURCE_AUTH_TYPE_KERBEROS.equals(connectionInfo.getAuthType())) {
            authenticate(connectionInfo, conf);
        }
        return FileSystem.get(conf);
    }

    protected void authenticate(DatasourceConnectionInfo connectionInfo, Configuration conf) throws IOException {
    }

    @Override
    public void testConnectAccessible(DatasourceConnectionInfo connectionInfo) {
        try (FileSystem fileSystem = getFileSystem(connectionInfo)) {
            fileSystem.mkdirs(new Path(Constants.HDFS_TEST_CONNECT_PATH));
            LOGGER.debug("数据源连接正常！");
        } catch (IOException e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        } finally {
            releaseAuthenticateEnv();
        }
    }

    protected void releaseAuthenticateEnv() {
    }

    private HdfsPool getExistHdfsPool(DatasourceConnectionInfo connectionInfo) {
        ResourcePool resourcePool = poolStrategy.getResourcePoolByBeanName(Constants.DATASOURCE_POOL_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        return resourcePool.get(connectionInfo);
    }

    @Override
    public void testExistConnectAccessible(DatasourceConnectionInfo connectionInfo) {
        HdfsPool hdfsPool = getExistHdfsPool(connectionInfo);
        HdfsClient hdfsClient = null;
        try {
            hdfsClient = hdfsPool.borrowObject();
            FileSystem fileSystem = hdfsClient.getFileSystem();
            fileSystem.mkdirs(new Path(Constants.HDFS_TEST_CONNECT_PATH));
        } catch (Exception e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        } finally {
            hdfsPool.returnObject(hdfsClient);
        }
    }

    @Override
    public int getFieldNumber(DatasourceConnectionInfo connectionInfo) {
        int fieldNumber = 0;
        BufferedReader reader = null;
        String tableName = connectionInfo.getTableName();
        HdfsPool hdfsPool = getExistHdfsPool(connectionInfo);
        HdfsClient hdfsClient = null;
        try {
            hdfsClient = hdfsPool.borrowObject();
            FileSystem fileSystem = hdfsClient.getFileSystem();
            Path path = new Path(tableName);
            if (fileSystem.exists(path)) {
                String advanceParamConf = connectionInfo.getAdvanceParamConf();
                JSONObject conf = JSONUtil.parseObj(advanceParamConf);
                String delimiter = conf.getStr(Constants.DATA_SOURCE_INPUT_CONF_KEY_DELIMITER, StrUtil.COMMA);
                String newLine = conf.getStr(Constants.DATA_SOURCE_INPUT_CONF_KEY_NEWLINE, StrUtil.CRLF);
                Integer fileTag = conf.getInt(Constants.DATA_SOURCE_INPUT_CONF_KEY_FILE_TAG, 1);
                // 如果是文件夹获取第一个文件
                if (Constants.DATA_SOURCE_INPUT_CONF_FILE_TAG_PATH.equals(fileTag)) {
                    RemoteIterator<LocatedFileStatus> files = fileSystem.listFiles(path, false);
                    if (files.hasNext()) {
                        path = files.next().getPath();
                    } else {
                        ExceptionCast.cast(DatasourceCode.DATASOURCE_DIR_PATH_IS_EMPTY);
                    }
                }
                FSDataInputStream inputStream = fileSystem.open(path);
                reader = IoUtil.getReader(inputStream, StandardCharsets.UTF_8);
                // 读取文件的第一行
                String line = DatasourceOperationUtils.readFirstLineByNewLine(reader, newLine);
                if (StrUtil.isNotBlank(line)) {
                    fieldNumber = StrUtil.split(line, delimiter).length;
                }
            } else {
                ExceptionCast.cast(DatasourceCode.DATASOURCE_FILE_PATH_NOT_EXIST);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("获取Hdfs文件字段数量失败！", e);
        } finally {
            IoUtil.close(reader);
            hdfsPool.returnObject(hdfsClient);
        }
        return fieldNumber;
    }
}
