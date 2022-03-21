package com.jhr.datasource.operation.hw.component.pool.impl;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.pool.AbstractHdfsResourcePool;
import com.jhr.datasource.operation.common.component.pool.hdfs.HdfsFactory;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.hw.component.pool.hdfs.HwHdfsFactory;
import org.springframework.stereotype.Component;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.RESOURCE_POOL_BEAN_NAME_HDFS)
public class HdfsResourcePool extends AbstractHdfsResourcePool {

    @Override
    protected HdfsFactory getHdfsFactory(DatasourceConnectionInfo connectionInfo) {
        return new HwHdfsFactory(connectionInfo, localFileTools);
    }
}
