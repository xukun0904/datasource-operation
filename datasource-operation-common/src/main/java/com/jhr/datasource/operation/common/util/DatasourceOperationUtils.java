package com.jhr.datasource.operation.common.util;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.jhr.datasource.operation.api.domain.dto.ConfFile;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.api.domain.response.ResultCode;
import com.jhr.datasource.operation.api.grpc.dataset.ConfFileInfo;
import com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo;
import com.jhr.datasource.operation.api.grpc.response.ResponseResult;
import com.jhr.datasource.operation.common.domain.response.CommonCode;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用与项目有关的工具类
 *
 * @author xukun
 * @since 1.0
 */
public class DatasourceOperationUtils {

    public static ResultCode buildResultCode(boolean success, String code, String message) {
        return new ResultCode() {
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
        };
    }

    public static String readFirstLineByNewLine(BufferedReader reader, String newLine) throws IOException {
        char[] chars = newLine.toCharArray();
        int i;
        StringBuilder line = new StringBuilder();
        while ((i = reader.read()) >= 0) {
            char c = (char) i;
            if (c == chars[0]) {
                if (chars.length == 1) {
                    break;
                }
                // 删除元素
                chars = ArrayUtil.remove(chars, 0);
                continue;
            } else if (chars.length != newLine.length()) {
                chars = newLine.toCharArray();
            }
            line.append(c);
        }
        return line.toString();
    }

    public static Properties getKafkaProperties(DatasourceConnectionInfo connectionInfo) {
        Properties props = new Properties();
        props.put("bootstrap.servers", connectionInfo.getIpAndPort());
        // 请求超时时间，默认120000
        props.put("request.timeout.ms", 5000);
        String advanceParamConf = connectionInfo.getAdvanceParamConf();
        if (StrUtil.isNotBlank(advanceParamConf)) {
            JSONObject jsonObject = JSONUtil.parseObj(advanceParamConf);
            for (String key : jsonObject.keySet()) {
                if (StrUtil.isNotBlank(key)) {
                    props.put(key, jsonObject.get(key));
                }
            }
        }
        return props;
    }

    public static Configuration getHBaseConfiguration(String ipAndPort, List<String> siteXmlPaths) {
        // 执行此步时，会去resources目录下找相应的配置文件，例如hbase-site.xml
        Configuration conf = HBaseConfiguration.create();
        // 请求超时时间，默认一分钟
        conf.setInt("hbase.client.timeout", 5000);
        // 客户端重试休眠时间
        conf.setInt("hbase.client.pause", 100);
        // 重试次数，默认35
        conf.setInt("hbase.client.retries.number", 3);
        // zookeeper集群重试次数
        conf.setInt("zookeeper.recovery.retry", 1);
        // zookeeper重试休眠时间
        conf.setInt("zookeeper.recovery.retry.intervalmill", 200);
        // 操作超时时间
        conf.setInt("hbase.client.operation.timeout", 30000);
        // scan超时时间
        conf.setInt("hbase.client.scanner.timeout.period", 60000);
        // zookeeper集群地址
        if (StrUtil.isNotBlank(ipAndPort)) {
            conf.set("hbase.zookeeper.quorum", ipAndPort);
        }
        loadConfResource(conf, siteXmlPaths);
        return conf;
    }

    private static void loadConfResource(Configuration conf, List<String> siteXmlPaths) {
        // 加载配置文件
        if (CollectionUtil.isNotEmpty(siteXmlPaths)) {
            for (String siteXmlPath : siteXmlPaths) {
                conf.addResource(new Path(siteXmlPath), false);
            }
        }
    }

    public static Configuration getHdfsConfiguration(List<String> confList, String ip) {
        Configuration conf = new Configuration();
        if (StrUtil.isNotBlank(ip)) {
            conf.set("fs.defaultFS", ip);
        }
        // 加载配置文件
        loadConfResource(conf, confList);
        conf.setBoolean("fs.hdfs.impl.disable.cache", true);
        return conf;
    }

