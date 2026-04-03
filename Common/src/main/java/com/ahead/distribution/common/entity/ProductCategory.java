package com.ahead.distribution.common.entity;

import lombok.Data;

import java.util.List;

@Data
public class ProductCategory {
    private Long id;
    private Long parentId;
    private String name;
    private Integer sortOrder;
    private String icon;
    private Integer status;
    private Integer createdAt;
    private Integer updatedAt;

    private List<ProductCategory> children;
}
