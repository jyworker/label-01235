package com.tourism.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileService {

    /**
     * 上传文件
     */
    String upload(MultipartFile file);
}
