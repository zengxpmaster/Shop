package com.ahead.distribution.admin.controller;

import com.ahead.distribution.admin.dto.DashboardStats;
import com.ahead.distribution.admin.mapper.DashboardMapper;
import com.ahead.distribution.common.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class HomeController {

    @Autowired
    private DashboardMapper dashboardMapper;

    @GetMapping("/stats")
    public R<DashboardStats> stats() {
        DashboardStats stats = DashboardStats.builder()
                .totalUsers(dashboardMapper.countUsers())
                .totalOrders(dashboardMapper.countOrders())
                .totalProducts(dashboardMapper.countProducts())
                .totalDistributors(dashboardMapper.countDistributors())
                .todaySales(dashboardMapper.todaySales())
                .pendingOrders(dashboardMapper.countPendingOrders())
                .build();
        return R.ok(stats);
    }
}

