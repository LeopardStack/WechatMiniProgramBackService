package com.scnu.wechatminiprogrambackservice.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * Sa-Token配置类
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册Sa-Token拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册Sa-Token拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 指定需要登录认证的路径
            SaRouter.match("/**")
                    // 排除登录接口、公开接口等
                    .notMatch("/auth/api-token", "/auth/login", "/auth/register", "/public/**",
                            "/question/**")  // 确保添加了 /auth/api-token
                    // 检查是否登录
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }

    /**
     * 获取不需要登录认证的URL列表
     */
    private List<String> getExcludedUrls() {
        return Arrays.asList(
                "/hello",             // 健康检查接口
                "/auth/login",        // 登录接口
                "/auth/register",     // 注册接口
                "/redis/**",          // Redis测试接口
                "/doc.html",          // API文档
                "/swagger-ui/**",     // Swagger UI
                "/swagger-resources/**", // Swagger资源
                "/v3/api-docs/**"     // OpenAPI文档
        );
    }
}