package com.ahead.distribution.admin.mapper;

import com.ahead.distribution.common.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    Product findById(@Param("id") Long id);
    List<Product> list(@Param("page") int page, @Param("size") int size,
                       @Param("keyword") String keyword, @Param("categoryId") Long categoryId,
                       @Param("status") Integer status);
    int count(@Param("keyword") String keyword, @Param("categoryId") Long categoryId,
              @Param("status") Integer status);
    int insert(Product product);
    int update(Product product);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);
}
