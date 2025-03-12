package com.scnu.wechatminiprogrambackservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scnu.wechatminiprogrambackservice.dto.UserDTO;
import com.scnu.wechatminiprogrambackservice.entity.User;
import com.scnu.wechatminiprogrambackservice.model.R;
import com.scnu.wechatminiprogrambackservice.service.UserService;
import com.scnu.wechatminiprogrambackservice.util.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    public R<Page<UserDTO>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "status", required = false) Integer status) {

        // 构建分页对象
        Page<User> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (StringUtils.hasText(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(phone)) {
            queryWrapper.like(User::getPhone, phone);
        }
        if (status != null) {
            queryWrapper.eq(User::getStatus, status);
        }

        // 设置排序
        queryWrapper.orderByDesc(User::getCreateTime);

        // 执行查询
        userService.page(page, queryWrapper);

        // 转换结果
        List<UserDTO> userDTOList = BeanCopyUtils.copyList(page.getRecords(), UserDTO.class);
        Page<UserDTO> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(userDTOList);

        return R.success(resultPage);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public R<UserDTO> getById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);

        if (userDTO != null) {
            return R.success(userDTO);
        } else {
            return R.error(404, "用户不存在");
        }
    }

    /**
     * 新增用户
     */
    @PostMapping
    public R<UserDTO> save(@RequestBody User user) {
        // 检查用户名是否已存在
        UserDTO existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser != null) {
            return R.error(400, "用户名已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 保存用户
        boolean success = userService.save(user);

        if (success) {
            UserDTO userDTO = BeanCopyUtils.copyObject(user, UserDTO.class);
            return R.success("用户创建成功", userDTO);
        } else {
            return R.error("用户创建失败");
        }
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public R<UserDTO> update(@PathVariable Long id, @RequestBody User user) {
        // 检查用户是否存在
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return R.error(404, "用户不存在");
        }

        // 设置ID
        user.setId(id);

        // 如果密码字段不为空，则加密密码
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 否则，保持原密码不变
            user.setPassword(existingUser.getPassword());
        }

        // 更新用户
        boolean success = userService.updateById(user);

        if (success) {
            UserDTO userDTO = BeanCopyUtils.copyObject(user, UserDTO.class);
            return R.success("用户更新成功", userDTO);
        } else {
            return R.error("用户更新失败");
        }
    }

    /**
     * 删除用户（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        boolean success = userService.removeById(id);

        if (success) {
            return R.success("用户删除成功");
        } else {
            return R.error("用户删除失败");
        }
    }

    /**
     * 修改用户状态
     */
    @PatchMapping("/{id}/status/{status}")
    public R<String> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        // 检查状态值
        if (status != 0 && status != 1) {
            return R.error(400, "无效的状态值");
        }

        // 构建更新对象
        User user = new User();
        user.setId(id);
        user.setStatus(status);

        // 更新状态
        boolean success = userService.updateById(user);

        if (success) {
            return R.success(status == 1 ? "用户已启用" : "用户已禁用");
        } else {
            return R.error("状态更新失败");
        }
    }

    /**
     * 重置密码
     */
    @PatchMapping("/{id}/password/reset")
    public R<String> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        // 检查密码是否为空
        if (!StringUtils.hasText(newPassword)) {
            return R.error(400, "密码不能为空");
        }

        // 检查用户是否存在
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return R.error(404, "用户不存在");
        }

        // 构建更新对象
        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(newPassword));

        // 更新密码
        boolean success = userService.updateById(user);

        if (success) {
            return R.success("密码重置成功");
        } else {
            return R.error("密码重置失败");
        }
    }
}