package com.jhr.datasource.operation.nl.component.summoner.impl;

import cn.hutool.core.util.StrUtil;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.summoner.JdbcSummoner;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author xukun
 * @since 1.0
 */
@Component(Constants.SUMMONER_BEAN_NAME_ORACLE)
public class OracleSummoner extends JdbcSummoner {

    @Override
    protected void putConnectionProperties(Properties prop) {
        prop.put("remarksReporting", "true");
    }

    @Override
    public String getJdbcUrl(DatasourceConnectionInfo connectionInfo) {
        if (Constants.ORACLE_CONNECT_TYPE_SID.equals(connectionInfo.getConnectType())) {
            return "jdbc:oracle:thin:@" + connectionInfo.getIp() + StrUtil.COLON + connectionInfo.getPort() + StrUtil.COLON + connectionInfo.getDbName();
        } else if (Constants.ORACLE_CONNECT_TYPE_SERVICE_NAME.equals(connectionInfo.getConnectType())) {
            return "jdbc:oracle:thin:@//" + connectionInfo.getIp() + StrUtil.COLON + connectionInfo.getPort() + StrUtil.SLASH + connectionInfo.getDbName();
        }
        ExceptionCast.cast(DatasourceCode.DATASOURCE_NOT_SUPPORT);
        return "";
    }

    @Override
    protected List<Map<String, Object>> findAllPrimaryKeys(DatasourceConnectionInfo connectionInfo, Connection con) throws SQLException {
        String sql = "SELECT NULL AS table_cat, c.owner AS table_schem, c.table_name, c.column_name, c.position AS key_seq,"
                + " c.constraint_name AS pk_name FROM all_cons_columns c, all_constraints k WHERE k.constraint_type = 'P'"
                + " AND k.owner like ? escape '/' AND k.constraint_name = c.constraint_name AND k.table_name = c.table_name"
                + " AND k.owner = c.owner ORDER BY column_name";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, con.getSchema());
        ResultSet primaryKeys = preparedStatement.executeQuery();
        return convertMap(primaryKeys);
    }

    @Override
    protected List<Map<String, Object>> findAllIndexInfos(DatasourceConnectionInfo connectionInfo, Connection con) throws SQLException {
        String sql = "select null as table_cat, i.owner as table_schem, i.table_name,"
                + " decode (i.uniqueness, 'UNIQUE', 0, 1) as non_unique, null as index_qualifier, i.index_name, 1 as type, c.column_position as ordinal_position,"
                + "c.column_name, null as asc_or_desc, i.distinct_keys as cardinality, i.leaf_blocks as pages, null as filter_condition from all_indexes i,"
                + " all_ind_columns c where i.owner = ? and i.index_name = c.index_name and i.table_owner = c.table_owner"
                + " and i.table_name = c.table_name and i.owner = c.index_owner order by type, index_name, ordinal_position";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, connectionInfo.getSchema());
        ResultSet indexInfo = preparedStatement.executeQuery();
        return convertMap(indexInfo);
    }
}
