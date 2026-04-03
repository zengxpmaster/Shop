package com.ahead.distribution.admin.mapper;

import com.ahead.distribution.common.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    Order findById(@Param("id") Long id);
    Order findByOrderNo(@Param("orderNo") String orderNo);
    List<Order> list(@Param("page") int page, @Param("size") int size,
                     @Param("userId") Long userId, @Param("status") Integer status);
    int count(@Param("userId") Long userId, @Param("status") Integer status);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status,
                     @Param("deliverySn") String deliverySn,
                     @Param("deliveryCompany") String deliveryCompany);
    int insert(Order order);
}
