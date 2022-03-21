package com.jhr.datasource.operation.common.component.minio;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Component
public class MinioUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioUtil.class);

    @Autowired
    private MinioClient client;

    /**
     * 获取⽂件
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @return ⼆进制流
     */
    public InputStream getObject(String bucketName, String objectName) throws Exception {
        return client.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 若本地文件不存在，拉取文件到本地
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @param filePath   本地文件路径
     */
    public void copyObjectToLocal(String bucketName, String objectName, String filePath) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                // 获取输入流
                inputStream = this.getObject(bucketName, objectName);
                File parentFile = file.getParentFile();
                // 创建父目录
                FileUtil.mkdir(parentFile);
                // 写入到本地文件中
                outputStream = new FileOutputStream(file);
                IoUtil.copy(inputStream, outputStream);
            }
        } catch (Exception e) {
            LOGGER.error("拉取文件到本地失败！", e);
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(outputStream);
        }
    }
}
