package com.ahead.distribution.common.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product {
    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private String detail;
    private String cover;
    private BigDecimal price;
    private BigDecimal marketPrice;
    private BigDecimal costPrice;
    private Integer stock;
    private Integer salesCount;
    private BigDecimal commissionAmount;
    private Integer commissionType;
    private Integer status;
    private Integer sortOrder;
    private Integer createdAt;
    private Integer updatedAt;
}
