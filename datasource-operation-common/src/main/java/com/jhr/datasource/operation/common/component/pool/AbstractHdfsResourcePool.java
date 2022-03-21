package com.jhr.datasource.operation.common.component.pool;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.component.pool.hdfs.HdfsClient;
import com.jhr.datasource.operation.common.component.pool.hdfs.HdfsFactory;
import com.jhr.datasource.operation.common.component.pool.hdfs.HdfsPool;
import com.jhr.datasource.operation.common.config.HdfsPoolConfig;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xukun
 * @since 1.0
 */
public abstract class AbstractHdfsResourcePool extends ResourcePool {

    private final Map<String, HdfsPool> resourceMap = new ConcurrentHashMap<>();

    @Autowired
    private HdfsPoolConfig hdfsPoolConfig;

    @Autowired
    protected LocalFileTools localFileTools;

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractHdfsResourcePool.class);

    @Override
    public HdfsPool getResource(DatasourceConnectionInfo connectionInfo) {
        try {
            HdfsFactory hdfsFactory = getHdfsFactory(connectionInfo);
            HdfsPool hdfsPool = new HdfsPool(hdfsFactory, hdfsPoolConfig);
            hdfsPool.preparePool();
            return hdfsPool;
        } catch (Exception e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        }
        return null;
    }

    protected HdfsFactory getHdfsFactory(DatasourceConnectionInfo connectionInfo) {
        return new HdfsFactory(connectionInfo, localFileTools);
    }

    @Override
    public <T> Map<String, T> getResourceMap() {
        return (Map<String, T>) resourceMap;
    }

    @Override
    public <T> void destroy(T resource) {
        if (resource != null) {
            HdfsPool hdfsPool = (HdfsPool) resource;
            hdfsPool.close();
            hdfsPool = null;
        }
    }

    @Override
    public void generateAllConnectId(List<String> accessibleIds, List<String> unAccessibleIds) {
        for (Map.Entry<String, HdfsPool> entry : resourceMap.entrySet()) {
            HdfsPool hdfsPool = entry.getValue();
            HdfsClient hdfsClient = null;
            try {
                hdfsClient = hdfsPool.borrowObject();
                hdfsClient.isConnected();
                accessibleIds.add(entry.getKey());
            } catch (Exception e) {
                LOGGER.error("连接Hdfs数据源失败！", e);
                // 连接异常的主键id
                unAccessibleIds.add(entry.getKey());
            } finally {
                hdfsPool.returnObject(hdfsClient);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        // 销毁连接池
        for (HdfsPool hdfsPool : resourceMap.values()) {
            destroy(hdfsPool);
        }
    }
}
