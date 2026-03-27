package com.tourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tourism.dto.LoginDTO;
import com.tourism.entity.Admin;

import java.util.Map;

/**
 * 管理员服务接口
 */
public interface AdminService extends IService<Admin> {

    /**
     * 管理员登录
     */
    Map<String, Object> login(LoginDTO dto);

    /**
     * 管理员登出
     */
    void logout();

    /**
     * 获取当前管理员信息
     */
    Admin getCurrentAdmin();
}
