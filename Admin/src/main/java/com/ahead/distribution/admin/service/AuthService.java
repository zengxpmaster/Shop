package com.ahead.distribution.admin.service;

import com.ahead.distribution.admin.dto.LoginRequest;
import com.ahead.distribution.admin.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
}
