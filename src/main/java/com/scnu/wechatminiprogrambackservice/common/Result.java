package com.scnu.wechatminiprogrambackservice.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回结果
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 构造私有化
     */
    private Result() {}

    /**
     * 成功返回结果
     * @param <T> 数据类型
     * @return 返回结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功返回结果
     * @param data 数据
     * @param <T> 数据类型
     * @return 返回结果
     */
    public static <T> Result<T> success(T data) {
        return success(data, "操作成功");
    }

    /**
     * 成功返回结果
     * @param data 数据
     * @param message 消息
     * @param <T> 数据类型
     * @return 返回结果
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败返回结果
     * @param <T> 数据类型
     * @return 返回结果
     */
    public static <T> Result<T> error() {
        return error("操作失败");
    }

    /**
     * 失败返回结果
     * @param message 消息
     * @param <T> 数据类型
     * @return 返回结果
     */
    public static <T> Result<T> error(String message) {
        return error(message, 500);
    }

    /**
     * 失败返回结果
     * @param message 消息
     * @param code 状态码
     * @param <T> 数据类型
     * @return 返回结果
     */
    public static <T> Result<T> error(String message, Integer code) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}