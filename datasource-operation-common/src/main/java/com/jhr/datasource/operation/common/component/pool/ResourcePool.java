package com.jhr.datasource.operation.common.component.pool;

import cn.hutool.core.util.StrUtil;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 数据源管理
 *
 * @author xukun
 * @since 1.0
 */
public abstract class ResourcePool {

    public static final Logger LOGGER = LoggerFactory.getLogger(ResourcePool.class);

    protected abstract <T> T getResource(DatasourceConnectionInfo connectionInfo);

    public abstract <T> Map<String, T> getResourceMap();

    public <T> T get(DatasourceConnectionInfo connectionInfo) {
        if (!existById(connectionInfo.getId())) {
            // 可能因为集群导致线程池不一致问题
            addResource(connectionInfo);
        }
        return this.getById(connectionInfo.getId());
    }

    public <T> T getById(String id) {
        return (T) getResourceMap().get(id);
    }

    public boolean existById(String id) {
        return getResourceMap().containsKey(id);
    }

    public <T> void removeById(String id) {
        T resource = (T) getResourceMap().get(id);
        if (resource != null) {
            destroy(resource);
            getResourceMap().remove(id);
        }
    }

    public abstract <T> void destroy(T resource);

    public void addResource(DatasourceConnectionInfo connectionInfo) {
        // 判断连接池是否已存在
        String id = connectionInfo.getId();
        if (StrUtil.isNotBlank(id)) {
            if (!existById(id)) {
                Object resource = getResource(connectionInfo);
                if (resource != null) {
                    getResourceMap().put(id, resource);
                }
            } else {
                LOGGER.debug("当前资源池已存在！");
            }
        }
    }

    /**
     * 根据连接状态进行分类
     *
     * @param accessibleIds   可用连接id
     * @param unAccessibleIds 不可用连接id
     */
    public abstract void generateAllConnectId(List<String> accessibleIds, List<String> unAccessibleIds);

    public void updateResource(DatasourceConnectionInfo connectionInfo) {
        // 先删除
        removeById(connectionInfo.getId());
        // 再添加
        addResource(connectionInfo);
    }

    public int cleanUpResourcePool(List<String> ids) {
        int size = 0;
        // 清理已被删除的连接池
        for (String id : getResourceMap().keySet()) {
            if (!ids.contains(id)) {
                this.removeById(id);
                size++;
            }
        }
        return size;
    }
}
