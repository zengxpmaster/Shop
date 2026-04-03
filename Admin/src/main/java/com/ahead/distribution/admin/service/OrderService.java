package com.ahead.distribution.admin.service;

import com.ahead.distribution.admin.dto.PageResult;
import com.ahead.distribution.common.entity.Order;

public interface OrderService {
    Order findById(Long id);
    PageResult<Order> list(int page, int size, Long userId, Integer status);
    void ship(Long id, String deliverySn, String deliveryCompany);
    void cancel(Long id, String cancelReason);
}
