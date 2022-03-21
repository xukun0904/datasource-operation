package com.jhr.datasource.operation.nl.component.summoner.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.AbstractFtp;
import cn.hutool.extra.ssh.JschRuntimeException;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jcraft.jsch.Session;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.pool.ResourcePool;
import com.jhr.datasource.operation.common.component.summoner.DatasourceSummoner;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import com.jhr.datasource.operation.nl.component.pool.ftp.FtpPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.SUMMONER_BEAN_NAME_SFTP)
public class SftpSummoner extends DatasourceSummoner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SftpSummoner.class);

    public Sftp getSftp(DatasourceConnectionInfo connectionInfo) {
        Session session = JschUtil.createSession(connectionInfo.getIp(), Integer.parseInt(connectionInfo.getPort()), connectionInfo.getUsername(), connectionInfo.getPassword());
        // 设置跳过Kerberos身份认证提示
        session.setConfig("PreferredAuthentications", "public,keyboard-interactive,password");
        return new Sftp(session);
    }

    @Override
    public void testConnectAccessible(DatasourceConnectionInfo connectionInfo) {
        try (Sftp sftp = getSftp(connectionInfo)) {
            sftp.pwd();
        } catch (JschRuntimeException | IOException e) {
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
        AbstractFtp ftp = null;
        FtpPool ftpPool = getExistFtpPool(connectionInfo);
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
        Sftp sftp = null;
        int fieldNumber = 0;
        BufferedReader reader = null;
        try {
            sftp = (Sftp) ftpPool.borrowObject();
            String tableName = connectionInfo.getTableName();
            String fileName = tableName;
            String advanceParamConf = connectionInfo.getAdvanceParamConf();
            JSONObject conf = JSONUtil.parseObj(advanceParamConf);
            Integer fileTag = conf.getInt(Constants.DATA_SOURCE_INPUT_CONF_KEY_FILE_TAG, 1);
            String delimiter = conf.getStr(Constants.DATA_SOURCE_INPUT_CONF_KEY_DELIMITER, StrUtil.COMMA);
            String newLine = conf.getStr(Constants.DATA_SOURCE_INPUT_CONF_KEY_NEWLINE, StrUtil.CRLF);
            if (Constants.DATA_SOURCE_INPUT_CONF_FILE_TAG_PATH.equals(fileTag)) {
                if (!existPath(tableName, sftp)) {
                    ExceptionCast.cast(DatasourceCode.DATASOURCE_FILE_PATH_NOT_EXIST);
                }
                List<String> ftpFileNames = sftp.lsFiles(tableName);
                if (CollectionUtil.isNotEmpty(ftpFileNames)) {
                    fileName = tableName + StrUtil.SLASH + ftpFileNames.get(0);
                } else {
                    ExceptionCast.cast(DatasourceCode.DATASOURCE_DIR_PATH_IS_EMPTY);
                }
            } else if (!sftp.exist(tableName)) {
                ExceptionCast.cast(DatasourceCode.DATASOURCE_FILE_PATH_NOT_EXIST);
            }
            // 读取文件的第一行
            reader = IoUtil.getReader(sftp.getClient().get(fileName), StandardCharsets.UTF_8);
            String line = DatasourceOperationUtils.readFirstLineByNewLine(reader, newLine);
            if (StrUtil.isNotBlank(line)) {
                fieldNumber = StrUtil.split(line, delimiter).length;
            }
        } catch (Exception e) {
            LOGGER.error("获取SFtp文件字段数量失败！", e);
            ExceptionCast.cast(DatasourceCode.DATASOURCE_GET_FIELD_NUMBER_FAILED);
        } finally {
            IoUtil.close(reader);
            ftpPool.returnObject(sftp);
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
