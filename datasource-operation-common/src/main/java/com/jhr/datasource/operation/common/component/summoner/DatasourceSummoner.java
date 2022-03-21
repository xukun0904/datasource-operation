package com.jhr.datasource.operation.common.component.summoner;

import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.pool.ResourcePoolStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 数据源操作类
 *
 * @author xukun
 * @since 1.0
 */
public abstract class DatasourceSummoner {

    @Autowired
    protected ResourcePoolStrategy poolStrategy;

    public void testConnectAccessible(DatasourceConnectionInfo connectionInfo) {
    }

    /**
     * 测试连接是否正常
     *
     * @param connectionInfo 连接信息
     */
    public void testExistConnectAccessible(DatasourceConnectionInfo connectionInfo) {
    }

    public String getJdbcUrl(DatasourceConnectionInfo connectionInfo) {
        return null;
    }

    public List<Map<String, Object>> findAllTables(DatasourceConnectionInfo connectionInfo) {
        return Collections.emptyList();
    }

    public List<String> findAllSchemas(DatasourceConnectionInfo connectionInfo) {
        return Collections.emptyList();
    }

    public List<Map<String, Object>> findAllColumns(DatasourceConnectionInfo connectionInfo) {
        return Collections.emptyList();
    }

    public List<Map<String, Object>> findAllIndexInfos(DatasourceConnectionInfo connectionInfo) {
        return Collections.emptyList();
    }

    public List<Map<String, Object>> findAllImportedKeys(DatasourceConnectionInfo connectionInfo) {
        return Collections.emptyList();
    }

    public List<List<Map<String, Object>>> findAllMetaData(DatasourceConnectionInfo connectionInfo) {
        return Collections.emptyList();
    }

    public void executeSqlMap(DatasourceConnectionInfo connectionInfo) {
    }

    public Map<String, Object> queryForMap(DatasourceConnectionInfo connectionInfo) {
        return Collections.emptyMap();
    }

    public List<Map<String, Object>> queryForList(DatasourceConnectionInfo connectionInfo) {
        return Collections.emptyList();
    }

    public int getFieldNumber(DatasourceConnectionInfo connectionInfo) {
        return 0;
    }

    public Collection<String> findAllTableNames(DatasourceConnectionInfo connectionInfo) {
        return Collections.emptyList();
    }

    public void batchExecuteSqlMap(DatasourceConnectionInfo connectionInfo) {
    }

    public Long getTotal(DatasourceConnectionInfo connectionInfo) {
        return 0L;
    }
}