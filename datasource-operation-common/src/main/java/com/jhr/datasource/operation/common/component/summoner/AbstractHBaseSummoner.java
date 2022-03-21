package com.jhr.datasource.operation.common.component.summoner;

import cn.hutool.core.util.StrUtil;
import com.jhr.datasource.operation.api.domain.dto.ConfFile;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.component.LocalFileTools;
import com.jhr.datasource.operation.common.component.pool.ResourcePool;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import com.jhr.datasource.operation.common.util.HBaseClient;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author xukun
 * @since 1.0
 */
public abstract class AbstractHBaseSummoner extends DatasourceSummoner {

    @Autowired
    protected LocalFileTools localFileTools;

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractHBaseSummoner.class);

    protected HBaseClient getHBaseClient(DatasourceConnectionInfo connectionInfo) throws IOException {
        try {
            List<ConfFile> confList = connectionInfo.getConfList();
            String ipAndPort = connectionInfo.getIpAndPort();
            // 获取hbase conf配置
            List<String> siteXmlPaths = localFileTools.getLocalConfPaths(confList, Constants.CONF_FILE_TYPE_HBASE_XML);
            Configuration conf = DatasourceOperationUtils.getHBaseConfiguration(ipAndPort, siteXmlPaths);
            if (Constants.DATA_SOURCE_AUTH_TYPE_KERBEROS.equals(connectionInfo.getAuthType())) {
                authenticate(conf, connectionInfo);
            }
            return new HBaseClient(conf);
        } finally {
            releaseAuthenticateEnv();
        }
    }

    protected void authenticate(Configuration conf, DatasourceConnectionInfo connectionInfo) throws IOException {
    }

    protected void releaseAuthenticateEnv() {
    }

    /**
     * 测试连接是否正常
     *
     * @param connectionInfo 连接信息
     */
    @Override
    public void testConnectAccessible(DatasourceConnectionInfo connectionInfo) {
        try (HBaseClient client = getHBaseClient(connectionInfo)) {
            assert client != null;
            client.isTableExist(Constants.HBASE_TEST_CONNECT_TABLE_NAME);
            LOGGER.debug("数据源连接正常！");
        } catch (IOException e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        }
    }

    /**
     * 测试连接是否正常
     *
     * @param connectionInfo 连接信息
     */
    @Override
    public void testExistConnectAccessible(DatasourceConnectionInfo connectionInfo) {
        try {
            HBaseClient client = getExistHBaseClient(connectionInfo);
            client.isTableExist(Constants.HBASE_TEST_CONNECT_TABLE_NAME);
        } catch (IOException e) {
            ExceptionCast.cast(DatasourceOperationUtils.buildResultCode(false, DatasourceCode.DATASOURCE_CONNECT_ERROR.code(),
                    DatasourceCode.DATASOURCE_CONNECT_ERROR.message() + e.getMessage()));
        }
    }

    /**
     * 获取可用连接，若没有则新建连接
     *
     * @param connectionInfo 连接信息
     * @return 可用连接
     */
    private HBaseClient getActiveHBaseClient(DatasourceConnectionInfo connectionInfo) throws IOException {
        if (StrUtil.isNotBlank(connectionInfo.getId())) {
            return getExistHBaseClient(connectionInfo);
        }
        return getHBaseClient(connectionInfo);
    }

    private HBaseClient getExistHBaseClient(DatasourceConnectionInfo connectionInfo) {
        ResourcePool resourcePool = poolStrategy.getResourcePoolByBeanName(Constants.DATASOURCE_POOL_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        return resourcePool.get(connectionInfo);
    }

    @Override
    public Collection<String> findAllTableNames(DatasourceConnectionInfo connectionInfo) {
        try {
            HBaseClient client = getActiveHBaseClient(connectionInfo);
            return client.getAllTableNames();
        } catch (IOException e) {
            LOGGER.error("查找表名称失败！", e);
            ExceptionCast.cast(DatasourceCode.DATASOURCE_FIND_ALL_TABLE_NAMES_FAILED);
        }
        return Collections.emptyList();
    }
}
