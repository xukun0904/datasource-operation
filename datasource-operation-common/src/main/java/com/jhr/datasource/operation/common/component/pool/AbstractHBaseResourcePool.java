package com.jhr.datasource.operation.common.component.pool;

import com.jhr.datasource.operation.api.domain.dto.ConfFile;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import com.jhr.datasource.operation.common.util.HBaseClient;
import org.apache.hadoop.conf.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xukun
 * @since 1.0
 */
public abstract class AbstractHBaseResourcePool extends ResourcePool {

    @Autowired
    protected LocalFileTools localFileTools;

    private final Map<String, HBaseClient> resourceMap = new ConcurrentHashMap<>();

    @Override
    protected HBaseClient getResource(DatasourceConnectionInfo connectionInfo) {
        try {
            List<ConfFile> confList = connectionInfo.getConfList();
            Short authType = connectionInfo.getAuthType();
            String ipAndPort = connectionInfo.getIpAndPort();
            // 获取hbase conf配置
            List<String> siteXmlPaths = localFileTools.getLocalConfPaths(confList, Constants.CONF_FILE_TYPE_HBASE_XML);
            Configuration conf = DatasourceOperationUtils.getHBaseConfiguration(ipAndPort, siteXmlPaths);
            if (Constants.DATA_SOURCE_AUTH_TYPE_KERBEROS.equals(authType)) {
                authenticate(conf, connectionInfo);
            }
            return new HBaseClient(conf);
        } catch (IOException e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        } finally {
            releaseAuthenticateEnv();
        }
        return null;
    }

    protected void authenticate(Configuration conf, DatasourceConnectionInfo connectionInfo) throws IOException {
    }

    protected void releaseAuthenticateEnv() {
    }

    @Override
    public <T> void destroy(T resource) {
        if (resource != null) {
            HBaseClient client = (HBaseClient) resource;
            client.closeConnect();
            client = null;
        }
    }

    @Override
    public <T> Map<String, T> getResourceMap() {
        return (Map<String, T>) resourceMap;
    }

    @Override
    public void generateAllConnectId(List<String> accessibleIds, List<String> unAccessibleIds) {
        for (Map.Entry<String, HBaseClient> entry : resourceMap.entrySet()) {
            try {
                HBaseClient client = entry.getValue();
                // 进行判断表是否存在操作，测试数据源连接是否正常
                client.isTableExist(Constants.HBASE_TEST_CONNECT_TABLE_NAME);
                accessibleIds.add(entry.getKey());
            } catch (IOException e) {
                LOGGER.error("连接Hbase数据源失败！", e);
                // 连接异常的主键id
                unAccessibleIds.add(entry.getKey());
            }
        }
    }

    @PreDestroy
    public void destroy() {
        // 销毁连接池
        for (HBaseClient client : resourceMap.values()) {
            destroy(client);
        }
    }
}
