package com.jhr.datasource.operation.nl.component.pool.impl;

import com.jhr.datasource.operation.common.component.pool.AbstractJdbcResourcePool;
import com.jhr.datasource.operation.common.config.JdbcPoolConfig;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.RESOURCE_POOL_BEAN_NAME_JDBC)
@EnableConfigurationProperties(JdbcPoolConfig.class)
public class JdbcResourcePool extends AbstractJdbcResourcePool {
}
