package com.jhr.datasource.operation.common.component.pool;

import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * @author xukun
 * @since 1.0
 */
@Component
public class ResourcePoolStrategy {
    @Autowired
    private Map<String, ResourcePool> resourcePoolMap;

    public ResourcePool getResourcePoolByBeanName(String beanName) {
        ResourcePool resourcePool = resourcePoolMap.get(beanName);
        if (resourcePool == null) {
            ExceptionCast.cast(DatasourceCode.DATASOURCE_NOT_SUPPORT);
        }
        return resourcePool;
    }

    public Collection<ResourcePool> getResourcePools() {
        return resourcePoolMap.values();
    }
}
