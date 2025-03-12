package com.scnu.wechatminiprogrambackservice.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.scnu.wechatminiprogrambackservice.dto.LoginParam;
import com.scnu.wechatminiprogrambackservice.dto.UserDTO;
import com.scnu.wechatminiprogrambackservice.model.ApiAuthRequest;
import com.scnu.wechatminiprogrambackservice.model.ApiResult;
import com.scnu.wechatminiprogrambackservice.model.R;
import com.scnu.wechatminiprogrambackservice.service.AuthService;
import com.scnu.wechatminiprogrambackservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Autowired
    private AuthService authService;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginParam loginParam) {
        // 调用用户服务进行登录验证
        UserDTO userDTO = userService.login(loginParam);

        if (userDTO != null) {
            // 登录成功，生成token
            StpUtil.login(userDTO.getId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

            // 返回用户信息和token
            Map<String, Object> result = new HashMap<>();
            result.put("user", userDTO);
            result.put("token", tokenInfo);

            return R.success("登录成功", result);
        } else {
            // 登录失败
            return R.error(400, "用户名或密码错误");
        }
    }

    /**
     * 登出接口
     */
    @PostMapping("/logout")
    public R<String> logout() {
        StpUtil.logout();
        return R.success("登出成功");
    }

    /**
     * 获取用户信息接口
     */
    @GetMapping("/info")
    public R<UserDTO> getUserInfo() {
        // 检查登录状态
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            UserDTO userDTO = userService.getUserById(userId);

            if (userDTO != null) {
                return R.success("获取用户信息成功", userDTO);
            } else {
                return R.error(404, "用户不存在");
            }
        } else {
            return R.error(401, "用户未登录");
        }
    }

    @PostMapping("/api-token")
    public ApiResult<SaTokenInfo> getApiToken(@RequestBody ApiAuthRequest request) {
       log.info("收到API认证请求: " + request.getClientId());
        try {
            SaTokenInfo tokenInfo = authService.authenticateApiClient(request.getClientId(), request.getApiKey());
            return ApiResult.success(tokenInfo);
        } catch (Exception e) {
            log.info("认证失败: " + e.getMessage());
            return ApiResult.error(401, e.getMessage());
        }
    }
}