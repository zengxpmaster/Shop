package com.ahead.distribution.common.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String nickname;
    private String avatar;
    private Integer gender;
    private Integer status;
    private Integer userType;
    private Integer lastLoginAt;
    private String lastLoginIp;
    private Integer createdAt;
    private Integer updatedAt;
}
