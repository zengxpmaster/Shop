package com.ahead.distribution.admin.service;

import com.ahead.distribution.admin.dto.PageResult;
import com.ahead.distribution.common.entity.User;

public interface UserService {
    User findById(Long id);
    PageResult<User> list(int page, int size, String keyword, Integer status);
    void update(Long id, User user);
    void updateStatus(Long id, Integer status);
}
