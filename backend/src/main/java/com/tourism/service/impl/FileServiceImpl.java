package com.tourism.service.impl;

import com.tourism.common.BusinessException;
import com.tourism.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${upload.path:./uploads/}")
    private String uploadPath;

    @Value("${upload.url-prefix:/uploads/}")
    private String urlPrefix;

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 按日期分目录
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path uploadRoot = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path datePath = uploadRoot.resolve(dateDir);

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path destPath = datePath.resolve(fileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.createDirectories(datePath);
            Files.copy(inputStream, destPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("文件上传失败: path={}", destPath, e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }

        // 返回访问URL
        String url = urlPrefix + dateDir + "/" + fileName;
        log.info("文件上传成功: {}", url);
        return url;
    }
}
