package com.scnu.wechatminiprogrambackservice.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RateLimitService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 对指定IP地址在指定秒数内限制请求数量为maxLimit次
     */
    public void limitIpInSeconds(String ipAddr, int timeOut, int maxLimit) {
        limit(ipAddr, timeOut, maxLimit);
    }

    private void limit(String ipAddr, int timeOut, int maxLimit) {
        String limitKey = "global:limit:ip:" + ipAddr;

        // 使用 RedisTemplate 的 increment 方法增加计数器
        Long currentCount = redisTemplate.opsForValue().increment(limitKey);

        // 如果是第一次设置，则设置过期时间
        if (currentCount == 1) {
            redisTemplate.expire(limitKey, timeOut, TimeUnit.SECONDS);
        }

        // 检查是否超过最大限制
        if (currentCount != null && currentCount > maxLimit) {
            log.error("IP {} request count {} exceeds the limit of {}", ipAddr, currentCount, maxLimit);
            throw new CustomFlowControlException(ApiResultCode.FLOW_CONTROL_ERROR);
        }
    }

    public class CustomFlowControlException extends RuntimeException {
        private ApiResultCode apiResultCode;

        public CustomFlowControlException(ApiResultCode apiResultCode) {
            super(apiResultCode.getMessage());
            this.apiResultCode = apiResultCode;
        }

        public ApiResultCode getApiResultCode() {
            return apiResultCode;
        }
    }

    public enum ApiResultCode {
        FLOW_CONTROL_ERROR(429, "Too many requests, please try again later.");

        private final int code;
        private final String message;

        ApiResultCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

    }
}