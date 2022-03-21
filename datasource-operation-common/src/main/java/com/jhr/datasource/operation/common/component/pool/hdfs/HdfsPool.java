package com.jhr.datasource.operation.common.component.pool.hdfs;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * commons-pool2 实现的连接池实例
 *
 * @author xukun
 * @since 1.0
 */
public class HdfsPool extends GenericObjectPool<HdfsClient> {

    public HdfsPool(PooledObjectFactory<HdfsClient> factory) {
        super(factory);
    }

    public HdfsPool(PooledObjectFactory<HdfsClient> factory, GenericObjectPoolConfig<HdfsClient> config) {
        super(factory, config);
    }

    public HdfsPool(PooledObjectFactory<HdfsClient> factory, GenericObjectPoolConfig<HdfsClient> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }

    @Override
    public void returnObject(HdfsClient obj) {
        if (obj != null) {
            super.returnObject(obj);
        }
    }
}
