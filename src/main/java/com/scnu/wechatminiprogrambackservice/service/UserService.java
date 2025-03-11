package com.scnu.wechatminiprogrambackservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scnu.wechatminiprogrambackservice.entity.UserPO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    UserPO getUserById(Long id);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    UserPO getUserByUsername(String username);

    /**
     * 分页查询用户列表
     * @param page 分页参数
     * @param keyword 搜索关键词（用户名或昵称）
     * @return 分页用户列表
     */
    IPage<UserPO> getUserPage(Page<UserPO> page, String keyword);

    /**
     * 创建用户
     * @param user 用户对象
     * @return 创建的用户ID
     */
    Long createUser(UserPO user);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 是否更新成功
     */
    boolean updateUser(UserPO user);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);
}