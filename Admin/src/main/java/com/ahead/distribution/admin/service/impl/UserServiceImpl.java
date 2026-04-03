package com.ahead.distribution.admin.service.impl;

import com.ahead.distribution.admin.dto.PageResult;
import com.ahead.distribution.admin.mapper.UserMapper;
import com.ahead.distribution.admin.service.UserService;
import com.ahead.distribution.common.entity.User;
import com.ahead.distribution.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(404, "User not found");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public PageResult<User> list(int page, int size, String keyword, Integer status) {
        int offset = (page - 1) * size;
        List<User> users = userMapper.list(offset, size, keyword, status);
        users.forEach(u -> u.setPassword(null));
        int total = userMapper.count(keyword, status);
        return new PageResult<>(users, total, page, size);
    }

    @Override
    public void update(Long id, User user) {
        User existing = userMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(404, "User not found");
        }
        user.setId(id);
        user.setUpdatedAt((int) (System.currentTimeMillis() / 1000));
        userMapper.update(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        User existing = userMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(404, "User not found");
        }
        userMapper.updateStatus(id, status);
    }
}
