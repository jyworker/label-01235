package com.tourism.controller.admin;

import com.tourism.common.Result;
import com.tourism.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理端文件上传控制器
 */
@Api(tags = "管理端-文件上传")
@RestController
@RequestMapping("/api/admin/file")
public class AdminFileController {

    @Autowired
    private FileService fileService;

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        return Result.success("上传成功", url);
    }
}
