package com.ahead.distribution.common.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSku {
    private Long id;
    private Long productId;
    private String skuName;
    private String skuCode;
    private String specs;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private Integer createdAt;
    private Integer updatedAt;
}
