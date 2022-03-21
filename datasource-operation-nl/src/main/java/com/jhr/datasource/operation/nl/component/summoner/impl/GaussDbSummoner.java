package com.jhr.datasource.operation.nl.component.summoner.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.summoner.JdbcSummoner;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import org.postgresql.core.ServerVersion;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.SUMMONER_BEAN_NAME_GAUSS_DB)
public class GaussDbSummoner extends JdbcSummoner {

    public static final Logger LOGGER = LoggerFactory.getLogger(GaussDbSummoner.class);

    @Override
    public String getJdbcUrl(DatasourceConnectionInfo connectionInfo) {
        return "jdbc:postgresql://" + connectionInfo.getIp() + StrUtil.C_COLON + connectionInfo.getPort() + StrUtil.SLASH + connectionInfo.getDbName();
    }

    @Override
    public List<String> findAllSchemas(DatasourceConnectionInfo connectionInfo) {
        try (Connection con = getActiveConnection(connectionInfo)) {
            List<String> schemaNames = new ArrayList<>();
            String schema = connectionInfo.getSchema();
            if (StrUtil.isBlank(schema) || Constants.DATA_SOURCE_DEFAULT_SCHEMA.contains(schema)) {
                schemaNames.add(Constants.DATA_SOURCE_DEFAULT_SCHEMA);
            }
            String sql = "select schema_name from information_schema.schemata";
            if (StrUtil.isNotBlank(schema)) {
                sql += " where schema_name like ?";
            }
            PreparedStatement statement = con.prepareStatement(sql);
            if (StrUtil.isNotBlank(schema)) {
                statement.setString(1, Constants.PERCENT + schema + Constants.PERCENT);
            }
            ResultSet schemas = statement.executeQuery();
            while (schemas.next()) {
                schemaNames.add(schemas.getString("schema_name"));
            }
            return schemaNames;
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("获取模式名称失败", e);
            ExceptionCast.cast(DatasourceCode.DATASOURCE_FIND_ALL_SCHEMAS_FAILED);
        }
        return Collections.emptyList();
    }

    @Override
    protected ResultSet getIndexInfo(DatasourceConnectionInfo connectionInfo, Connection con) throws SQLException {
        return getIndexInfo(connectionInfo.getSchema(), connectionInfo.getTableName(), false, con);
    }

    /**
     * 重写PgDatabaseMetaData类中的方法，可以查询所有表的index
     */
    public ResultSet getIndexInfo(String schema, String tableName, boolean unique, Connection con) throws SQLException {
        String sql;
        DruidPooledConnection druidPooledConnection = (DruidPooledConnection) con;
        PgConnection connection = (PgConnection) druidPooledConnection.getConnection();
        if (connection.haveMinimumServerVersion(ServerVersion.v8_3)) {
            sql = "SELECT NULL AS TABLE_CAT, n.nspname AS TABLE_SCHEM,   ct.relname AS TABLE_NAME, NOT i.indisunique AS NON_UNIQUE,   NULL AS INDEX_QUALIFIER, ci.relname AS INDEX_NAME,   CASE i.indisclustered     WHEN true THEN 1    ELSE CASE am.amname       WHEN 'hash' THEN 2      ELSE 3    END   END AS TYPE,   (i.keys).n AS ORDINAL_POSITION,   trim(both '\"' from pg_catalog.pg_get_indexdef(ci.oid, (i.keys).n, false)) AS COLUMN_NAME, " + (connection.haveMinimumServerVersion(ServerVersion.v9_6) ? "  CASE am.amname     WHEN 'btree' THEN CASE i.indoption[(i.keys).n - 1] & 1       WHEN 1 THEN 'D'       ELSE 'A'     END     ELSE NULL   END AS ASC_OR_DESC, " : "  CASE am.amcanorder     WHEN true THEN CASE i.indoption[(i.keys).n - 1] & 1       WHEN 1 THEN 'D'       ELSE 'A'     END     ELSE NULL   END AS ASC_OR_DESC, ") + "  ci.reltuples AS CARDINALITY,   ci.relpages AS PAGES,   pg_catalog.pg_get_expr(i.indpred, i.indrelid) AS FILTER_CONDITION FROM pg_catalog.pg_class ct   JOIN pg_catalog.pg_namespace n ON (ct.relnamespace = n.oid)   JOIN (SELECT i.indexrelid, i.indrelid, i.indoption,           i.indisunique, i.indisclustered, i.indpred,           i.indexprs,           information_schema._pg_expandarray(i.indkey) AS keys         FROM pg_catalog.pg_index i) i     ON (ct.oid = i.indrelid)   JOIN pg_catalog.pg_class ci ON (ci.oid = i.indexrelid)   JOIN pg_catalog.pg_am am ON (ci.relam = am.oid) WHERE true ";
            if (schema != null && !schema.isEmpty()) {
                sql = sql + " AND n.nspname = " + this.escapeQuotes(schema, connection);
            }
        } else {
            String select = "SELECT NULL AS TABLE_CAT, n.nspname AS TABLE_SCHEM, ";
            String from = " FROM pg_catalog.pg_namespace n, pg_catalog.pg_class ct, pg_catalog.pg_class ci,  pg_catalog.pg_attribute a, pg_catalog.pg_am am ";
            String where = " AND n.oid = ct.relnamespace ";
            from = from + ", pg_catalog.pg_index i ";
            if (schema != null && !schema.isEmpty()) {
                where = where + " AND n.nspname = " + this.escapeQuotes(schema, connection);
            }

            sql = select + " ct.relname AS TABLE_NAME, NOT i.indisunique AS NON_UNIQUE, NULL AS INDEX_QUALIFIER, ci.relname AS INDEX_NAME,  CASE i.indisclustered  WHEN true THEN " + 1 + " ELSE CASE am.amname  WHEN 'hash' THEN " + 2 + " ELSE " + 3 + " END  END AS TYPE,  a.attnum AS ORDINAL_POSITION,  CASE WHEN i.indexprs IS NULL THEN a.attname  ELSE pg_catalog.pg_get_indexdef(ci.oid,a.attnum,false) END AS COLUMN_NAME,  NULL AS ASC_OR_DESC,  ci.reltuples AS CARDINALITY,  ci.relpages AS PAGES,  pg_catalog.pg_get_expr(i.indpred, i.indrelid) AS FILTER_CONDITION " + from + " WHERE ct.oid=i.indrelid AND ci.oid=i.indexrelid AND a.attrelid=ci.oid AND ci.relam=am.oid " + where;
        }
        // 主要加一个表判断
        if (StrUtil.isNotBlank(tableName)) {
            sql = sql + " AND ct.relname = " + this.escapeQuotes(tableName, connection);
        }
        if (unique) {
            sql = sql + " AND i.indisunique ";
        }

        sql = sql + " ORDER BY NON_UNIQUE, TYPE, INDEX_NAME, ORDINAL_POSITION ";
        return connection.createStatement(1004, 1007).executeQuery(sql);
    }

    private String escapeQuotes(String s, PgConnection connection) throws SQLException {
        StringBuilder sb = new StringBuilder();
        if (!connection.getStandardConformingStrings()) {
            sb.append("E");
        }
        sb.append("'");
        sb.append(connection.escapeString(s));
        sb.append("'");
        return sb.toString();
    }
}
