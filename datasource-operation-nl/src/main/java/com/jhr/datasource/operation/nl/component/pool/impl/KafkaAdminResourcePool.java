package com.jhr.datasource.operation.nl.component.pool.impl;

import com.jhr.datasource.operation.common.component.pool.AbstractKafkaAdminResourcePool;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import org.springframework.stereotype.Component;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.RESOURCE_POOL_BEAN_NAME_KAFKA)
public class KafkaAdminResourcePool extends AbstractKafkaAdminResourcePool {
}
