package com.scnu.wechatminiprogrambackservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scnu.wechatminiprogrambackservice.entity.UserPO;
import com.scnu.wechatminiprogrambackservice.mapper.UserMapper;
import com.scnu.wechatminiprogrambackservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public UserPO getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserPO getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public IPage<UserPO> getUserPage(Page<UserPO> page, String keyword) {
        LambdaQueryWrapper<UserPO> queryWrapper = new LambdaQueryWrapper<>();

        // 添加搜索条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(UserPO::getUsername, keyword)
                    .or()
                    .like(UserPO::getNickname, keyword);
        }

        // 添加排序
        queryWrapper.orderByDesc(UserPO::getCreateTime);

        return userMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserPO user) {
        // 业务逻辑检查（如检查用户名是否存在）可以在这里添加
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(UserPO user) {
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }
}