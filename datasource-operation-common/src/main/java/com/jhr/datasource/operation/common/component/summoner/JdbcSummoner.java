package com.jhr.datasource.operation.common.component.summoner;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.pool.AbstractJdbcResourcePool;
import com.jhr.datasource.operation.common.component.pool.ResourcePool;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xukun
 * @since 1.0
 */
public abstract class JdbcSummoner extends DatasourceSummoner {

    protected static final Logger LOGGER = LoggerFactory.getLogger(JdbcSummoner.class);

    /**
     * 根据连接信息获取连接，由子类实现
     *
     * @param connectionInfo 连接信息
     * @return 连接
     */
    protected Connection getConnection(DatasourceConnectionInfo connectionInfo) throws ClassNotFoundException, SQLException {
        Class.forName(Constants.DATASOURCE_DRIVER_MAP.get(connectionInfo.getDsourceType()));
        Properties prop = new Properties();
        String username = connectionInfo.getUsername();
        String password = connectionInfo.getPassword();
        if (StrUtil.isNotBlank(username)) {
            prop.put("user", username);
        }
        if (StrUtil.isNotBlank(password)) {
            prop.put("password", password);
        }
        putConnectionProperties(prop);
        return DriverManager.getConnection(getJdbcUrl(connectionInfo), prop);
    }

    protected void putConnectionProperties(Properties prop) {
    }

