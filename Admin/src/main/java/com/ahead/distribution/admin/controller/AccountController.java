package com.ahead.distribution.admin.controller;

import com.ahead.distribution.admin.dto.LoginRequest;
import com.ahead.distribution.admin.dto.LoginResponse;
import com.ahead.distribution.admin.service.AuthService;
import com.ahead.distribution.common.result.R;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AccountController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return R.ok(response);
    }

    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            authService.logout(authorization.substring(7));
        }
        return R.ok();
    }
}

