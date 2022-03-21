package com.jhr.datasource.operation.hw.component.pool;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.hw.util.AuthUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author xukun
 * @since 1.0
 */
public class DruidDataSourceWrapper extends DruidDataSource {

    @Override
    public Connection createPhysicalConnection(String url, Properties info) throws SQLException {
        // 判断数据源类型
        if (DbType.hive.equals(super.getDbType())) {
            // 若需要Kerberos认证，先进行认证
            String krb5FilePath = info.getProperty(Constants.KRB_5_FILE_PATH, "");
            if (StrUtil.isNotBlank(krb5FilePath)) {
                AuthUtils.initHive(krb5FilePath);
            }
        }
        return super.createPhysicalConnection(url, info);
    }
}
