package com.tourism.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.tourism.config.StpAdminUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tourism.common.BusinessException;
import com.tourism.dto.LoginDTO;
import com.tourism.entity.Admin;
import com.tourism.mapper.AdminMapper;
import com.tourism.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        Admin admin = this.getOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, dto.getUsername()));
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!BCrypt.checkpw(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (admin.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 管理员登录（多账号体系）
        StpAdminUtil.login(admin.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", StpAdminUtil.getTokenValue());

        Admin safeAdmin = new Admin();
        safeAdmin.setId(admin.getId());
        safeAdmin.setUsername(admin.getUsername());
        safeAdmin.setNickname(admin.getNickname());
        safeAdmin.setAvatar(admin.getAvatar());
        safeAdmin.setRole(admin.getRole());
        result.put("adminInfo", safeAdmin);

        log.info("管理员登录成功: adminId={}, username={}", admin.getId(), admin.getUsername());
        return result;
    }

    @Override
    public void logout() {
        StpAdminUtil.logout();
    }

    @Override
    public Admin getCurrentAdmin() {
        Long adminId = StpAdminUtil.getLoginIdAsLong();
        Admin admin = this.getById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        // 返回不含密码的管理员信息
        Admin safeAdmin = new Admin();
        safeAdmin.setId(admin.getId());
        safeAdmin.setUsername(admin.getUsername());
        safeAdmin.setNickname(admin.getNickname());
        safeAdmin.setAvatar(admin.getAvatar());
        safeAdmin.setRole(admin.getRole());
        safeAdmin.setStatus(admin.getStatus());
        safeAdmin.setCreateTime(admin.getCreateTime());
        return safeAdmin;
    }
}
