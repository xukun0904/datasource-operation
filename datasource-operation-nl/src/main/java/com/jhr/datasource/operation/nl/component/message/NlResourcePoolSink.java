package com.jhr.datasource.operation.nl.component.message;

import com.jhr.datasource.operation.common.message.ResourcePoolSink;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author xukun
 * @since 1.0
 */
public interface NlResourcePoolSink extends ResourcePoolSink {
    String ADD_RESOURCE_INPUT = "add-resource-nl-input";
    String UPDATE_RESOURCE_INPUT = "update-resource-nl-input";
    String REMOVE_RESOURCES_INPUT = "remove-resources-nl-input";

    @Input(ADD_RESOURCE_INPUT)
    SubscribableChannel addResourceInput();

    @Input(UPDATE_RESOURCE_INPUT)
    SubscribableChannel updateResourceInput();

    @Input(REMOVE_RESOURCES_INPUT)
    SubscribableChannel removeResourcesInput();
}
