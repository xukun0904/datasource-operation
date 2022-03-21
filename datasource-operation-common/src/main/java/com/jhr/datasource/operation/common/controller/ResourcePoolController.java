package com.jhr.datasource.operation.common.controller;

import com.jhr.datasource.operation.api.domain.api.ResourcePoolApi;
import com.jhr.datasource.operation.api.domain.response.DsResponseResult;
import com.jhr.datasource.operation.common.component.pool.ResourcePool;
import com.jhr.datasource.operation.common.component.pool.ResourcePoolStrategy;
import com.jhr.datasource.operation.common.domain.response.ResponseResultBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xukun
 * @since 1.0
 */
@RestController
@RequestMapping("pool")
public class ResourcePoolController extends BaseController implements ResourcePoolApi {

    @Autowired
    private ResourcePoolStrategy poolStrategy;

    @Override
    @GetMapping("generateAllConnectId")
    public DsResponseResult<Map<String, List<String>>> generateAllConnectId() {
        List<String> accessibleIds = new ArrayList<>();
        List<String> unAccessibleIds = new ArrayList<>();
        for (ResourcePool resourcePool : poolStrategy.getResourcePools()) {
            resourcePool.generateAllConnectId(accessibleIds, unAccessibleIds);
        }
        Map<String, List<String>> result = new HashMap<>();
        result.put(ResourcePoolApi.GENERATE_ALL_CONNECT_ID_RESULT_ACCESSIBLE_IDS, accessibleIds);
        result.put(ResourcePoolApi.GENERATE_ALL_CONNECT_ID_RESULT_UNACCESSIBLE_IDS, unAccessibleIds);
        return ResponseResultBuilder.builder().success(result);
    }
}
