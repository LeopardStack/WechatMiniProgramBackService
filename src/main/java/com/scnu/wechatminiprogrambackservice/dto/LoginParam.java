package com.scnu.wechatminiprogrambackservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录参数
 */
@Data
public class LoginParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}