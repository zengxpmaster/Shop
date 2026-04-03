package com.ahead.distribution.admin.service.impl;

import com.ahead.distribution.admin.mapper.ProductCategoryMapper;
import com.ahead.distribution.admin.service.CategoryService;
import com.ahead.distribution.common.entity.ProductCategory;
import com.ahead.distribution.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private ProductCategoryMapper categoryMapper;

    @Override
    public List<ProductCategory> treeList() {
        List<ProductCategory> all = categoryMapper.findAll();
        return buildTree(all, 0L);
    }

    private List<ProductCategory> buildTree(List<ProductCategory> all, Long parentId) {
        return all.stream()
                .filter(c -> parentId.equals(c.getParentId()))
                .peek(c -> c.setChildren(buildTree(all, c.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public void create(ProductCategory category) {
        int now = (int) (System.currentTimeMillis() / 1000);
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        categoryMapper.insert(category);
    }

    @Override
    public void update(Long id, ProductCategory category) {
        ProductCategory existing = categoryMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(404, "Category not found");
        }
        category.setId(id);
        category.setUpdatedAt((int) (System.currentTimeMillis() / 1000));
        categoryMapper.update(category);
    }

    @Override
    public void delete(Long id) {
        ProductCategory existing = categoryMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(404, "Category not found");
        }
        categoryMapper.delete(id);
    }
}
