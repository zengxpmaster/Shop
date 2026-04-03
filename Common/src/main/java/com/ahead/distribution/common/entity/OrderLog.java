package com.ahead.distribution.common.entity;

import lombok.Data;

@Data
public class OrderLog {
    private Long id;
    private Long orderId;
    private Long userId;
    private Integer orderStatus;
    private String action;
    private String remark;
    private Integer createdAt;
}
