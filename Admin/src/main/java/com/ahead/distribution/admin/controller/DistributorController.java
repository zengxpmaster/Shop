package com.ahead.distribution.admin.controller;

import com.ahead.distribution.admin.dto.PageResult;
import com.ahead.distribution.admin.service.DistributorService;
import com.ahead.distribution.common.entity.Distributor;
import com.ahead.distribution.common.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/distributors")
@CrossOrigin
public class DistributorController {

    @Autowired
    private DistributorService distributorService;

    @GetMapping
    public R<PageResult<Distributor>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return R.ok(distributorService.list(page, size, keyword, status));
    }

    @GetMapping("/{id}")
    public R<Distributor> getById(@PathVariable Long id) {
        return R.ok(distributorService.findById(id));
    }

    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        distributorService.updateStatus(id, body.get("status"));
        return R.ok();
    }
}
