package com.jhr.datasource.operation.nl.component.config;

import cn.hutool.extra.ftp.AbstractFtp;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Component;

/**
 * @author xukun
 * @since 1.0
 */
@Component
public class FtpPoolConfig extends GenericObjectPoolConfig<AbstractFtp> {

    public FtpPoolConfig(FtpPoolProperties ftpPoolProperties) {
        this.setMinIdle(ftpPoolProperties.getMinIdle());
        this.setMaxIdle(ftpPoolProperties.getMaxIdle());
        this.setMaxTotal(ftpPoolProperties.getMaxTotal());
        this.setMaxWaitMillis(ftpPoolProperties.getMaxWaitMillis());
        this.setJmxEnabled(ftpPoolProperties.isJmxEnabled());
        this.setTestWhileIdle(ftpPoolProperties.isTestWhileIdle());
        this.setMinEvictableIdleTimeMillis(ftpPoolProperties.getMinEvictableIdleTimeMillis());
        this.setTimeBetweenEvictionRunsMillis(ftpPoolProperties.getTimeBetweenEvictionRunsMillis());
        this.setNumTestsPerEvictionRun(ftpPoolProperties.getNumTestsPerEvictionRun());
    }
}
