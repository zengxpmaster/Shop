package com.ahead.distribution.admin.service;

import com.ahead.distribution.common.entity.ProductCategory;

import java.util.List;

public interface CategoryService {
    List<ProductCategory> treeList();
    void create(ProductCategory category);
    void update(Long id, ProductCategory category);
    void delete(Long id);
}
