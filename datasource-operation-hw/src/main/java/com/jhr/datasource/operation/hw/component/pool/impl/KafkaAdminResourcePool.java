package com.jhr.datasource.operation.hw.component.pool.impl;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.component.pool.AbstractKafkaAdminResourcePool;
import com.jhr.datasource.operation.common.component.pool.kafka.KafkaAdminFactory;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.hw.component.pool.kafka.HwKafkaAdminFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.RESOURCE_POOL_BEAN_NAME_KAFKA)
public class KafkaAdminResourcePool extends AbstractKafkaAdminResourcePool {

    @Autowired
    private LocalFileTools localFileTools;

    @Override
    protected KafkaAdminFactory getKafkaAdminFactory(DatasourceConnectionInfo connectionInfo) {
        return new HwKafkaAdminFactory(connectionInfo, localFileTools);
    }
}
