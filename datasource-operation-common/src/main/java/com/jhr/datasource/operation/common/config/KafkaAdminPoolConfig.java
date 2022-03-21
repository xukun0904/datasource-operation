package com.jhr.datasource.operation.common.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.stereotype.Component;

/**
 * HDFS 连接池配置
 *
 * @author xukun
 * @since 1.0
 */
@Component
public class KafkaAdminPoolConfig extends GenericObjectPoolConfig<AdminClient> {

    public KafkaAdminPoolConfig(KafkaAdminPoolProperties kafkaPoolProperties) {
        this.setMinIdle(kafkaPoolProperties.getMinIdle());
        this.setMaxIdle(kafkaPoolProperties.getMaxIdle());
        this.setMaxTotal(kafkaPoolProperties.getMaxTotal());
        this.setMaxWaitMillis(kafkaPoolProperties.getMaxWaitMillis());
        this.setJmxEnabled(kafkaPoolProperties.isJmxEnabled());
        this.setTestWhileIdle(kafkaPoolProperties.isTestWhileIdle());
        this.setMinEvictableIdleTimeMillis(kafkaPoolProperties.getMinEvictableIdleTimeMillis());
        this.setTimeBetweenEvictionRunsMillis(kafkaPoolProperties.getTimeBetweenEvictionRunsMillis());
        this.setNumTestsPerEvictionRun(kafkaPoolProperties.getNumTestsPerEvictionRun());
    }
}
