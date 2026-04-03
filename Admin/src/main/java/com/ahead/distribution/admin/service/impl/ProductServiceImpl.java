package com.ahead.distribution.admin.service.impl;

import com.ahead.distribution.admin.dto.PageResult;
import com.ahead.distribution.admin.mapper.ProductMapper;
import com.ahead.distribution.admin.service.ProductService;
import com.ahead.distribution.common.entity.Product;
import com.ahead.distribution.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Product findById(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException(404, "Product not found");
        }
        return product;
    }

    @Override
    public PageResult<Product> list(int page, int size, String keyword, Long categoryId, Integer status) {
        int offset = (page - 1) * size;
        List<Product> products = productMapper.list(offset, size, keyword, categoryId, status);
        int total = productMapper.count(keyword, categoryId, status);
        return new PageResult<>(products, total, page, size);
    }

    @Override
    public void create(Product product) {
        int now = (int) (System.currentTimeMillis() / 1000);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getSortOrder() == null) {
            product.setSortOrder(0);
        }
        productMapper.insert(product);
    }

    @Override
    public void update(Long id, Product product) {
        Product existing = productMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(404, "Product not found");
        }
        product.setId(id);
        product.setUpdatedAt((int) (System.currentTimeMillis() / 1000));
        productMapper.update(product);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product existing = productMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(404, "Product not found");
        }
        productMapper.updateStatus(id, status);
    }
}
