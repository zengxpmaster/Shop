package com.ahead.distribution.admin.mapper;

import com.ahead.distribution.common.entity.OrderProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderProductMapper {
    List<OrderProduct> findByOrderId(@Param("orderId") Long orderId);
    int insert(OrderProduct orderProduct);
}
