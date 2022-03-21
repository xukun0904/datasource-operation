package com.jhr.datasource.operation.common.message;

import cn.hutool.core.collection.CollectionUtil;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.pool.ResourcePool;
import com.jhr.datasource.operation.common.component.pool.ResourcePoolStrategy;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;

import java.util.List;
import java.util.Map;

/**
 * @author xukun
 * @since 1.0
 */
public abstract class AbstractResourcePoolListener {
    @Autowired
    private ResourcePoolStrategy poolStrategy;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResourcePoolListener.class);

    public void addResource(DatasourceConnectionInfo connectionInfo) {
        ResourcePool resourcePool = poolStrategy.getResourcePoolByBeanName(Constants.DATASOURCE_POOL_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        resourcePool.addResource(connectionInfo);
    }

    public void updateResource(DatasourceConnectionInfo connectionInfo) {
        ResourcePool resourcePool = poolStrategy.getResourcePoolByBeanName(Constants.DATASOURCE_POOL_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        resourcePool.updateResource(connectionInfo);
    }

    public void removeResources(Map<Short, List<String>> deleteResourceMap) {
        if (CollectionUtil.isNotEmpty(deleteResourceMap)) {
            for (Short dsourceType : deleteResourceMap.keySet()) {
                ResourcePool resourcePool = poolStrategy.getResourcePoolByBeanName(Constants.DATASOURCE_POOL_BEAN_NAME_MAP.get(dsourceType));
                for (String id : deleteResourceMap.get(dsourceType)) {
                    resourcePool.removeById(id);
                }
            }
        }
    }

    @StreamListener(ResourcePoolSink.CLEAN_UP_INPUT)
    public void cleanUpResourcePool(List<String> ids) {
        int removeSize = 0;
        if (CollectionUtil.isNotEmpty(ids)) {
            for (ResourcePool resourcePool : poolStrategy.getResourcePools()) {
                removeSize += resourcePool.cleanUpResourcePool(ids);
            }
        }
        LOGGER.debug("废弃连接池清理成功，清理{}个连接！", removeSize);
    }
}
