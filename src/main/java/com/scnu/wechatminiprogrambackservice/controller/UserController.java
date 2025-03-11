package com.scnu.wechatminiprogrambackservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scnu.wechatminiprogrambackservice.common.Result;
import com.scnu.wechatminiprogrambackservice.entity.UserPO;
import com.scnu.wechatminiprogrambackservice.service.UserService;
import com.scnu.wechatminiprogrambackservice.entity.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    @Resource
    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户信息")
    public Result<UserVO> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        UserPO user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在", 404);
        }
        UserVO userVO = convertToVO(user);
        return Result.success(userVO);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取用户列表")
    public Result<IPage<UserVO>> getUserPage(
            @Parameter(description = "页码", required = true)
            @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页记录数", required = true)
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词（用户名或昵称）")
            @RequestParam(required = false) String keyword) {
        Page<UserPO> page = new Page<>(current, size);
        IPage<UserPO> userPage = userService.getUserPage(page, keyword);

        // 转换为VO对象
        IPage<UserVO> voPage = userPage.convert(this::convertToVO);
        return Result.success(voPage);
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public Result<Long> createUser(@RequestBody UserPO user) {
        Long userId = userService.createUser(user);
        return Result.success(userId, "用户创建成功");
    }

    @PutMapping
    @Operation(summary = "更新用户信息")
    public Result<Void> updateUser(@RequestBody UserPO user) {
        if (user.getId() == null) {
            return Result.error("用户ID不能为空");
        }

        boolean updated = userService.updateUser(user);
        if (updated) {
            return Result.success(null, "用户更新成功");
        } else {
            return Result.error("用户更新失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return Result.success(null, "用户删除成功");
        } else {
            return Result.error("用户删除失败");
        }
    }

    /**
     * 将User实体转换为UserVO
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO convertToVO(UserPO user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}