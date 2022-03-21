package com.jhr.datasource.operation.api.domain.dto;

import java.io.Serializable;

/**
 * @author xukun
 * @since 1.0
 */
public class ConfFile implements Serializable {

    /**
     * 数据源类型(1 rdbms 2 hive 3 kafka 4 文件服务器)
     */
    private Short dsourceType;

    /**
     * 文件存储位置
     */
    private String confFilePath;

    /**
     * 文件类别(1 krb5.conf 2 user.keytab 3 hadoop配置文件(core-site.xml, hdfs-site.xml, hbase-site.xml)
     */
    private Short confFileType;

    /**
     * 本地文件存储位置
     */
    private String localConfFilePath;

    public Short getDsourceType() {
        return dsourceType;
    }

    public void setDsourceType(Short dsourceType) {
        this.dsourceType = dsourceType;
    }

    public String getConfFilePath() {
        return confFilePath;
    }

    public void setConfFilePath(String confFilePath) {
        this.confFilePath = confFilePath;
    }

    public Short getConfFileType() {
        return confFileType;
    }

    public void setConfFileType(Short confFileType) {
        this.confFileType = confFileType;
    }

    public String getLocalConfFilePath() {
        return localConfFilePath;
    }

    public void setLocalConfFilePath(String localConfFilePath) {
        this.localConfFilePath = localConfFilePath;
    }
}
