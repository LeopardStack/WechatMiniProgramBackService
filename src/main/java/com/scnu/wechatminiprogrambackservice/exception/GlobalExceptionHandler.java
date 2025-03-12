package com.scnu.wechatminiprogrambackservice.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.scnu.wechatminiprogrambackservice.model.ApiResult;
import com.scnu.wechatminiprogrambackservice.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理无权限异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public R<Void> handleNotPermissionException(NotPermissionException e) {
        log.error("无权限访问: {}", e.getMessage());
        return R.error(403, "无权限访问");
    }

    /**
     * 处理无角色异常
     */
    @ExceptionHandler(NotRoleException.class)
    public R<Void> handleNotRoleException(NotRoleException e) {
        log.error("无角色访问: {}", e.getMessage());
        return R.error(403, "无角色访问");
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return R.error("系统异常: " + e.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public ApiResult<?> handleNotLoginException(NotLoginException e) {
        return ApiResult.error(401, "未登录或token已过期");
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResult<?> handleRuntimeException(RuntimeException e) {
        return ApiResult.error(500, e.getMessage());
    }
}