    /**
     * 根据数据源id获取已注册的连接
     *
     * @param connectionInfo 数据源信息
     * @return 可用连接
     */
    protected Connection getExistConnection(DatasourceConnectionInfo connectionInfo) throws SQLException {
        ResourcePool resourcePool = poolStrategy.getResourcePoolByBeanName(Constants.DATASOURCE_POOL_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        DataSource dataSource = resourcePool.get(connectionInfo);
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        ExceptionCast.cast(DatasourceCode.DATASOURCE_NOT_EXIST);
        return null;
    }

    /**
     * 获取可用连接，若没有则新建连接
     *
     * @param connectionInfo 连接信息
     * @return 可用连接
     */
    protected Connection getActiveConnection(DatasourceConnectionInfo connectionInfo) throws SQLException, ClassNotFoundException {
        if (StrUtil.isNotBlank(connectionInfo.getId())) {
            return getExistConnection(connectionInfo);
        }
        return getConnection(connectionInfo);
    }

    /**
     * 测试连接是否正常
     *
     * @param connectionInfo 连接信息
     */
    @Override
    public void testConnectAccessible(DatasourceConnectionInfo connectionInfo) {
        try (Connection ignored = getConnection(connectionInfo)) {
            LOGGER.debug("数据源连接正常！");
        } catch (SQLException | ClassNotFoundException e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        }
    }

    /**
     * 测试连接是否正常
     *
     * @param connectionInfo 连接信息
     */
    @Override
    public void testExistConnectAccessible(DatasourceConnectionInfo connectionInfo) {
        try (Connection ignored = getExistConnection(connectionInfo)) {
            LOGGER.debug("数据源连接正常！");
        } catch (SQLException e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(), e.getMessage()));
        }
    }

    @Override
    public List<Map<String, Object>> findAllTables(DatasourceConnectionInfo connectionInfo) {
        try (Connection con = getActiveConnection(connectionInfo)) {
            return findAllTables(connectionInfo, con);
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("查找表信息失败！", e);
            ExceptionCast.cast(DatasourceCode.DATASOURCE_FIND_ALL_TABLES_FAILED);
        }
        return Collections.emptyList();
    }

    private void setDefaultConValue(DatasourceConnectionInfo connectionInfo, Connection con) throws SQLException {
        connectionInfo.setDbName(StrUtil.isNotBlank(connectionInfo.getDbName()) ?
                connectionInfo.getDbName() : con.getCatalog());
        connectionInfo.setSchema(StrUtil.isNotBlank(connectionInfo.getSchema()) ?
                connectionInfo.getSchema() : con.getSchema());
    }

    @Override
    public List<Map<String, Object>> findAllColumns(DatasourceConnectionInfo connectionInfo) {
        try (Connection con = getActiveConnection(connectionInfo)) {
            return findAllColumns(connectionInfo, con);
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("查找字段信息失败！", e);
            ExceptionCast.cast(DatasourceCode.DATASOURCE_FIND_ALL_COLUMNS_FAILED);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> findAllIndexInfos(DatasourceConnectionInfo connectionInfo) {
        try (Connection con = getActiveConnection(connectionInfo)) {
            return findAllIndexInfos(connectionInfo, con);
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("查找索引信息失败！", e);
            ExceptionCast.cast(DatasourceCode.DATASOURCE_FIND_ALL_INDEXES_FAILED);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> findAllImportedKeys(DatasourceConnectionInfo connectionInfo) {
        try (Connection con = getActiveConnection(connectionInfo)) {
            return findAllImportedKeys(connectionInfo, con);
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("查找外键信息失败！", e);
            ExceptionCast.cast(DatasourceCode.DATASOURCE_FIND_ALL_IMPORTED_KEYS_FAILED);
        }
        return Collections.emptyList();
    }

    @Override
    public List<List<Map<String, Object>>> findAllMetaData(DatasourceConnectionInfo connectionInfo) {
        try (Connection con = getActiveConnection(connectionInfo)) {
            List<List<Map<String, Object>>> metas = new ArrayList<>();
            metas.add(findAllTables(connectionInfo, con));
            metas.add(findAllPrimaryKeys(connectionInfo, con));
            metas.add(findAllColumns(connectionInfo, con));
            metas.add(findAllImportedKeys(connectionInfo, con));
            metas.add(findAllIndexInfos(connectionInfo, con));
            return metas;
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("查找数据库元信息失败！", e);
            ExceptionCast.cast(DatasourceCode.DATASOURCE_FIND_ALL_METADATA_FAILED);
        }
        return Collections.emptyList();
    }

    protected List<Map<String, Object>> findAllIndexInfos(DatasourceConnectionInfo connectionInfo, Connection con) throws SQLException {
        assert con != null;
        setDefaultConValue(connectionInfo, con);
        ResultSet indexInfo = getIndexInfo(connectionInfo, con);
        return convertMap(indexInfo);
    }

    protected ResultSet getIndexInfo(DatasourceConnectionInfo connectionInfo, Connection con) throws SQLException {
        return con.getMetaData().getIndexInfo(connectionInfo.getDbName(), connectionInfo.getSchema(), connectionInfo.getTableName(), false, true);
    }

    protected List<Map<String, Object>> findAllImportedKeys(DatasourceConnectionInfo connectionInfo, Connection con) throws SQLException {
        assert con != null;
        setDefaultConValue(connectionInfo, con);
        DatabaseMetaData databaseMetaData = con.getMetaData();
        ResultSet importedKeys = databaseMetaData.getImportedKeys(connectionInfo.getDbName(), connectionInfo.getSchema(), connectionInfo.getTableName());
        return convertMap(importedKeys);
    }

    private List<Map<String, Object>> findAllColumns(DatasourceConnectionInfo connectionInfo, Connection con) throws SQLException {
        assert con != null;
        setDefaultConValue(connectionInfo, con);
        DatabaseMetaData databaseMetaData = con.getMetaData();
        ResultSet columns = databaseMetaData.getColumns(connectionInfo.getDbName(), connectionInfo.getSchema(), connectionInfo.getTableName(), null);
        return convertMap(columns);
    }

    protected List<Map<String, Object>> findAllPrimaryKeys(DatasourceConnectionInfo connectionInfo, Connection con) throws SQLException {
        assert con != null;
        setDefaultConValue(connectionInfo, con);
        DatabaseMetaData databaseMetaData = con.getMetaData();
        ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(connectionInfo.getDbName(), connectionInfo.getSchema(), connectionInfo.getTableName());
        return convertMap(primaryKeys);
    }

    @Override
    public Collection<String> findAllTableNames(DatasourceConnectionInfo connectionInfo) {
        String tableName = connectionInfo.getTableName();
        tableName = StrUtil.isNotBlank(tableName) ? (Constants.PERCENT + tableName + Constants.PERCENT) : Constants.PERCENT;
        connectionInfo.setTableName(tableName);
        List<Map<String, Object>> tables = findAllTables(connectionInfo);
        if (CollectionUtil.isNotEmpty(tables)) {
            return tables.stream().map(t -> MapUtil.getStr(t, "table_name")).collect(Collectors.toList());
        }
        return null;
    }

    private List<Map<String, Object>> findAllTables(DatasourceConnectionInfo connectionInfo, Connection con) throws SQLException {
        assert con != null;
        setDefaultConValue(connectionInfo, con);
        DatabaseMetaData databaseMetaData = con.getMetaData();
        ResultSet tables = databaseMetaData.getTables(connectionInfo.getDbName(), connectionInfo.getSchema(), connectionInfo.getTableName(), new String[]{"TABLE"});
        return convertMap(tables);
    }

    protected List<Map<String, Object>> convertMap(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> allRows = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnLabel = metaData.getColumnLabel(i);
                row.put(columnLabel.toLowerCase(), resultSet.getObject(columnLabel));
            }
            allRows.add(row);
        }
        return allRows;
    }

    protected JdbcTemplate getJdbcTemplate(DatasourceConnectionInfo connectionInfo) {
        AbstractJdbcResourcePool resourcePool = (AbstractJdbcResourcePool) poolStrategy.getResourcePoolByBeanName(Constants.DATASOURCE_POOL_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        JdbcTemplate jdbcTemplate = resourcePool.getJdbcTemplate(connectionInfo);
        if (jdbcTemplate == null) {
            // 数据源不存在
            ExceptionCast.cast(DatasourceCode.DATASOURCE_NOT_EXIST);
        }
        return jdbcTemplate;
    }

    @Override
    public void executeSqlMap(DatasourceConnectionInfo connectionInfo) {
        try {
            Map<String, Object[]> sqlMap = connectionInfo.getSqlMap();
            if (CollectionUtil.isNotEmpty(sqlMap)) {
                JdbcTemplate jdbcTemplate = getJdbcTemplate(connectionInfo);
                String sql = CollectionUtil.getFirst(sqlMap.keySet());
                jdbcTemplate.execute(sql);
            }
        } catch (Exception e) {
            LOGGER.error("执行SQL失败！", e);
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_EXECUTE_SQLS_ERROR.code(),
                    DatasourceCode.DATASOURCE_EXECUTE_SQLS_ERROR.message() + e.getMessage()));
        }
    }

    @Override
    public void batchExecuteSqlMap(DatasourceConnectionInfo connectionInfo) {
        Map<String, Object[]> sqlMap = connectionInfo.getSqlMap();
        if (CollectionUtil.isNotEmpty(sqlMap)) {
            try (Connection con = getActiveConnection(connectionInfo)) {
                assert con != null;
                // 创建表
                Statement statement = con.createStatement();
                for (String sql : sqlMap.keySet()) {
                    statement.addBatch(sql);
                }
                statement.executeBatch();
            } catch (SQLException | ClassNotFoundException e) {
                LOGGER.error("执行SQL失败！", e);
                ExceptionCast.cast(DatasourceCode.DATASOURCE_BATCH_EXECUTE_SQL_MAP_FAILED);
            }
        }
    }

    @Override
    public Map<String, Object> queryForMap(DatasourceConnectionInfo connectionInfo) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(connectionInfo);
        Map.Entry<String, Object[]> sqlEntry = CollectionUtil.getFirst(connectionInfo.getSqlMap().entrySet());
        if (sqlEntry != null) {
            return queryForMap(jdbcTemplate, sqlEntry.getKey(), sqlEntry.getValue());
        }
        return Collections.emptyMap();
    }

    protected <T> T queryForMap(JdbcTemplate jdbcTemplate, String sql, @Nullable Object... args) throws DataAccessException {
        List<T> results = (List) jdbcTemplate.query(sql, args, (new RowMapperResultSetExtractor(new ColumnMapRowMapper(), 1)));
        if (CollectionUtils.isEmpty(results)) {
            return null;
        } else if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, results.size());
        } else {
            return results.iterator().next();
        }
    }

    @Override
    public List<Map<String, Object>> queryForList(DatasourceConnectionInfo connectionInfo) {
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(connectionInfo);
            Map.Entry<String, Object[]> sqlEntry = CollectionUtil.getFirst(connectionInfo.getSqlMap().entrySet());
            if (sqlEntry != null) {
                return jdbcTemplate.queryForList(sqlEntry.getKey(), sqlEntry.getValue());
            }
        } catch (Exception e) {
            LOGGER.error("执行SQL失败！", e);
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_EXECUTE_SQLS_ERROR.code(),
                    DatasourceCode.DATASOURCE_EXECUTE_SQLS_ERROR.message() + e.getMessage()));
        }
        return Collections.emptyList();
    }

    @Override
    public Long getTotal(DatasourceConnectionInfo connectionInfo) {
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(connectionInfo);
            Map.Entry<String, Object[]> sqlEntry = CollectionUtil.getFirst(connectionInfo.getSqlMap().entrySet());
            if (sqlEntry != null) {
                Map<String, Number> resultMap = queryForMap(jdbcTemplate, sqlEntry.getKey(), sqlEntry.getValue());
                return CollectionUtil.getFirst(resultMap.values()).longValue();
            }
        } catch (Exception e) {
            LOGGER.error("执行SQL失败！", e);
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_EXECUTE_SQLS_ERROR.code(),
                    DatasourceCode.DATASOURCE_EXECUTE_SQLS_ERROR.message() + e.getMessage()));
        }
        return 0L;
    }
}
