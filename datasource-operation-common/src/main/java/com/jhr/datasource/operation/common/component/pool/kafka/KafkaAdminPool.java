package com.jhr.datasource.operation.common.component.pool.kafka;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.kafka.clients.admin.AdminClient;

/**
 * commons-pool2 实现的连接池实例
 *
 * @author xukun
 * @since 1.0
 */
public class KafkaAdminPool extends GenericObjectPool<AdminClient> {

    public KafkaAdminPool(PooledObjectFactory<AdminClient> factory) {
        super(factory);
    }

    public KafkaAdminPool(PooledObjectFactory<AdminClient> factory, GenericObjectPoolConfig<AdminClient> config) {
        super(factory, config);
    }

    public KafkaAdminPool(PooledObjectFactory<AdminClient> factory, GenericObjectPoolConfig<AdminClient> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }

    @Override
    public void returnObject(AdminClient obj) {
        if (obj != null) {
            super.returnObject(obj);
        }
    }
}
