package com.jhr.datasource.operation.common.component.pool;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.component.pool.kafka.KafkaAdminFactory;
import com.jhr.datasource.operation.common.component.pool.kafka.KafkaAdminPool;
import com.jhr.datasource.operation.common.config.KafkaAdminPoolConfig;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xukun
 * @since 1.0
 */
public abstract class AbstractKafkaAdminResourcePool extends ResourcePool {

    private final Map<String, KafkaAdminPool> resourceMap = new ConcurrentHashMap<>();

    @Autowired
    protected LocalFileTools localFileTools;

    @Autowired
    private KafkaAdminPoolConfig kafkaAdminPoolConfig;

    @Override
    protected KafkaAdminPool getResource(DatasourceConnectionInfo connectionInfo) {
        try {
            KafkaAdminFactory kafkaAdminFactory = getKafkaAdminFactory(connectionInfo);
            KafkaAdminPool kafkaAdminPool = new KafkaAdminPool(kafkaAdminFactory, kafkaAdminPoolConfig);
            kafkaAdminPool.preparePool();
            return kafkaAdminPool;
        } catch (Exception e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        }
        return null;
    }

    protected KafkaAdminFactory getKafkaAdminFactory(DatasourceConnectionInfo connectionInfo) {
        return new KafkaAdminFactory(connectionInfo);
    }

    @Override
    public <T> void destroy(T resource) {
        KafkaAdminPool adminPool = (KafkaAdminPool) resource;
        if (adminPool != null) {
            adminPool.close();
            adminPool = null;
        }
    }

    @Override
    public <T> Map<String, T> getResourceMap() {
        return (Map<String, T>) resourceMap;
    }

    @Override
    public void generateAllConnectId(List<String> accessibleIds, List<String> unAccessibleIds) {
        for (Map.Entry<String, KafkaAdminPool> entry : resourceMap.entrySet()) {
            KafkaAdminPool adminPool = entry.getValue();
            AdminClient client = null;
            try {
                client = adminPool.borrowObject();
                // 进行获取所有消息名操作，测试数据源连接是否正常
                ListTopicsResult topics = client.listTopics();
                topics.names().get();
                accessibleIds.add(entry.getKey());
            } catch (Exception e) {
                LOGGER.error("连接Kafka-Admin数据源失败！", e);
                // 连接异常的主键id
                unAccessibleIds.add(entry.getKey());
            } finally {
                adminPool.returnObject(client);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        // 销毁连接池
        for (KafkaAdminPool adminPool : resourceMap.values()) {
            destroy(adminPool);
        }
    }
}
