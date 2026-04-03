package com.ahead.distribution.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardStats {
    private long totalUsers;
    private long totalOrders;
    private long totalProducts;
    private long totalDistributors;
    private BigDecimal todaySales;
    private long pendingOrders;
}
