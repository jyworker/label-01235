package com.tourism.controller.admin;

import com.tourism.common.PageResult;
import com.tourism.common.Result;
import com.tourism.entity.User;
import com.tourism.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端用户控制器
 */
@Api(tags = "管理端-用户管理")
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户列表")
    @GetMapping("/list")
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<User> result = userService.listUsers(pageNum, pageSize, keyword, status);
        return Result.success(result);
    }

    @ApiOperation("启用/禁用用户")
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        Integer status = body != null ? body.get("status") : null;
        if (status == null) {
            throw new com.tourism.common.BusinessException("状态不能为空");
        }
        userService.updateUserStatus(id, status);
        return Result.success("操作成功", null);
    }
}
