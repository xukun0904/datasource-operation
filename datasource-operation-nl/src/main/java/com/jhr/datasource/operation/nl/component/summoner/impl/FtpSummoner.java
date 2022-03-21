package com.jhr.datasource.operation.nl.component.summoner.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.AbstractFtp;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpException;
import cn.hutool.extra.ftp.FtpMode;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.pool.ResourcePool;
import com.jhr.datasource.operation.common.component.summoner.DatasourceSummoner;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.CustomException;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import com.jhr.datasource.operation.nl.component.pool.ftp.FtpPool;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.SUMMONER_BEAN_NAME_FTP)
public class FtpSummoner extends DatasourceSummoner {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpSummoner.class);

    public Ftp getFtp(DatasourceConnectionInfo connectionInfo) {
        Ftp ftp = new Ftp(connectionInfo.getIp(), Integer.parseInt(connectionInfo.getPort()), connectionInfo.getUsername(), connectionInfo.getPassword(), CharsetUtil.CHARSET_UTF_8, FtpMode.Passive);
        ftp.setBackToPwd(true);
        return ftp;
    }

    @Override
    public void testConnectAccessible(DatasourceConnectionInfo connectionInfo) {
        try (Ftp ftp = getFtp(connectionInfo)) {
            ftp.pwd();
        } catch (FtpException | IOException e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        }
    }

    private FtpPool getExistFtpPool(DatasourceConnectionInfo connectionInfo) {
        ResourcePool resourcePool = poolStrategy.getResourcePoolByBeanName(Constants.DATASOURCE_POOL_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        return resourcePool.get(connectionInfo);
    }

    /**
     * 测试连接是否正常
     *
     * @param connectionInfo 连接信息
     */
    @Override
    public void testExistConnectAccessible(DatasourceConnectionInfo connectionInfo) {
        FtpPool ftpPool = getExistFtpPool(connectionInfo);
        AbstractFtp ftp = null;
        try {
            ftp = ftpPool.borrowObject();
            ftp.pwd();
        } catch (Exception e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        } finally {
            ftpPool.returnObject(ftp);
        }
    }

    @Override
    public int getFieldNumber(DatasourceConnectionInfo connectionInfo) {
        FtpPool ftpPool = getExistFtpPool(connectionInfo);
        Ftp ftp = null;
        int fieldNumber = 0;
        BufferedReader reader = null;
        try {
            ftp = (Ftp) ftpPool.borrowObject();
            String tableName = connectionInfo.getTableName();
            String advanceParamConf = connectionInfo.getAdvanceParamConf();
            JSONObject conf = JSONUtil.parseObj(advanceParamConf);
            String newLine = conf.getStr(Constants.DATA_SOURCE_INPUT_CONF_KEY_NEWLINE, StrUtil.CRLF);
            String delimiter = conf.getStr(Constants.DATA_SOURCE_INPUT_CONF_KEY_DELIMITER, StrUtil.COMMA);
            Integer fileTag = conf.getInt(Constants.DATA_SOURCE_INPUT_CONF_KEY_FILE_TAG, 1);
            String fileName = tableName;
            if (Constants.DATA_SOURCE_INPUT_CONF_FILE_TAG_PATH.equals(fileTag)) {
                if (!existPath(tableName, ftp)) {
                    ExceptionCast.cast(DatasourceCode.DATASOURCE_FILE_PATH_NOT_EXIST);
                }
                FTPFile[] ftpFiles = ftp.lsFiles(tableName);
                if (ArrayUtil.isNotEmpty(ftpFiles)) {
                    FTPFile ftpFile = ftpFiles[0];
                    fileName = tableName + StrUtil.SLASH + ftpFile.getName();
                } else {
                    ExceptionCast.cast(DatasourceCode.DATASOURCE_DIR_PATH_IS_EMPTY);
                }
            } else if (!ftp.exist(tableName)) {
                ExceptionCast.cast(DatasourceCode.DATASOURCE_FILE_PATH_NOT_EXIST);
            }
            // 读取文件的第一行
            FTPClient client = ftp.getClient();
            reader = IoUtil.getReader(client.retrieveFileStream(fileName), StandardCharsets.UTF_8);
            String line = DatasourceOperationUtils.readFirstLineByNewLine(reader, newLine);
            if (StrUtil.isNotBlank(line)) {
                fieldNumber = StrUtil.split(line, delimiter).length;
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("获取Ftp文件字段数量失败！", e);
            ExceptionCast.cast(DatasourceCode.DATASOURCE_GET_FIELD_NUMBER_FAILED);
        } finally {
            IoUtil.close(reader);
            ftpPool.returnObject(ftp);
        }
        return fieldNumber;
    }

    public boolean existPath(String path, AbstractFtp ftp) {
        String currentPath = ftp.pwd();
        try {
            return ftp.cd(path);
        } finally {
            ftp.cd(currentPath);
        }
    }
}
