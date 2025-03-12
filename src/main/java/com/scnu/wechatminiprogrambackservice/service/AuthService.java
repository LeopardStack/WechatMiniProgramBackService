package com.scnu.wechatminiprogrambackservice.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.scnu.wechatminiprogrambackservice.config.ApiKeyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private ApiKeyConfig apiKeyConfig;

    /**
     * 验证API密钥并生成Token
     * @param clientId 客户端ID
     * @param apiKey API密钥
     * @return Token信息
     */
    public SaTokenInfo authenticateApiClient(String clientId, String apiKey) {
        // 调试日志
        System.out.println("正在验证客户端凭据: clientId=" + clientId);
        System.out.println("当前配置的API密钥: " + apiKeyConfig.getKeys());

        // 验证API密钥是否有效
        String expectedApiKey = apiKeyConfig.getKeys().get(clientId);
        if (expectedApiKey == null) {
            System.out.println("验证失败: 客户端ID不存在");
            throw new RuntimeException("无效的API凭据: 客户端ID不存在");
        }

        if (!expectedApiKey.equals(apiKey)) {
            System.out.println("验证失败: API密钥不匹配");
            throw new RuntimeException("无效的API凭据: API密钥不匹配");
        }

        System.out.println("验证成功，生成token");

        // 登录（生成token）
        StpUtil.login(clientId);

        // 返回token信息
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        System.out.println("生成的token: " + tokenInfo.getTokenValue());

        return tokenInfo;
    }
}
