package com.mall.product.controller;

import com.mall.common.Result;
import com.mall.product.entity.Advert;
import com.mall.product.entity.Category;
import com.mall.product.service.AdvertService;
import com.mall.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 平台运营：类目管理、广告管理（网关已验证 type=1） */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCatalogController {

    private final CategoryService categoryService;
    private final AdvertService advertService;

    // --- 类目管理 ---

    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.ok(categoryService.list());
    }

    @PostMapping("/categories")
    public Result<Void> createCategory(@RequestBody Category category) {
        categoryService.create(category);
        return Result.ok();
    }

    @PutMapping("/categories/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.update(category);
        return Result.ok();
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok();
    }

    // --- 广告管理 ---

    @GetMapping("/adverts")
    public Result<List<Advert>> adverts() {
        return Result.ok(advertService.listAll());
    }

    @PostMapping("/adverts")
    public Result<Void> createAdvert(@RequestBody Advert advert) {
        advertService.create(advert);
        return Result.ok();
    }

    @PutMapping("/adverts/{id}")
    public Result<Void> updateAdvert(@PathVariable Long id, @RequestBody Advert advert) {
        advert.setId(id);
        advertService.update(advert);
        return Result.ok();
    }

    @DeleteMapping("/adverts/{id}")
    public Result<Void> deleteAdvert(@PathVariable Long id) {
        advertService.delete(id);
        return Result.ok();
    }
}
