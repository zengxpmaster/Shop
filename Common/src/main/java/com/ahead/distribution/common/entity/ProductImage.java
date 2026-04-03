package com.ahead.distribution.common.entity;

import lombok.Data;

@Data
public class ProductImage {
    private Long id;
    private Long productId;
    private String imagePath;
    private Integer sortOrder;
    private Integer createdAt;
}
