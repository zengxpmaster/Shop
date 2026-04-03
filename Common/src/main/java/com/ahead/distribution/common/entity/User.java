package com.ahead.distribution.common.entity;

import lombok.Data;

@Data
public class User {
    Long id;
    String username;
    String password;
    String nickname;
    Integer status;
    Integer created_at;
    Integer updated_at;
}
