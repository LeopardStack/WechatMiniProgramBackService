package com.scnu.wechatminiprogrambackservice.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户展示对象
 */
@Data
public class UserVO {

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
     * 手机号
     */
    private String phone;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 用户状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}