package com.jhr.datasource.operation.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author xukun
 * @since 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "jdbc-pool")
public class JdbcPoolConfig {
    private int initialSize;

    private int minIdle;

    private int maxActive;

    private Properties prop;

    public JdbcPoolConfig() {
        this.initialSize = 5;
        this.minIdle = 5;
        this.maxActive = 20;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public Properties getProp() {
        return prop;
    }

    public void setProp(Properties prop) {
        this.prop = prop;
    }
}
