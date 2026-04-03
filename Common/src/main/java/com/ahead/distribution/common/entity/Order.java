package com.ahead.distribution.common.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long distributorId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal freightAmount;
    private BigDecimal discountAmount;
    private BigDecimal commissionAmount;
    private String contactName;
    private String contactPhone;
    private String province;
    private String city;
    private String district;
    private String address;
    private String remark;
    private Integer status;
    private Integer payStatus;
    private String payType;
    private Integer paidAt;
    private Integer shipedAt;
    private String deliverySn;
    private String deliveryCompany;
    private Integer receivedAt;
    private Integer confirmedEd;
    private Integer canceledAt;
    private String cancelReason;
    private Integer createdAt;
    private Integer updatedAt;
}
