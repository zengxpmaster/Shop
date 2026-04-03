package com.ahead.distribution.admin.service;

import com.ahead.distribution.admin.dto.PageResult;
import com.ahead.distribution.common.entity.Product;

public interface ProductService {
    Product findById(Long id);
    PageResult<Product> list(int page, int size, String keyword, Long categoryId, Integer status);
    void create(Product product);
    void update(Long id, Product product);
    void updateStatus(Long id, Integer status);
}
