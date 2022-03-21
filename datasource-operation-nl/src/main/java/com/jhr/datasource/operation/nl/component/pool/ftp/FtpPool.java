package com.jhr.datasource.operation.nl.component.pool.ftp;

import cn.hutool.extra.ftp.AbstractFtp;
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
public class FtpPool extends GenericObjectPool<AbstractFtp> {

    public FtpPool(PooledObjectFactory<AbstractFtp> factory) {
        super(factory);
    }

    public FtpPool(PooledObjectFactory<AbstractFtp> factory, GenericObjectPoolConfig<AbstractFtp> config) {
        super(factory, config);
    }

    public FtpPool(PooledObjectFactory<AbstractFtp> factory, GenericObjectPoolConfig<AbstractFtp> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }

    @Override
    public void returnObject(AbstractFtp obj) {
        if (obj != null) {
            super.returnObject(obj);
        }
    }
}
