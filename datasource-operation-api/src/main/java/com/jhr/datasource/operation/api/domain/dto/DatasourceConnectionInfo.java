package com.jhr.datasource.operation.api.domain.dto;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author xukun
 * @since 1.0
 */
public class DatasourceConnectionInfo implements Serializable {

    /**
     * 唯一标识
     */
    private String id;

    /**
     * rdbms以及文件服务器ip
     */
    private String ip;

    /**
     * rdbms以及文件服务器端口
     */
    private String port;

    /**
     * rdbms以及hive数据库名称
     */
    private String dbName;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * rdbms数据库集合
     */
    private String schema;

    /**
     * hive以及kafka（可以是多个值例如：x.x.x.x:port,x.x.x.x:port）
     */
    private String ipAndPort;

    /**
     * hive以及kafka认证模式
     */
    private Short authType;

    /**
     * hive服务发现模式
     */
    private String serviceDiscoveryMode;

    /**
     * hive zookeeper命名空间
     */
    private String zookeeperNamespace;

    /**
     * hive跨节点认证加密配置
     */
    private String saslQop;

    /**
     * hive集群认证信息
     */
    private String principal;

    /**
     * 认证用户
     */
    private String userPrincipal;

    /**
     * 配置文件
     */
    private List<ConfFile> confList;

    /**
     * 数据源类型（1 MPP 2 MYSQL 3 ORACLE 21 HIVE 31 Kafka 41 FTP 42 SFTP）
     */
    private Short dsourceType;

    /**
     * 高级参数配置JSON
     */
    private String advanceParamConf;

    /**
     * rdbms及hive driver
     */
    private String driverClassName;

    /**
     * 选择的表名
     */
    private String tableName;

    /**
     * 执行的sql
     */
    private Map<String, Object[]> sqlMap;

    /**
     * 厂商(1：华为；2：星环； 3：腾讯；4：阿里； 5：CDH；6：TDH；7：开源；)
     */
    private Short dsourceMfrs;

    /**
     * 连接类型（给Oracle用的：1 sid,2 service）
     */
    private Short connectType;

    public Short getConnectType() {
        return connectType;
    }

    public void setConnectType(Short connectType) {
        this.connectType = connectType;
    }

    public Short getDsourceMfrs() {
        return dsourceMfrs;
    }

    public void setDsourceMfrs(Short dsourceMfrs) {
        this.dsourceMfrs = dsourceMfrs;
    }

    public Map<String, Object[]> getSqlMap() {
        return sqlMap;
    }

    public void setSqlMap(Map<String, Object[]> sqlMap) {
        this.sqlMap = sqlMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdvanceParamConf() {
        return advanceParamConf;
    }

    public void setAdvanceParamConf(String advanceParamConf) {
        this.advanceParamConf = advanceParamConf;
    }

    public Short getDsourceType() {
        return dsourceType;
    }

    public void setDsourceType(Short dsourceType) {
        this.dsourceType = dsourceType;
    }

    public List<ConfFile> getConfList() {
        return confList;
    }

    public void setConfList(List<ConfFile> confList) {
        this.confList = confList;
    }

    public String getUserPrincipal() {
        return userPrincipal;
    }

    public void setUserPrincipal(String userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getSaslQop() {
        return saslQop;
    }

    public void setSaslQop(String saslQop) {
        this.saslQop = saslQop;
    }

    public String getZookeeperNamespace() {
        return zookeeperNamespace;
    }

    public void setZookeeperNamespace(String zookeeperNamespace) {
        this.zookeeperNamespace = zookeeperNamespace;
    }

    public String getServiceDiscoveryMode() {
        return serviceDiscoveryMode;
    }

    public void setServiceDiscoveryMode(String serviceDiscoveryMode) {
        this.serviceDiscoveryMode = serviceDiscoveryMode;
    }

    public Short getAuthType() {
        return authType;
    }

    public void setAuthType(Short authType) {
        this.authType = authType;
    }

    public String getIpAndPort() {
        return ipAndPort;
    }

    public void setIpAndPort(String ipAndPort) {
        this.ipAndPort = ipAndPort;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
