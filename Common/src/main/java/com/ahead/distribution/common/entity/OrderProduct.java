package com.ahead.distribution.common.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderProduct {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long productSkuId;
    private String productName;
    private String productCover;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private BigDecimal commissionAmount;
    private Integer commissionType;
    private String specs;
    private Integer createdAt;
}
