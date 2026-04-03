package com.ahead.distribution.common.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DistributorLevel {
    private Long id;
    private String name;
    private BigDecimal minSalesAmount;
    private BigDecimal commissionRate;
    private String description;
    private Integer status;
    private Integer createdAt;
    private Integer updatedAt;
}
