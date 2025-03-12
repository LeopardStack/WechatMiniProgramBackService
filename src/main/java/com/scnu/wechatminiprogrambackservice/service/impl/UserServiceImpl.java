package com.scnu.wechatminiprogrambackservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnu.wechatminiprogrambackservice.dto.LoginParam;
import com.scnu.wechatminiprogrambackservice.dto.UserDTO;
import com.scnu.wechatminiprogrambackservice.entity.User;
import com.scnu.wechatminiprogrambackservice.mapper.UserMapper;
import com.scnu.wechatminiprogrambackservice.service.UserService;
import com.scnu.wechatminiprogrambackservice.util.BeanCopyUtils;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDTO login(LoginParam loginParam) {
        // 根据用户名查询用户
        User user = baseMapper.selectByUsername(loginParam.getUsername());

        // 用户不存在或已被禁用
        if (user == null || user.getStatus() == 0) {
            return null;
        }

        // 验证密码
        if (!passwordEncoder.matches(loginParam.getPassword(), user.getPassword())) {
            return null;
        }

        // 转换为DTO
        return BeanCopyUtils.copyObject(user, UserDTO.class);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        User user = getById(userId);
        return user != null ? BeanCopyUtils.copyObject(user, UserDTO.class) : null;
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = baseMapper.selectByUsername(username);
        return user != null ? BeanCopyUtils.copyObject(user, UserDTO.class) : null;
    }
}