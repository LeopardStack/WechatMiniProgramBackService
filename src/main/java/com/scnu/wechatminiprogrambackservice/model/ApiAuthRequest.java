package com.scnu.wechatminiprogrambackservice.model;

import lombok.Data;

@Data
public class ApiAuthRequest {
    private String clientId;
    private String apiKey;
}