package com.jhr.datasource.operation.common.component.summoner;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.component.SnowflakeId;
import com.jhr.datasource.operation.common.component.pool.ResourcePool;
import com.jhr.datasource.operation.common.component.pool.kafka.KafkaAdminPool;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author xukun
 * @since 1.0
 */
public abstract class AbstractKafkaSummoner extends DatasourceSummoner {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractKafkaSummoner.class);

    @Autowired
    protected LocalFileTools localFileTools;

    @Autowired
    private SnowflakeId snowflakeId;

    protected AdminClient getAdminClient(DatasourceConnectionInfo connectionInfo) {
        try {
            if (Constants.DATA_SOURCE_AUTH_TYPE_KERBEROS.equals(connectionInfo.getAuthType())) {
                authenticate(connectionInfo);
            }
            Properties props = DatasourceOperationUtils.getKafkaProperties(connectionInfo);
            return AdminClient.create(props);
        } catch (IOException e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        } finally {
            releaseAuthenticateEnv();
        }
        return null;
    }

    protected void authenticate(DatasourceConnectionInfo connectionInfo) throws IOException {
    }

    protected void releaseAuthenticateEnv() {
    }

    @Override
    public void testConnectAccessible(DatasourceConnectionInfo connectionInfo) {
        try (AdminClient client = getAdminClient(connectionInfo)) {
            // 测试连接是否正常
            ListTopicsResult topics = client.listTopics();
            topics.names().get();
        } catch (ExecutionException | InterruptedException e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        }
    }

    /**
     * 测试连接是否正常
     *
     * @param connectionInfo 连接信息
     */
    @Override
    public void testExistConnectAccessible(DatasourceConnectionInfo connectionInfo) {
        KafkaAdminPool adminPool = getExistAdminPool(connectionInfo);
        AdminClient client = null;
        try {
            client = adminPool.borrowObject();
            ListTopicsResult topics = client.listTopics();
            topics.names().get();
        } catch (Exception e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        } finally {
            adminPool.returnObject(client);
        }
    }

    private KafkaAdminPool getExistAdminPool(DatasourceConnectionInfo connectionInfo) {
        ResourcePool resourcePool = poolStrategy.getResourcePoolByBeanName(Constants.DATASOURCE_POOL_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        return resourcePool.get(connectionInfo);
    }

    @Override
    public Collection<String> findAllTableNames(DatasourceConnectionInfo connectionInfo) {
        AdminClient adminClient = null;
        KafkaAdminPool adminPool = null;
        try {
            if (StrUtil.isBlank(connectionInfo.getId())) {
                adminClient = getAdminClient(connectionInfo);
            } else {
                adminPool = getExistAdminPool(connectionInfo);
                adminClient = adminPool.borrowObject();
            }
            ListTopicsResult topics = adminClient.listTopics();
            if (topics != null) {
                KafkaFuture<Set<String>> names = topics.names();
                if (names != null) {
                    return names.get();
                }
            }
        } catch (Exception e) {
            LOGGER.error("查找主题名称失败！", e);
            ExceptionCast.cast(DatasourceCode.DATASOURCE_FIND_ALL_TABLE_NAMES_FAILED);
        } finally {
            if (adminPool != null) {
                adminPool.returnObject(adminClient);
            }
        }
        return Collections.emptySet();
    }


    @Override
    public int getFieldNumber(DatasourceConnectionInfo connectionInfo) {
        KafkaConsumer<String, String> consumer = null;
        int fieldNumber = 0;
        String tableName = connectionInfo.getTableName();
        try {
            if (Constants.DATA_SOURCE_AUTH_TYPE_KERBEROS.equals(connectionInfo.getAuthType())) {
                authenticate(connectionInfo);
            }
            Properties props = DatasourceOperationUtils.getKafkaProperties(connectionInfo);
            // 设置消费组id
            String groupId = snowflakeId.nextIdStr();
            props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            // 超时时间10s，不能小于session_time_out 10s，group最小时间10s
            props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 10001);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            consumer = new KafkaConsumer<>(props);
            // 判断连接是否成功，否则服务器关闭会一直卡在poll
            consumer.listTopics();
            // 订阅主题
            consumer.subscribe(Collections.singletonList(tableName));
            // 5s超时
            long delayTime = Instant.now().plusSeconds(5).toEpochMilli();
            String value = null;
            while (true) {
                long milli = Instant.now().toEpochMilli();
                if (milli >= delayTime) {
                    ExceptionCast.cast(DatasourceCode.DATASOURCE_GET_TOPIC_CONTENT_TIMEOUT);
                    break;
                }
                // 获取结果集
                ConsumerRecords<String, String> records = consumer.poll(100);
                Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
                if (iterator.hasNext()) {
                    // 获取一条内容
                    ConsumerRecord<String, String> first = records.iterator().next();
                    value = first.value();
                    break;
                }
            }
            if (StrUtil.isNotBlank(value)) {
                String advanceParamConf = connectionInfo.getAdvanceParamConf();
                JSONObject conf = JSONUtil.parseObj(advanceParamConf);
                String delimiter = conf.getStr("delimiter", StrUtil.COMMA);
                // 获取内容分割后的数量
                fieldNumber = StrUtil.split(value, delimiter).length;
            }

        } catch (IOException e) {
            LOGGER.error("获取字段数量失败", e);
            ExceptionCast.cast(DatasourceCode.DATASOURCE_GET_FIELD_NUMBER_FAILED);
        } finally {
            releaseAuthenticateEnv();
            if (consumer != null) {
                consumer.close();
            }
        }
        return fieldNumber;
    }
}
