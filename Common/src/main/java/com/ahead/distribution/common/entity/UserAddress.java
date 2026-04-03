package com.ahead.distribution.common.entity;

import lombok.Data;

@Data
public class UserAddress {
    private Long id;
    private Long userId;
    private String contactName;
    private String contactPhone;
    private String province;
    private String city;
    private String district;
    private String address;
    private String postalCode;
    private Integer isDefault;
    private Integer createdAt;
    private Integer updatedAt;
}
