package com.jhr.datasource.operation.nl;

import com.jhr.datasource.operation.nl.component.message.NlResourcePoolSink;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author xukun
 * @since 1.0
 */
@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.jhr.datasource.operation"})
@EnableBinding({NlResourcePoolSink.class})
public class DatasourceOperationNlApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatasourceOperationNlApplication.class, args);
    }
}
