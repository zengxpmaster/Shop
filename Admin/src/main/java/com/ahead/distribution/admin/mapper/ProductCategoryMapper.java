package com.ahead.distribution.admin.mapper;

import com.ahead.distribution.common.entity.ProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductCategoryMapper {
    List<ProductCategory> findAll();
    ProductCategory findById(@Param("id") Long id);
    int insert(ProductCategory category);
    int update(ProductCategory category);
    int delete(@Param("id") Long id);
}
