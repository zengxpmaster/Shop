package com.ahead.distribution.admin.controller;

import com.ahead.distribution.admin.dto.CancelRequest;
import com.ahead.distribution.admin.dto.PageResult;
import com.ahead.distribution.admin.dto.ShipRequest;
import com.ahead.distribution.admin.service.OrderService;
import com.ahead.distribution.common.entity.Order;
import com.ahead.distribution.common.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public R<PageResult<Order>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status) {
        return R.ok(orderService.list(page, size, userId, status));
    }

    @GetMapping("/{id}")
    public R<Order> getById(@PathVariable Long id) {
        return R.ok(orderService.findById(id));
    }

    @PostMapping("/{id}/ship")
    public R<Void> ship(@PathVariable Long id, @RequestBody ShipRequest request) {
        orderService.ship(id, request.getDeliverySn(), request.getDeliveryCompany());
        return R.ok();
    }

    @PostMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id, @RequestBody CancelRequest request) {
        orderService.cancel(id, request.getCancelReason());
        return R.ok();
    }
}
