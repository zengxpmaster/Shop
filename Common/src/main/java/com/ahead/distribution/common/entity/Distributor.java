package com.ahead.distribution.common.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Distributor {
    private Long id;
    private Long userId;
    private Long levelId;
    private String inviteCode;
    private BigDecimal totalCommission;
    private BigDecimal withdrawableAmount;
    private BigDecimal frozenAmount;
    private BigDecimal totalWithdrawn;
    private Integer status;
    private Integer approved;
    private Integer createdAt;
    private Integer updatedAt;
}
