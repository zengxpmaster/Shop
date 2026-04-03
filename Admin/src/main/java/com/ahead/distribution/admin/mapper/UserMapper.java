package com.ahead.distribution.admin.mapper;

import com.ahead.distribution.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findById(@Param("id") Long id);
    User findByUsername(@Param("username") String username);
    User findByPhone(@Param("phone") String phone);
    List<User> list(@Param("page") int page, @Param("size") int size,
                    @Param("keyword") String keyword, @Param("status") Integer status);
    int count(@Param("keyword") String keyword, @Param("status") Integer status);
    int update(User user);
    int insert(User user);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
