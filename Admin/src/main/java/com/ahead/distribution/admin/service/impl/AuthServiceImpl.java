package com.ahead.distribution.admin.service.impl;

import com.ahead.distribution.admin.dto.LoginRequest;
import com.ahead.distribution.admin.dto.LoginResponse;
import com.ahead.distribution.admin.mapper.UserMapper;
import com.ahead.distribution.admin.service.AuthService;
import com.ahead.distribution.admin.util.JwtUtil;
import com.ahead.distribution.common.entity.User;
import com.ahead.distribution.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(401, "Invalid username or password");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "Invalid username or password");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(403, "Account is disabled");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(token, user.getUsername(), user.getId(), user.getUserType());
    }

    @Override
    public void logout(String token) {
        if (token != null && !token.isBlank()) {
            redisTemplate.opsForValue().set(
                TOKEN_BLACKLIST_PREFIX + token, "1", 7200, TimeUnit.SECONDS);
        }
    }
}
