package com.jhr.datasource.operation.hw.component.pool.hdfs;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.component.pool.hdfs.HdfsClient;
import com.jhr.datasource.operation.common.component.pool.hdfs.HdfsFactory;

/**
 * HDFS 工厂类
 *
 * @author xukun
 * @since 1.0
 */
public class HwHdfsFactory extends HdfsFactory {

    public HwHdfsFactory(DatasourceConnectionInfo connectionInfo, LocalFileTools localFileTools) {
        super(connectionInfo, localFileTools);
    }

    @Override
    protected HdfsClient getHdfsClient() {
        return new HwHdfsClient(connectionInfo, localFileTools);
    }
}
