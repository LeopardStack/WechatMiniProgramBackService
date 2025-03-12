package com.scnu.wechatminiprogrambackservice.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.scnu.wechatminiprogrambackservice.model.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/data")
    @SaCheckLogin  // 需要登录才能访问
    public ApiResult<String> getData() {
        String clientId = StpUtil.getLoginIdAsString();
        return ApiResult.success("Data for client: " + clientId);
    }

    @GetMapping("/public/info")
    public ApiResult<String> getPublicInfo() {
        return ApiResult.success("This is public information");
    }
}