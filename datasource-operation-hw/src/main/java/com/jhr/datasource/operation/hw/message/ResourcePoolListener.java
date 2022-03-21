package com.jhr.datasource.operation.hw.message;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.message.AbstractResourcePoolListener;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author xukun
 * @since 1.0
 */
@Component
public class ResourcePoolListener extends AbstractResourcePoolListener {

    @StreamListener(HwResourcePoolSink.ADD_RESOURCE_INPUT)
    @Override
    public void addResource(DatasourceConnectionInfo connectionInfo) {
        super.addResource(connectionInfo);
    }

    @StreamListener(HwResourcePoolSink.UPDATE_RESOURCE_INPUT)
    @Override
    public void updateResource(DatasourceConnectionInfo connectionInfo) {
        super.updateResource(connectionInfo);
    }

    @StreamListener(HwResourcePoolSink.REMOVE_RESOURCES_INPUT)
    @Override
    public void removeResources(Map<Short, List<String>> deleteResourceMap) {
        super.removeResources(deleteResourceMap);
    }
}
