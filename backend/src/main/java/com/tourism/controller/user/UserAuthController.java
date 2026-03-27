package com.tourism.controller.user;

import com.tourism.common.Result;
import com.tourism.dto.LoginDTO;
import com.tourism.dto.PasswordDTO;
import com.tourism.dto.RegisterDTO;
import com.tourism.dto.UserUpdateDTO;
import com.tourism.entity.User;
import com.tourism.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;

/**
 * 用户认证控制器
 */
@Api(tags = "用户端-认证接口")
@RestController
@RequestMapping("/api/user")
public class UserAuthController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterDTO dto) {
        Map<String, Object> result = userService.register(dto);
        return Result.success("注册成功", result);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        Map<String, Object> result = userService.login(dto);
        return Result.success("登录成功", result);
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Result<?> logout() {
        userService.logout();
        return Result.success("登出成功", null);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        User user = userService.getCurrentUser();
        return Result.success(user);
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/info")
    public Result<?> updateUserInfo(@RequestBody UserUpdateDTO dto) {
        userService.updateUserInfo(dto);
        return Result.success("更新成功", null);
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<?> updatePassword(@Valid @RequestBody PasswordDTO dto) {
        userService.updatePassword(dto);
        return Result.success("密码修改成功", null);
    }

    @ApiOperation("上传头像")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadAvatar(file);
        return Result.success("头像上传成功", avatarUrl);
    }
}
