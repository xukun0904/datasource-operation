package com.jhr.datasource.operation.common.domain.constant;

import cn.hutool.core.collection.CollectionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xukun
 * @since 1.0
 */
public class Constants {

    public static final String BUCKET_NAME_DATASOURCE = "data-govern-datasource";
    public static final String DATA_SOURCE_DEFAULT_SCHEMA = "public";
    public static final String HBASE_TEST_CONNECT_TABLE_NAME = "default";
    public static final String DATA_SOURCE_AUTH_KERBEROS_LOWERCASE = "kerberos";
    public static final String HDFS_TEST_CONNECT_PATH = "/test";

    public static final Short DATA_SOURCE_AUTH_TYPE_NORMAL = 1;
    public static final Short DATA_SOURCE_AUTH_TYPE_KERBEROS = 2;
    public static final Short ORACLE_CONNECT_TYPE_SID = 1;
    public static final Short ORACLE_CONNECT_TYPE_SERVICE_NAME = 2;

    public static final Short CONF_FILE_TYPE_KRB5_CONF = 1;
    public static final Short CONF_FILE_TYPE_USER_KEYTAB = 2;
    public static final Short CONF_FILE_TYPE_CORE_SITE_XML = 3;
    public static final Short CONF_FILE_TYPE_HDFS_SITE_XML = 4;
    public static final Short CONF_FILE_TYPE_HBASE_SITE_XML = 5;
    public static final List<Short> CONF_FILE_TYPE_HDFS_XML = CollectionUtil.newArrayList(CONF_FILE_TYPE_CORE_SITE_XML, CONF_FILE_TYPE_HDFS_SITE_XML,
            CONF_FILE_TYPE_HBASE_SITE_XML);
    public static final List<Short> CONF_FILE_TYPE_HBASE_XML = CollectionUtil.newArrayList(CONF_FILE_TYPE_CORE_SITE_XML, CONF_FILE_TYPE_HDFS_SITE_XML,
            CONF_FILE_TYPE_HBASE_SITE_XML);

    public static final String KRB_5_FILE_PATH = "krb5FilePath";
    public static final String DATA_SOURCE_AUTH_KERBEROS = "KERBEROS";
    public static final String JAVA_SECURITY_LOGIN_CONF = "java.security.auth.login.config";
    public static final String JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";
    public static final String ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME = "Client";
    public static final String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";
    public static final String ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL = "zookeeper/hadoop.hadoop.com";
    public static final String HDFS_AUTH_KERBEROS_KEY = "hadoop.security.authentication";

    public static final String DATA_SOURCE_INPUT_CONF_KEY_NEWLINE = "newLine";
    public static final String DATA_SOURCE_INPUT_CONF_KEY_DELIMITER = "delimiter";
    public static final String DATA_SOURCE_INPUT_CONF_KEY_FILE_TAG = "fileTag";
    public static final Integer DATA_SOURCE_INPUT_CONF_FILE_TAG_FILE = 1;
    public static final Integer DATA_SOURCE_INPUT_CONF_FILE_TAG_PATH = 2;

    /**
     * symbol
     */
    public static final String PERCENT = "%";
    public static final String SEMICOLON = ";";

    public static final Short DATA_CONN_TYPE_MPP = 1;
    public static final Short DATA_CONN_TYPE_MYSQL = 2;
    public static final Short DATA_CONN_TYPE_ORACLE = 3;
    public static final Short DATA_CONN_TYPE_HIVE = 21;
    public static final Short DATA_CONN_TYPE_HBASE = 22;
    public static final Short DATA_CONN_TYPE_KAFKA = 31;
    public static final Short DATA_CONN_TYPE_FTP = 41;
    public static final Short DATA_CONN_TYPE_SFTP = 42;
    public static final Short DATA_CONN_TYPE_HDFS = 43;

    public static final String SUMMONER_BEAN_NAME_MYSQL = "mysqlSummoner";
    public static final String SUMMONER_BEAN_NAME_GAUSS_DB = "gaussDbSummoner";
    public static final String SUMMONER_BEAN_NAME_ORACLE = "oracleSummoner";
    public static final String SUMMONER_BEAN_NAME_HIVE = "hiveSummoner";
    public static final String SUMMONER_BEAN_NAME_HBASE = "hbaseSummoner";
    public static final String SUMMONER_BEAN_NAME_KAFKA = "kafkaSummoner";
    public static final String SUMMONER_BEAN_NAME_FTP = "ftpSummoner";
    public static final String SUMMONER_BEAN_NAME_SFTP = "sftpSummoner";
    public static final String SUMMONER_BEAN_NAME_HDFS = "hdfsSummoner";

