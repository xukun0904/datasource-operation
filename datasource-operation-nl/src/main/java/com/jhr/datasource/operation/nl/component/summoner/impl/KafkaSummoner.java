package com.jhr.datasource.operation.nl.component.summoner.impl;

import com.jhr.datasource.operation.common.component.summoner.AbstractKafkaSummoner;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import org.springframework.stereotype.Component;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.SUMMONER_BEAN_NAME_KAFKA)
public class KafkaSummoner extends AbstractKafkaSummoner {
}
