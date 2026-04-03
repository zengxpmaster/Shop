package com.ahead.distribution.admin.service.impl;

import com.ahead.distribution.admin.dto.PageResult;
import com.ahead.distribution.admin.mapper.OrderMapper;
import com.ahead.distribution.admin.service.OrderService;
import com.ahead.distribution.common.entity.Order;
import com.ahead.distribution.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order findById(Long id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException(404, "Order not found");
        }
        return order;
    }

    @Override
    public PageResult<Order> list(int page, int size, Long userId, Integer status) {
        int offset = (page - 1) * size;
        List<Order> orders = orderMapper.list(offset, size, userId, status);
        int total = orderMapper.count(userId, status);
        return new PageResult<>(orders, total, page, size);
    }

    @Override
    public void ship(Long id, String deliverySn, String deliveryCompany) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException(404, "Order not found");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(400, "Order cannot be shipped in its current status");
        }
        // status 2 = shipped/待收货
        orderMapper.updateStatus(id, 2, deliverySn, deliveryCompany);
    }

    @Override
    public void cancel(Long id, String cancelReason) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException(404, "Order not found");
        }
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new BusinessException(400, "Order cannot be cancelled in its current status");
        }
        // status 4 = cancelled
        orderMapper.updateStatus(id, 4, null, null);
    }
}
