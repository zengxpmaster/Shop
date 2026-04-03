package com.ahead.distribution.admin.controller;

import com.ahead.distribution.admin.dto.PageResult;
import com.ahead.distribution.admin.service.ProductService;
import com.ahead.distribution.common.entity.Product;
import com.ahead.distribution.common.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public R<PageResult<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        return R.ok(productService.list(page, size, keyword, categoryId, status));
    }

    @GetMapping("/{id}")
    public R<Product> getById(@PathVariable Long id) {
        return R.ok(productService.findById(id));
    }

    @PostMapping
    public R<Void> create(@RequestBody Product product) {
        productService.create(product);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Product product) {
        productService.update(id, product);
        return R.ok();
    }

    @PatchMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        productService.updateStatus(id, body.get("status"));
        return R.ok();
    }
}
