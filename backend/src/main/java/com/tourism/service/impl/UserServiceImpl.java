package com.tourism.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.tourism.config.StpUserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tourism.common.BusinessException;
import com.tourism.common.PageResult;
import com.tourism.dto.LoginDTO;
import com.tourism.dto.PasswordDTO;
import com.tourism.dto.RegisterDTO;
import com.tourism.dto.UserUpdateDTO;
import com.tourism.entity.User;
import com.tourism.mapper.UserMapper;
import com.tourism.service.FileService;
import com.tourism.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private FileService fileService;

    @Override
    public Map<String, Object> register(RegisterDTO dto) {
        // 检查用户名是否已存在
        long count = this.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(1);
        user.setGender(0);
        this.save(user);

        // 注册成功后自动登录
        StpUserUtil.login(user.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUserUtil.getTokenValue());
        result.put("userInfo", getSafeUser(user));

        log.info("用户注册成功: username={}", dto.getUsername());
        return result;
    }

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        // 根据用户名查询用户
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // Sa-Token 登录
        StpUserUtil.login(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUserUtil.getTokenValue());
        result.put("userInfo", getSafeUser(user));

        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());
        return result;
    }

    @Override
    public void logout() {
        StpUserUtil.logout();
    }

    @Override
    public User getCurrentUser() {
        Long userId = StpUserUtil.getLoginIdAsLong();
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return getSafeUser(user);
    }

    @Override
    public void updateUserInfo(UserUpdateDTO dto) {
        Long userId = StpUserUtil.getLoginIdAsLong();
        User user = new User();
        user.setId(userId);
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
        this.updateById(user);

        log.info("用户更新信息: userId={}", userId);
    }

    @Override
    public void updatePassword(PasswordDTO dto) {
        Long userId = StpUserUtil.getLoginIdAsLong();
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }

        // 更新密码
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(BCrypt.hashpw(dto.getNewPassword()));
        this.updateById(updateUser);

        log.info("用户修改密码: userId={}", userId);
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        Long userId = StpUserUtil.getLoginIdAsLong();
        String avatarUrl = fileService.upload(file);

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setAvatar(avatarUrl);
        this.updateById(updateUser);

        log.info("用户上传头像: userId={}, avatarUrl={}", userId, avatarUrl);
        return avatarUrl;
    }

    @Override
    public PageResult<User> listUsers(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = this.page(page, wrapper);

        // 清除密码
        result.getRecords().forEach(u -> u.setPassword(null));

        return PageResult.of(result.getRecords(), result.getTotal(),
                pageNum, pageSize);
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setStatus(status);
        this.updateById(updateUser);

        log.info("管理员更新用户状态: userId={}, status={}", userId, status);
    }

    /**
     * 返回不含密码的用户对象
     */
    private User getSafeUser(User user) {
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setNickname(user.getNickname());
        safeUser.setAvatar(user.getAvatar());
        safeUser.setEmail(user.getEmail());
        safeUser.setPhone(user.getPhone());
        safeUser.setGender(user.getGender());
        safeUser.setStatus(user.getStatus());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUpdateTime(user.getUpdateTime());
        return safeUser;
    }
}
