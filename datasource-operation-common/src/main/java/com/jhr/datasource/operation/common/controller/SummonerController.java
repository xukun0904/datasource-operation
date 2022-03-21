package com.jhr.datasource.operation.common.controller;

import com.jhr.datasource.operation.api.domain.api.SummonerApi;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.api.domain.response.DsResponseResult;
import com.jhr.datasource.operation.common.component.summoner.DatasourceSummoner;
import com.jhr.datasource.operation.common.component.summoner.DatasourceSummonerStrategy;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.ResponseResultBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author xukun
 * @since 1.0
 */
@RestController
@RequestMapping("summoner")
public class SummonerController extends BaseController implements SummonerApi {

    @Autowired
    private DatasourceSummonerStrategy strategy;

    @Override
    @PostMapping("testConnect")
    public DsResponseResult<Void> testConnectAccessible(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        summoner.testConnectAccessible(connectionInfo);
        return ResponseResultBuilder.builder().success();
    }

    @Override
    @PostMapping("testExistConnectAccessible")
    public DsResponseResult<Void> testExistConnectAccessible(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        summoner.testExistConnectAccessible(connectionInfo);
        return ResponseResultBuilder.builder().success();
    }

    @Override
    @PostMapping("findAllTables")
    public DsResponseResult<List<Map<String, Object>>> findAllTables(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        List<Map<String, Object>> allTables = summoner.findAllTables(connectionInfo);
        return ResponseResultBuilder.builder().success(allTables);
    }

    @Override
    @PostMapping("findAllIndexInfos")
    public DsResponseResult<List<Map<String, Object>>> findAllIndexInfos(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        List<Map<String, Object>> allTables = summoner.findAllIndexInfos(connectionInfo);
        return ResponseResultBuilder.builder().success(allTables);
    }

    @Override
    @PostMapping("findAllImportedKeys")
    public DsResponseResult<List<Map<String, Object>>> findAllImportedKeys(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        List<Map<String, Object>> allTables = summoner.findAllImportedKeys(connectionInfo);
        return ResponseResultBuilder.builder().success(allTables);
    }

    @Override
    @PostMapping("findAllTableNames")
    public DsResponseResult<Collection<String>> findAllTableNames(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        Collection<String> tableNames = summoner.findAllTableNames(connectionInfo);
        return ResponseResultBuilder.builder().success(tableNames);
    }

    @Override
    @PostMapping("findAllSchemas")
    public DsResponseResult<List<String>> findAllSchemas(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        List<String> schemas = summoner.findAllSchemas(connectionInfo);
        return ResponseResultBuilder.builder().success(schemas);
    }

    @Override
    @PostMapping("findAllColumns")
    public DsResponseResult<List<Map<String, Object>>> findAllColumns(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        List<Map<String, Object>> columns = summoner.findAllColumns(connectionInfo);
        return ResponseResultBuilder.builder().success(columns);
    }

    @Override
    @PostMapping("findAllMetaData")
    public DsResponseResult<List<List<Map<String, Object>>>> findAllMetaData(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        List<List<Map<String, Object>>> allMetaData = summoner.findAllMetaData(connectionInfo);
        return ResponseResultBuilder.builder().success(allMetaData);
    }

    @Override
    @PostMapping("executeSqlMap")
    public DsResponseResult<Void> executeSqlMap(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        summoner.executeSqlMap(connectionInfo);
        return ResponseResultBuilder.builder().success();
    }

    @Override
    @PostMapping("batchExecuteSqlMap")
    public DsResponseResult<Void> batchExecuteSqlMap(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        summoner.batchExecuteSqlMap(connectionInfo);
        return ResponseResultBuilder.builder().success();
    }

    @Override
    @PostMapping("queryForMap")
    public DsResponseResult<Map<String, Object>> queryForMap(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        Map<String, Object> data = summoner.queryForMap(connectionInfo);
        return ResponseResultBuilder.builder().success(data);
    }

    @Override
    @PostMapping("queryForList")
    public DsResponseResult<List<Map<String, Object>>> queryForList(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        List<Map<String, Object>> data = summoner.queryForList(connectionInfo);
        return ResponseResultBuilder.builder().success(data);
    }

    @Override
    @PostMapping("getFieldNumber")
    public DsResponseResult<Integer> getFieldNumber(@RequestBody DatasourceConnectionInfo connectionInfo) {
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        int fieldNumber = summoner.getFieldNumber(connectionInfo);
        return ResponseResultBuilder.builder().success(fieldNumber);
    }
}
