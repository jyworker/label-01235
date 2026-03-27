package com.tourism.controller.admin;

import com.tourism.common.Result;
import com.tourism.dto.LoginDTO;
import com.tourism.entity.Admin;
import com.tourism.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 管理端认证控制器
 */
@Api(tags = "管理端-认证接口")
@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    @Autowired
    private AdminService adminService;

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        Map<String, Object> result = adminService.login(dto);
        return Result.success("登录成功", result);
    }

    @ApiOperation("管理员登出")
    @PostMapping("/logout")
    public Result<?> logout() {
        adminService.logout();
        return Result.success("登出成功", null);
    }

    @ApiOperation("获取当前管理员信息")
    @GetMapping("/info")
    public Result<Admin> getAdminInfo() {
        Admin admin = adminService.getCurrentAdmin();
        return Result.success(admin);
    }
}
