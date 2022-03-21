package com.jhr.datasource.operation.nl.component.pool.impl;

import cn.hutool.extra.ftp.AbstractFtp;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.pool.ResourcePool;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import com.jhr.datasource.operation.nl.component.pool.ftp.FtpFactory;
import com.jhr.datasource.operation.nl.component.pool.ftp.FtpPool;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.RESOURCE_POOL_BEAN_NAME_FTP)
public class FtpResourcePool extends ResourcePool {

    private final Map<String, FtpPool> resourceMap = new ConcurrentHashMap<>();

    @Override
    protected FtpPool getResource(DatasourceConnectionInfo connectionInfo) {
        try {
            FtpFactory ftpFactory = new FtpFactory(connectionInfo);
            FtpPool ftpPool = new FtpPool(ftpFactory);
            ftpPool.preparePool();
            return ftpPool;
        } catch (Exception e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        }
        return null;
    }

    @Override
    public <T> Map<String, T> getResourceMap() {
        return (Map<String, T>) resourceMap;
    }

    @Override
    public <T> void destroy(T resource) {
        FtpPool ftpPool = (FtpPool) resource;
        if (ftpPool != null) {
            ftpPool.close();
            ftpPool = null;
        }
    }

    @Override
    public void generateAllConnectId(List<String> accessibleIds, List<String> unAccessibleIds) {
        for (Map.Entry<String, FtpPool> entry : resourceMap.entrySet()) {
            FtpPool ftpPool = entry.getValue();
            AbstractFtp abstractFtp = null;
            try {
                abstractFtp = ftpPool.borrowObject();
                // 进行查询当前路径操作，测试数据源连接是否正常
                abstractFtp.pwd();
                accessibleIds.add(entry.getKey());
            } catch (Exception e) {
                LOGGER.error("连接文件服务器数据源失败！", e);
                // 连接异常的主键id
                unAccessibleIds.add(entry.getKey());
            } finally {
                ftpPool.returnObject(abstractFtp);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        // 销毁连接池
        for (FtpPool ftpPool : resourceMap.values()) {
            destroy(ftpPool);
        }
    }
}
