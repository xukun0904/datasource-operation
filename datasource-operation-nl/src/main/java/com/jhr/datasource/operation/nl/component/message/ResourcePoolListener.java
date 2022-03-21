package com.jhr.datasource.operation.nl.component.message;

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

    @StreamListener(NlResourcePoolSink.ADD_RESOURCE_INPUT)
    @Override
    public void addResource(DatasourceConnectionInfo connectionInfo) {
        super.addResource(connectionInfo);
    }

    @StreamListener(NlResourcePoolSink.UPDATE_RESOURCE_INPUT)
    @Override
    public void updateResource(DatasourceConnectionInfo connectionInfo) {
        super.updateResource(connectionInfo);
    }

    @StreamListener(NlResourcePoolSink.REMOVE_RESOURCES_INPUT)
    @Override
    public void removeResources(Map<Short, List<String>> deleteResourceMap) {
        super.removeResources(deleteResourceMap);
    }
}
