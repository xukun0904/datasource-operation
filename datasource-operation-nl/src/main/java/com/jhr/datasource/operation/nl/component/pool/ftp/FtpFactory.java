package com.jhr.datasource.operation.nl.component.pool.ftp;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.extra.ftp.AbstractFtp;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpMode;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import com.jcraft.jsch.Session;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.domain.response.custom.DatasourceCode;
import com.jhr.datasource.operation.common.exception.ExceptionCast;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author xukun
 * @since 1.0
 */
public class FtpFactory implements PooledObjectFactory<AbstractFtp> {

    private final DatasourceConnectionInfo connectionInfo;

    public static final Logger LOGGER = LoggerFactory.getLogger(FtpFactory.class);

    public FtpFactory(DatasourceConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    @Override
    public PooledObject<AbstractFtp> makeObject() {
        AbstractFtp abstractFtp = null;
        if (Constants.DATA_CONN_TYPE_FTP.equals(connectionInfo.getDsourceType())) {
            Ftp ftp = new Ftp(connectionInfo.getIp(), Integer.parseInt(connectionInfo.getPort()), connectionInfo.getUsername(), connectionInfo.getPassword(), CharsetUtil.CHARSET_UTF_8, FtpMode.Passive);
            ftp.setBackToPwd(true);
            abstractFtp = ftp;
        } else if (Constants.DATA_CONN_TYPE_SFTP.equals(connectionInfo.getDsourceType())) {
            Session session = JschUtil.createSession(connectionInfo.getIp(), Integer.parseInt(connectionInfo.getPort()), connectionInfo.getUsername(), connectionInfo.getPassword());
            // 设置跳过Kerberos身份认证提示
            session.setConfig("PreferredAuthentications", "public,keyboard-interactive,password");
            abstractFtp = new Sftp(session);
        } else {
            ExceptionCast.cast(DatasourceCode.DATASOURCE_NOT_SUPPORT);
        }
        return new DefaultPooledObject<>(abstractFtp);
    }

    @Override
    public void destroyObject(PooledObject<AbstractFtp> pooledObject) throws IOException {
        AbstractFtp object = pooledObject.getObject();
        object.close();
    }

    @Override
    public boolean validateObject(PooledObject<AbstractFtp> pooledObject) {
        AbstractFtp object = pooledObject.getObject();
        try {
            // 进行查询当前路径操作，测试数据源连接是否正常
            object.pwd();
            return true;
        } catch (Exception e) {
            LOGGER.error("验证当前Ftp对象是否存活失败！", e);
        }
        return false;
    }

    @Override
    public void activateObject(PooledObject<AbstractFtp> pooledObject) throws Exception {
        // 连接过期，重新连接
        pooledObject.getObject().reconnectIfTimeout();
    }

    @Override
    public void passivateObject(PooledObject<AbstractFtp> pooledObject) throws Exception {
    }
}
