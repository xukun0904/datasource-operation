package com.jhr.datasource.operation.common.config;

import com.jhr.datasource.operation.common.component.pool.hdfs.HdfsClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Component;

/**
 * HDFS 连接池配置
 *
 * @author xukun
 * @since 1.0
 */
@Component
public class HdfsPoolConfig extends GenericObjectPoolConfig<HdfsClient> {

    public HdfsPoolConfig(HdfsPoolProperties hdfsProperties) {
        this.setMinIdle(hdfsProperties.getMinIdle());
        this.setMaxIdle(hdfsProperties.getMaxIdle());
        this.setMaxTotal(hdfsProperties.getMaxTotal());
        this.setMaxWaitMillis(hdfsProperties.getMaxWaitMillis());
        this.setJmxEnabled(hdfsProperties.isJmxEnabled());
        this.setTestWhileIdle(hdfsProperties.isTestWhileIdle());
        this.setMinEvictableIdleTimeMillis(hdfsProperties.getMinEvictableIdleTimeMillis());
        this.setTimeBetweenEvictionRunsMillis(hdfsProperties.getTimeBetweenEvictionRunsMillis());
        this.setNumTestsPerEvictionRun(hdfsProperties.getNumTestsPerEvictionRun());
    }
}
