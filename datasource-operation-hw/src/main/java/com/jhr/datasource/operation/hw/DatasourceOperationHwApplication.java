package com.jhr.datasource.operation.hw;

import com.jhr.datasource.operation.hw.message.HwResourcePoolSink;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author xukun
 * @since 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.jhr.datasource.operation"})
@EnableBinding({HwResourcePoolSink.class})
public class DatasourceOperationHwApplication {
    public static void main(String[] args) {
        SpringApplication.run(DatasourceOperationHwApplication.class, args);
    }
}
