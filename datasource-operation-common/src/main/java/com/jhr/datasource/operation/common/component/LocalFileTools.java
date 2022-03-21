package com.jhr.datasource.operation.common.component;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.jhr.datasource.operation.api.domain.dto.ConfFile;
import com.jhr.datasource.operation.common.component.minio.MinioUtil;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地文件操作类
 *
 * @author xukun
 * @since 1.0
 */
@Component
public class LocalFileTools {

    @Value("${datasource-operation.file-path}")
    private String filePath;

    @Autowired
    private MinioUtil minioUtil;

    public String getLocalConfPath(List<ConfFile> confList, Short confFileType) {
        if (CollectionUtil.isNotEmpty(confList)) {
            for (ConfFile confFile : confList) {
                if (confFile.getConfFileType().equals(confFileType)) {
                    String confFilePath = confFile.getConfFilePath();
                    // 若本地文件不存在，拉取文件到本地
                    return copyFileToLocal(Constants.BUCKET_NAME_DATASOURCE, confFilePath);
                }
            }
        }
        return "";
    }

    public List<String> getLocalConfPaths(List<ConfFile> confList, List<Short> confFileTypes) {
        List<String> localConfFilePath = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(confList)) {
            for (ConfFile confFile : confList) {
                if (confFileTypes.contains(confFile.getConfFileType())) {
                    String confFilePath = confFile.getConfFilePath();
                    // 若本地文件不存在，拉取文件到本地
                    String localFilePath = copyFileToLocal(Constants.BUCKET_NAME_DATASOURCE, confFilePath);
                    localConfFilePath.add(localFilePath);
                }
            }
        }
        return localConfFilePath;
    }

    public String copyFileToLocal(String bucketName, String objectName) {
        // 获取本地文件路径
        String localFilePath = filePath + StrUtil.C_SLASH + objectName;
        if (!FileUtil.exist(localFilePath)) {
            minioUtil.copyObjectToLocal(bucketName, objectName, localFilePath);
        }
        return localFilePath;
    }
}
