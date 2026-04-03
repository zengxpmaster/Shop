package com.ahead.distribution.admin.controller;

import com.ahead.distribution.admin.service.CategoryService;
import com.ahead.distribution.common.entity.ProductCategory;
import com.ahead.distribution.common.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public R<List<ProductCategory>> list() {
        return R.ok(categoryService.treeList());
    }

    @PostMapping
    public R<Void> create(@RequestBody ProductCategory category) {
        categoryService.create(category);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody ProductCategory category) {
        categoryService.update(id, category);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return R.ok();
    }
}
