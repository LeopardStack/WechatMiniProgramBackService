package com.scnu.wechatminiprogrambackservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户数据传输对象
 */
@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * A用户头像URL
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户状态：0-禁用，1-正常
     */
    private Integer status;
}