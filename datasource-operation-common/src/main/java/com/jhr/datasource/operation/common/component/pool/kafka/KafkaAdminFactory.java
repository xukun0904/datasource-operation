package com.jhr.datasource.operation.common.component.pool.kafka;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author xukun
 * @since 1.0
 */
public class KafkaAdminFactory implements PooledObjectFactory<AdminClient> {

    protected final DatasourceConnectionInfo connectionInfo;

    public static final Logger LOGGER = LoggerFactory.getLogger(KafkaAdminFactory.class);

    public KafkaAdminFactory(DatasourceConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    @Override
    public PooledObject<AdminClient> makeObject() {
        AdminClient client = null;
        try {
            if (Constants.DATA_SOURCE_AUTH_TYPE_KERBEROS.equals(connectionInfo.getAuthType())) {
                authenticate(connectionInfo);
            }
            Properties props = DatasourceOperationUtils.getKafkaProperties(connectionInfo);
            client = AdminClient.create(props);
        } catch (Exception e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        } finally {
            releaseAuthenticateEnv();
        }
        return new DefaultPooledObject<>(client);
    }

    protected void authenticate(DatasourceConnectionInfo connectionInfo) throws IOException {
    }

    protected void releaseAuthenticateEnv() {
    }

    @Override
    public void destroyObject(PooledObject<AdminClient> pooledObject) {
        AdminClient object = pooledObject.getObject();
        object.close();
    }

    @Override
    public boolean validateObject(PooledObject<AdminClient> pooledObject) {
        AdminClient object = pooledObject.getObject();
        try {
            // 测试连接是否正常
            ListTopicsResult topics = object.listTopics();
            topics.names().get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("验证当前Kafka Admin对象是否存活失败！", e);
        }
        return false;
    }

    @Override
    public void activateObject(PooledObject<AdminClient> pooledObject) {
    }

    @Override
    public void passivateObject(PooledObject<AdminClient> pooledObject) {
    }
}
