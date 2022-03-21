package com.jhr.datasource.operation.common.component.summoner;

import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 策略模式
 *
 * @author xukun
 * @since 1.0
 */
@Component
public class DatasourceSummonerStrategy {
    @Autowired
    private Map<String, DatasourceSummoner> summonerMap;

    public DatasourceSummoner getSummonerByBeanName(String beanName) {
        DatasourceSummoner summoner = summonerMap.get(beanName);
        if (summoner == null) {
            ExceptionCast.cast(DatasourceCode.DATASOURCE_NOT_SUPPORT);
        }
        return summoner;
    }
}
