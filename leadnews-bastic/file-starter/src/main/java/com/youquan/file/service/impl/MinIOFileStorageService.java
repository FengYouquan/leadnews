package com.youquan.file.service.impl;

import com.youquan.file.config.MinIOConfig;
import com.youquan.file.config.MinIOConfigProperties;
import com.youquan.file.service.FileStorageService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/1 0:07
 */
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(MinIOConfigProperties.class)
@Import(MinIOConfig.class)
public class MinIOFileStorageService implements FileStorageService {
    private final MinioClient minioClient;
    private final MinIOConfigProperties minIOConfigProperties;
    private final static String SEPARATOR = "/";


    /**
     * @param dirPath  路径
     * @param filename yyyy/mm/dd/file.jpg
     * @return String
     */
    public String builderFilePath(String dirPath, String filename) {
        StringBuilder stringBuilder = new StringBuilder(50);
        if (!StringUtils.isEmpty(dirPath)) {
            stringBuilder.append(dirPath).append(SEPARATOR);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String todayStr = sdf.format(new Date());
        stringBuilder.append(todayStr).append(SEPARATOR);
        stringBuilder.append(filename);
        return stringBuilder.toString();
    }

    /**
     * 上传图片文件
     *
     * @param prefix      文件前缀
     * @param filename    文件名
     * @param inputStream 文件流
     * @return 文件全路径
     */
    @Override
    public String uploadImgFile(String prefix, String filename, InputStream inputStream) {
        String filePath = builderFilePath(prefix, filename);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType("image/jpg")
                    .bucket(minIOConfigProperties.getBucket()).stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            return minIOConfigProperties.getReadPath() + SEPARATOR + minIOConfigProperties.getBucket() +
                    SEPARATOR + filePath;
        } catch (Exception ex) {
            log.error("minio put file error.", ex);
            throw new RuntimeException("上传文件失败");
        }
    }

    /**
     * 上传html文件
     *
     * @param prefix      文件前缀
     * @param filename    文件名
     * @param inputStream 文件流
     * @return 文件全路径
     */
    @Override
    public String uploadHtmlFile(String prefix, String filename, InputStream inputStream) {
        String filePath = builderFilePath(prefix, filename);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType("text/html")
                    .bucket(minIOConfigProperties.getBucket()).stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            return minIOConfigProperties.getReadPath() + SEPARATOR + minIOConfigProperties.getBucket() +
                    SEPARATOR +
                    filePath;
        } catch (Exception ex) {
            log.error("minio put file error.", ex);
            throw new RuntimeException("上传文件失败");
        }
    }

    /**
     * 删除文件
     *
     * @param pathUrl 文件全路径
     */
    @Override
    public void delete(String pathUrl) {
        String key = pathUrl.replace(minIOConfigProperties.getEndpoint() + "/", "");
        int index = key.indexOf(SEPARATOR);
        String bucket = key.substring(0, index);
        String filePath = key.substring(index + 1);
        // 删除Objects
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucket).object(filePath).build();
        try {
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("minio remove file error. pathUrl:{}", pathUrl);
            throw new RuntimeException("删除文件失败");
        }
    }

    /**
     * 下载文件
     *
     * @param pathUrl 文件全路径
     * @return byte[]
     */
    @Override
    public byte[] downLoadFile(String pathUrl) {
        String key = pathUrl.replace(minIOConfigProperties.getEndpoint() + "/", "");
        int index = key.indexOf(SEPARATOR);
        String bucket = key.substring(0, index);
        String filePath = key.substring(index + 1);
        InputStream inputStream = null;
        try {
            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(minIOConfigProperties.getBucket()).object(filePath).build());
        } catch (Exception e) {
            log.error("minio down file error.  pathUrl:{}", pathUrl);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc;
        while (true) {
            try {
                if (!((rc = inputStream.read(buff, 0, 100)) > 0)) {
                    break;
                }
            } catch (IOException e) {
                log.error("minio download file error.", e);
                throw new RuntimeException("下载文件失败");
            }
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
