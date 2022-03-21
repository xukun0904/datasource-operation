package com.jhr.datasource.operation.common.message;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author xukun
 * @since 1.0
 */
public interface ResourcePoolSink {

    String CLEAN_UP_INPUT = "clean-up-input";

    @Input(CLEAN_UP_INPUT)
    SubscribableChannel cleanUpInput();
}
