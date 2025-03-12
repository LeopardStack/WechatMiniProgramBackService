package com.scnu.wechatminiprogrambackservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnu.wechatminiprogrambackservice.dto.LoginParam;
import com.scnu.wechatminiprogrambackservice.dto.UserDTO;
import com.scnu.wechatminiprogrambackservice.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param loginParam 登录参数
     * @return 登录成功返回用户信息，失败返回null
     */
    UserDTO login(LoginParam loginParam);

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户DTO
     */
    UserDTO getUserById(Long userId);

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户DTO
     */
    UserDTO getUserByUsername(String username);
}