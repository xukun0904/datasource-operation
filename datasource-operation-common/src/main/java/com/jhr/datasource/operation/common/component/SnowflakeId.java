package com.jhr.datasource.operation.common.component;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 雪花算法获取分布式唯一键
 *
 * @author xukun
 * @since 1.0
 */
@Component
public class SnowflakeId {
    @Value("${snowflake.worker-id:0}")
    private int workerId;

    @Value("${snowflake.datacenter-id:0}")
    private int datacenterId;

    private final Snowflake snowflake;

    public SnowflakeId() {
        snowflake = IdUtil.createSnowflake(workerId, datacenterId);
    }

    public String nextIdStr() {
        return snowflake.nextIdStr();
    }
}