    public static DatasourceConnectionInfo getDatasourceConnectionInfo(ConnectionInfo request) {
        DatasourceConnectionInfo connectionInfo = new DatasourceConnectionInfo();
        connectionInfo.setId(request.getId());
        connectionInfo.setIp(request.getIp());
        connectionInfo.setPort(request.getPort());
        connectionInfo.setDbName(request.getDbName());
        connectionInfo.setUsername(request.getUsername());
        connectionInfo.setPassword(request.getPassword());
        connectionInfo.setSchema(request.getSchema());
        connectionInfo.setIpAndPort(request.getIpAndPort());
        connectionInfo.setAuthType((short) request.getAuthTypeValue());
        connectionInfo.setServiceDiscoveryMode(request.getServiceDiscoveryMode());
        connectionInfo.setZookeeperNamespace(request.getZookeeperNamespace());
        connectionInfo.setSaslQop(request.getSaslQop());
        connectionInfo.setPrincipal(request.getPrincipal());
        connectionInfo.setUserPrincipal(request.getUserPrincipal());
        connectionInfo.setConfList(getConfFileList(request.getConfFileList()));
        connectionInfo.setDsourceType((short) request.getDsourceTypeValue());
        connectionInfo.setAdvanceParamConf(request.getAdvanceParamConf());
        connectionInfo.setDriverClassName(request.getDriverClassName());
        connectionInfo.setTableName(request.getTableName());
        Map<String, Object[]> sqlMap = getSqlMap(request.getSqlMap());
        connectionInfo.setSqlMap(sqlMap);
        connectionInfo.setDsourceMfrs((short) request.getDsourceMfrsValue());
        connectionInfo.setConnectType((short) request.getConnectTypeValue());
        return connectionInfo;
    }

    public static Map<String, Object[]> getSqlMap(String sqlMapString) {
        Map<String, Object[]> sqlMap = new HashMap<>();
        JSONObject sqlMapJson = JSONUtil.parseObj(sqlMapString);
        for (Map.Entry<String, Object> sqlEntry : sqlMapJson.entrySet()) {
            List<Object> args = new ArrayList<>();
            String argsJsonString = String.valueOf(sqlEntry.getValue());
            if (JSONUtil.isJsonArray(argsJsonString)) {
                // JSONArray转换成Object[]
                JSONArray argJsonArray = JSONUtil.parseArray(argsJsonString);
                for (Object arg : argJsonArray) {
                    if (arg instanceof JSONArray) {
                        JSONArray os = (JSONArray) arg;
                        args.addAll(os);
                    } else {
                        args.add(arg);
                    }
                }
            }
            sqlMap.put(sqlEntry.getKey(), args.toArray());
        }
        return sqlMap;
    }

    public static List<ConfFile> getConfFileList(List<ConfFileInfo> requestConfFileList) {
        return requestConfFileList.stream().map(requestConfFile -> {
            ConfFile confFile = new ConfFile();
            confFile.setConfFilePath(requestConfFile.getConfFilePath());
            confFile.setLocalConfFilePath(requestConfFile.getLocalConfFilePath());
            confFile.setDsourceType((short) requestConfFile.getDsourceTypeValue());
            confFile.setConfFileType((short) requestConfFile.getConfFileTypeValue());
            return confFile;
        }).collect(Collectors.toList());
    }

    public static String parseString(Object obj) {
        // 增加时间的转换
        return obj == null ? "" : obj.toString();
    }

    public static ResponseResult success() {
        return withCode(CommonCode.SUCCESS);
    }

    public static <T extends Message> ResponseResult success(T data) {
        return withCode(CommonCode.SUCCESS, data);
    }

    public static ResponseResult error() {
        return withCode(CommonCode.SERVER_ERROR);
    }

    public static ResponseResult error(String message) {
        return withCode(CommonCode.SERVER_ERROR, StrUtil.blankToDefault(message, CommonCode.SERVER_ERROR.message()));
    }

    public static ResponseResult withCode(ResultCode resultCode) {
        return ResponseResult.newBuilder()
                .setSuccess(resultCode.success())
                .setCode(resultCode.code())
                .setMessage(resultCode.message())
                .build();
    }

    public static ResponseResult withCode(ResultCode resultCode, String message) {
        return ResponseResult.newBuilder()
                .setSuccess(resultCode.success())
                .setCode(resultCode.code())
                .setMessage(message)
                .build();
    }

    public static <T extends Message> ResponseResult withCode(ResultCode resultCode, T data) {
        return ResponseResult.newBuilder()
                .setSuccess(resultCode.success())
                .setCode(resultCode.code())
                .setMessage(resultCode.message())
                .setData(Any.pack(data))
                .build();
    }

}
