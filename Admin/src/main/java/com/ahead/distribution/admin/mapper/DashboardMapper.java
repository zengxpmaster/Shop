package com.ahead.distribution.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface DashboardMapper {

    @Select("SELECT COUNT(*) FROM users")
    long countUsers();

    @Select("SELECT COUNT(*) FROM orders")
    long countOrders();

    @Select("SELECT COUNT(*) FROM products")
    long countProducts();

    @Select("SELECT COUNT(*) FROM distributors")
    long countDistributors();

    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM orders WHERE pay_status = 1 AND FROM_UNIXTIME(paid_at, '%Y-%m-%d') = CURDATE()")
    BigDecimal todaySales();

    @Select("SELECT COUNT(*) FROM orders WHERE status = 1")
    long countPendingOrders();
}
