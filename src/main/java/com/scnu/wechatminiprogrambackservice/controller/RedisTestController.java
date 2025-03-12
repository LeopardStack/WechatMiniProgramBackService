package com.scnu.wechatminiprogrambackservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis测试控制器
 */
@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisTestController {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 存储数据到Redis
     */
    @PostMapping("/set")
    public Map<String, Object> set(@RequestParam String key, @RequestParam String value, @RequestParam(required = false, defaultValue = "60") long timeout) {
        Map<String, Object> result = new HashMap<>();

        try {
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            result.put("code", 200);
            result.put("message", "数据存储成功");
            result.put("data", value);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "数据存储失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 从Redis获取数据
     */
    @GetMapping("/get")
    public Map<String, Object> get(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();

        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                result.put("code", 200);
                result.put("message", "数据获取成功");
                result.put("data", value);
            } else {
                result.put("code", 404);
                result.put("message", "数据不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "数据获取失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 从Redis删除数据
     */
    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();

        try {
            Boolean deleted = redisTemplate.delete(key);
            result.put("code", 200);
            result.put("message", deleted ? "数据删除成功" : "键不存在");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "数据删除失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 测试Redis连接
     */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> result = new HashMap<>();

        try {
            String pingResult = redisTemplate.getConnectionFactory().getConnection().ping();
            result.put("code", 200);
            result.put("message", "Redis连接成功");
            result.put("data", pingResult);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "Redis连接失败: " + e.getMessage());
        }

        return result;
    }
}