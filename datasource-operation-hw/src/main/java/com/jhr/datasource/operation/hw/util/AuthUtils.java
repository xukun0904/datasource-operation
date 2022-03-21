package com.jhr.datasource.operation.hw.util;

import cn.hutool.core.util.StrUtil;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.File;
import java.io.IOException;

/**
 * @author xukun
 * @since 1.0
 */
public class AuthUtils {
    public static void initHive(String krb5File) {
        releaseKerberos();
        if (StrUtil.isNotBlank(krb5File)) {
            System.setProperty("java.security.krb5.conf", krb5File);
        }
        String serverRealm = System.getProperty("SERVER_REALM");
        String authHostName;
        if (serverRealm != null && !"".equals(serverRealm)) {
            authHostName = "hadoop." + serverRealm.toLowerCase();
        } else {
            serverRealm = KerberosUtil.getKrb5DomainRealm();
            if (serverRealm != null && !"".equals(serverRealm)) {
                authHostName = "hadoop." + serverRealm.toLowerCase();
            } else {
                authHostName = "hadoop";
            }
        }
        System.setProperty(Constants.ZOOKEEPER_SERVER_PRINCIPAL_KEY, "zookeeper/" + authHostName);
    }

    public static void releaseKerberos() {
        // 删除Kerberos认证时候的环境变量，防止污染
        System.clearProperty(Constants.JAVA_SECURITY_LOGIN_CONF);
        System.clearProperty(Constants.JAVA_SECURITY_KRB5_CONF);
        System.clearProperty(Constants.ZOOKEEPER_SERVER_PRINCIPAL_KEY);
        javax.security.auth.login.Configuration.setConfiguration(null);
        UserGroupInformation.reset();
    }

    public static void initKafka(String krb5FilePath, String userKeytabFilePath, String userPrincipal) throws IOException {
        releaseKerberos();
        KafkaLoginUtil.setKrb5Config(krb5FilePath);
        KafkaLoginUtil.setZookeeperServerPrincipal(Constants.ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);
        KafkaLoginUtil.setJaasFile(userPrincipal, userKeytabFilePath);
    }

    public static void initHBase(Configuration conf, String username, String krb5FilePath, String userKeytabFilePath) throws IOException {
        if (User.isHBaseSecurityEnabled(conf)) {
            releaseKerberos();
            /*
             * if need to connect zk, please provide jaas info about zk. of course,
             * you can do it as below:
             * System.setProperty("java.security.auth.login.config", confDirPath +
             * "jaas.conf"); but the demo can help you more : Note: if this process
             * will connect more than one zk cluster, the demo may be not proper. you
             * can contact us for more help
             */
            userKeytabFilePath = userKeytabFilePath.replace(StrUtil.SLASH, File.separator);
            krb5FilePath = krb5FilePath.replace(StrUtil.SLASH, File.separator);
            HBaseLoginUtil.setJaasConf(Constants.ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME, username, userKeytabFilePath);
            HBaseLoginUtil.setZookeeperServerPrincipal(Constants.ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);
            HBaseLoginUtil.login(username, userKeytabFilePath, krb5FilePath, conf);
        }
    }

    public static void initHdfs(Configuration conf, String krb5FilePath, String userKeytabFilePath, String username) throws IOException {
        // Kerberos认证
        if (Constants.DATA_SOURCE_AUTH_KERBEROS_LOWERCASE.equalsIgnoreCase(conf.get(Constants.HDFS_AUTH_KERBEROS_KEY))) {
            System.setProperty("java.security.krb5.conf", krb5FilePath);
            HdfsLoginUtil.login(username, userKeytabFilePath, krb5FilePath, conf);
        }
    }
}