    public static final String RESOURCE_POOL_BEAN_NAME_JDBC = "jdbcResourcePool";
    public static final String RESOURCE_POOL_BEAN_NAME_HBASE = "hbaseResourcePool";
    public static final String RESOURCE_POOL_BEAN_NAME_KAFKA = "kafkaAdminResourcePool";
    public static final String RESOURCE_POOL_BEAN_NAME_FTP = "ftpRsourcePool";
    public static final String RESOURCE_POOL_BEAN_NAME_HDFS = "hdfsResourcePool";

    public static final Map<Short, String> DATASOURCE_POOL_BEAN_NAME_MAP = new HashMap<>();
    public static final Map<Short, String> DATASOURCE_DRIVER_MAP = new HashMap<>();
    public static final Map<Short, String> DATASOURCE_BEAN_NAME_MAP = new HashMap<>();
    static {
        // 数据源类型对应beanName
        DATASOURCE_BEAN_NAME_MAP.put(DATA_CONN_TYPE_MPP, SUMMONER_BEAN_NAME_GAUSS_DB);
        DATASOURCE_BEAN_NAME_MAP.put(DATA_CONN_TYPE_MYSQL, SUMMONER_BEAN_NAME_MYSQL);
        DATASOURCE_BEAN_NAME_MAP.put(DATA_CONN_TYPE_ORACLE, SUMMONER_BEAN_NAME_ORACLE);
        DATASOURCE_BEAN_NAME_MAP.put(DATA_CONN_TYPE_HIVE, SUMMONER_BEAN_NAME_HIVE);
        DATASOURCE_BEAN_NAME_MAP.put(DATA_CONN_TYPE_HBASE, SUMMONER_BEAN_NAME_HBASE);
        DATASOURCE_BEAN_NAME_MAP.put(DATA_CONN_TYPE_FTP, SUMMONER_BEAN_NAME_FTP);
        DATASOURCE_BEAN_NAME_MAP.put(DATA_CONN_TYPE_HDFS, SUMMONER_BEAN_NAME_HDFS);
        DATASOURCE_BEAN_NAME_MAP.put(DATA_CONN_TYPE_SFTP, SUMMONER_BEAN_NAME_SFTP);
        DATASOURCE_BEAN_NAME_MAP.put(DATA_CONN_TYPE_KAFKA, SUMMONER_BEAN_NAME_KAFKA);
        // 数据源类型对应poolName
        DATASOURCE_POOL_BEAN_NAME_MAP.put(DATA_CONN_TYPE_MPP, RESOURCE_POOL_BEAN_NAME_JDBC);
        DATASOURCE_POOL_BEAN_NAME_MAP.put(DATA_CONN_TYPE_MYSQL, RESOURCE_POOL_BEAN_NAME_JDBC);
        DATASOURCE_POOL_BEAN_NAME_MAP.put(DATA_CONN_TYPE_HIVE, RESOURCE_POOL_BEAN_NAME_JDBC);
        DATASOURCE_POOL_BEAN_NAME_MAP.put(DATA_CONN_TYPE_ORACLE, RESOURCE_POOL_BEAN_NAME_JDBC);
        DATASOURCE_POOL_BEAN_NAME_MAP.put(DATA_CONN_TYPE_HBASE, RESOURCE_POOL_BEAN_NAME_HBASE);
        DATASOURCE_POOL_BEAN_NAME_MAP.put(DATA_CONN_TYPE_KAFKA, RESOURCE_POOL_BEAN_NAME_KAFKA);
        DATASOURCE_POOL_BEAN_NAME_MAP.put(DATA_CONN_TYPE_FTP, RESOURCE_POOL_BEAN_NAME_FTP);
        DATASOURCE_POOL_BEAN_NAME_MAP.put(DATA_CONN_TYPE_SFTP, RESOURCE_POOL_BEAN_NAME_FTP);
        DATASOURCE_POOL_BEAN_NAME_MAP.put(DATA_CONN_TYPE_HDFS, RESOURCE_POOL_BEAN_NAME_HDFS);
        // 数据源类型对应连接驱动
        DATASOURCE_DRIVER_MAP.put(DATA_CONN_TYPE_MPP, "org.postgresql.Driver");
        DATASOURCE_DRIVER_MAP.put(DATA_CONN_TYPE_MYSQL, "com.mysql.cj.jdbc.Driver");
        DATASOURCE_DRIVER_MAP.put(DATA_CONN_TYPE_ORACLE, "oracle.jdbc.OracleDriver");
        DATASOURCE_DRIVER_MAP.put(DATA_CONN_TYPE_HIVE, "org.apache.hive.jdbc.HiveDriver");
        DATASOURCE_DRIVER_MAP.put(DATA_CONN_TYPE_HBASE, "org.apache.phoenix.jdbc.PhoenixEmbeddedDriver");
    }
}
