package com.tourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tourism.common.PageResult;
import com.tourism.dto.LoginDTO;
import com.tourism.dto.PasswordDTO;
import com.tourism.dto.RegisterDTO;
import com.tourism.dto.UserUpdateDTO;
import com.tourism.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册（注册成功后自动登录，返回 token 和 userInfo）
     */
    Map<String, Object> register(RegisterDTO dto);

    /**
     * 用户登录
     */
    Map<String, Object> login(LoginDTO dto);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前用户信息
     */
    User getCurrentUser();

    /**
     * 更新用户信息
     */
    void updateUserInfo(UserUpdateDTO dto);

    /**
     * 修改密码
     */
    void updatePassword(PasswordDTO dto);

    /**
     * 上传头像
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 用户列表（管理端）
     */
    PageResult<User> listUsers(Integer pageNum, Integer pageSize, String keyword, Integer status);

    /**
     * 启用/禁用用户（管理端）
     */
    void updateUserStatus(Long userId, Integer status);
}
