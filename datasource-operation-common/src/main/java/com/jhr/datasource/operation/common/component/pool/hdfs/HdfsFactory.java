package com.jhr.datasource.operation.common.component.pool.hdfs;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HDFS 工厂类
 *
 * @author xukun
 * @since 1.0
 */
public class HdfsFactory implements PooledObjectFactory<HdfsClient> {

    protected final DatasourceConnectionInfo connectionInfo;

    protected final LocalFileTools localFileTools;

    public static final Logger LOGGER = LoggerFactory.getLogger(HdfsFactory.class);

    public HdfsFactory(DatasourceConnectionInfo connectionInfo, LocalFileTools localFileTools) {
        this.connectionInfo = connectionInfo;
        this.localFileTools = localFileTools;
    }

    @Override
    public PooledObject<HdfsClient> makeObject() {
        HdfsClient hdfsClient = getHdfsClient();
        hdfsClient.createFileSystem();
        return new DefaultPooledObject<>(hdfsClient);
    }

    protected HdfsClient getHdfsClient() {
        return new HdfsClient(connectionInfo, localFileTools);
    }

    @Override
    public void destroyObject(PooledObject<HdfsClient> pooledObject) {
        HdfsClient object = pooledObject.getObject();
        object.close();
    }

    @Override
    public boolean validateObject(PooledObject<HdfsClient> pooledObject) {
        HdfsClient object = pooledObject.getObject();
        try {
            return object.isConnected();
        } catch (IOException e) {
            LOGGER.error("验证当前HDFS对象是否存活失败！", e);
        }
        return false;
    }

    @Override
    public void activateObject(PooledObject<HdfsClient> pooledObject) {
        HdfsClient object = pooledObject.getObject();
        object.borrowObjectPreProcess();
    }

    @Override
    public void passivateObject(PooledObject<HdfsClient> pooledObject) {
        HdfsClient object = pooledObject.getObject();
        object.returnObjectPostProcess();
    }
}
