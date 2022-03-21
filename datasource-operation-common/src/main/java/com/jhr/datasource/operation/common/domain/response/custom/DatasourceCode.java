package com.jhr.datasource.operation.common.domain.response.custom;


import com.jhr.datasource.operation.api.domain.response.ResultCode;

/**
 * @author xukun
 * @since 1.0
 */
public enum DatasourceCode implements ResultCode {
    DATASOURCE_NOT_SUPPORT(false, "A4001", "当前数据源暂未适配！"),
    DATASOURCE_CONNECT_ERROR(false, "A4002", "数据源连接异常！"),
    DATASOURCE_POOL_EXIST(false, "A4003", "数据源连接池已存在！"),
    DATASOURCE_NOT_EXIST(false, "A4004", "数据源不存在！"),
    DATASOURCE_EXECUTE_SQLS_ERROR(false, "A4005", "执行SQL异常！"),
    DATASOURCE_DIR_PATH_IS_EMPTY(false, "A4006", "文件目录下没有文件！"),
    DATASOURCE_FILE_PATH_NOT_EXIST(false, "A4007", "文件路径不存在！"),
    DATASOURCE_GET_TOPIC_CONTENT_TIMEOUT(false, "A4008", "获取消息字段数超时！"),
    DATASOURCE_FIND_ALL_TABLE_NAMES_FAILED(false, "A4009", "查找表名称失败！"),
    DATASOURCE_GET_FIELD_NUMBER_FAILED(false, "A4010", "获取字段数量失败！"),
    DATASOURCE_FIND_ALL_SCHEMAS_FAILED(false, "A4011", "获取模式名称失败！"),
    DATASOURCE_FIND_ALL_TABLES_FAILED(false, "A4012", "查找表信息失败！"),
    DATASOURCE_FIND_ALL_COLUMNS_FAILED(false, "A4013", "查找字段信息失败！"),
    DATASOURCE_FIND_ALL_METADATA_FAILED(false, "A4014", "查找数据库元信息失败！"),
    DATASOURCE_BATCH_EXECUTE_SQL_MAP_FAILED(false, "A4015", "执行SQL失败！"),
    DATASOURCE_FIND_ALL_INDEXES_FAILED(false, "A4016", "查找索引信息失败！"),
    DATASOURCE_FIND_ALL_IMPORTED_KEYS_FAILED(false, "A4017", "查找外键信息失败！"),
    ;

    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * 操作代码
     */
    private String code;

    /**
     * 提示信息
     */
    private String message;

    DatasourceCode(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
