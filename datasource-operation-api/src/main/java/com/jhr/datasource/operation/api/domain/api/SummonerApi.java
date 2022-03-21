package com.jhr.datasource.operation.api.domain.api;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.api.domain.response.DsResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * path = @RequestMapping("summoner")
 *
 * @author xukun
 * @since 1.0
 */
public interface SummonerApi {

    @PostMapping("testConnect")
    DsResponseResult<Void> testConnectAccessible(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("testExistConnectAccessible")
    DsResponseResult<Void> testExistConnectAccessible(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("findAllTables")
    DsResponseResult<List<Map<String, Object>>> findAllTables(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("findAllIndexInfos")
    DsResponseResult<List<Map<String, Object>>> findAllIndexInfos(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("findAllImportedKeys")
    DsResponseResult<List<Map<String, Object>>> findAllImportedKeys(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("findAllTableNames")
    DsResponseResult<Collection<String>> findAllTableNames(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("findAllSchemas")
    DsResponseResult<List<String>> findAllSchemas(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("findAllColumns")
    DsResponseResult<List<Map<String, Object>>> findAllColumns(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("findAllMetaData")
    DsResponseResult<List<List<Map<String, Object>>>> findAllMetaData(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("executeSqlMap")
    DsResponseResult<Void> executeSqlMap(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("batchExecuteSqlMap")
    DsResponseResult<Void> batchExecuteSqlMap(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("queryForMap")
    DsResponseResult<Map<String, Object>> queryForMap(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("queryForList")
    DsResponseResult<List<Map<String, Object>>> queryForList(@RequestBody DatasourceConnectionInfo connectionInfo);

    @PostMapping("getFieldNumber")
    DsResponseResult<Integer> getFieldNumber(@RequestBody DatasourceConnectionInfo connectionInfo);
}
