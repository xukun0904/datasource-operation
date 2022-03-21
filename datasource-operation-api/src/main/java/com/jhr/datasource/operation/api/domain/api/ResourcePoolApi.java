package com.jhr.datasource.operation.api.domain.api;

import com.jhr.datasource.operation.api.domain.response.DsResponseResult;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * path = @RequestMapping("pool")
 *
 * @author xukun
 * @since 1.0
 */
public interface ResourcePoolApi {

    String GENERATE_ALL_CONNECT_ID_RESULT_ACCESSIBLE_IDS = "accessibleIds";

    String GENERATE_ALL_CONNECT_ID_RESULT_UNACCESSIBLE_IDS = "unAccessibleIds";

    @GetMapping("generateAllConnectId")
    DsResponseResult<Map<String, List<String>>> generateAllConnectId();
}
