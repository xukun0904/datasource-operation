package com.jhr.datasource.operation.nl.component.summoner.impl;

import cn.hutool.core.util.StrUtil;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.summoner.JdbcSummoner;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * MYSQL元信息获取类
 *
 * @author xukun
 * @since 1.0
 */
@Component(Constants.SUMMONER_BEAN_NAME_MYSQL)
public class MysqlSummoner extends JdbcSummoner {

    @Override
    public String getJdbcUrl(DatasourceConnectionInfo connectionInfo) {
        return "jdbc:mysql://" + connectionInfo.getIp() + StrUtil.C_COLON + connectionInfo.getPort() + StrUtil.SLASH + connectionInfo.getDbName();
    }

    @Override
    protected List<Map<String, Object>> findAllImportedKeys(DatasourceConnectionInfo connectionInfo, Connection con) throws SQLException {
        String sql = "select k.COLUMN_NAME as pkcolumn_name,k.TABLE_NAME as pktable_name,"
                + "k.CONSTRAINT_NAME as fk_name,k.REFERENCED_COLUMN_NAME as fkcolumn_name,"
                + "k.REFERENCED_TABLE_NAME as fktable_name,r.UPDATE_RULE as update_rule,r.DELETE_RULE as delete_rule"
                + " from information_schema.TABLE_CONSTRAINTS t join information_schema.KEY_COLUMN_USAGE k"
                + " using (CONSTRAINT_NAME,TABLE_SCHEMA,TABLE_NAME) join information_schema.REFERENTIAL_CONSTRAINTS r"
                + " using (TABLE_NAME) where t.CONSTRAINT_TYPE='FOREIGN KEY' and t.TABLE_SCHEMA=?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, connectionInfo.getDbName());
        ResultSet importedKeys = preparedStatement.executeQuery();
        return convertMap(importedKeys);
    }

    @Override
    protected List<Map<String, Object>> findAllIndexInfos(DatasourceConnectionInfo connectionInfo, Connection con) throws SQLException {
        String sql = "select a.TABLE_NAME as table_name,group_concat(a.COLUMN_NAME) as column_name,a.NON_UNIQUE as non_unique,"
                + "a.INDEX_NAME as index_name,a.INDEX_TYPE as type from information_schema.statistics"
                + " a where a.INDEX_NAME <> 'PRIMARY' and a.TABLE_SCHEMA=? group by a.TABLE_NAME, a.NON_UNIQUE, a.INDEX_NAME, a.INDEX_TYPE";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, connectionInfo.getDbName());
        ResultSet indexInfo = preparedStatement.executeQuery();
        return convertMap(indexInfo);
    }

    @Override
    protected List<Map<String, Object>> findAllPrimaryKeys(DatasourceConnectionInfo connectionInfo, Connection connection) throws SQLException {
        String sql = "select k.COLUMN_NAME,k.TABLE_NAME from information_schema.TABLE_CONSTRAINTS t"
                + " join information_schema.KEY_COLUMN_USAGE k using (CONSTRAINT_NAME,TABLE_SCHEMA,TABLE_NAME)"
                + " where t.CONSTRAINT_TYPE='PRIMARY KEY' and t.TABLE_SCHEMA=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, connectionInfo.getDbName());
        ResultSet primaryKeys = preparedStatement.executeQuery();
        return convertMap(primaryKeys);
    }
}
