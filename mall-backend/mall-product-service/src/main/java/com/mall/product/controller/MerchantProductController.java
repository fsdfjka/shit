package com.mall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.MallConstants;
import com.mall.common.Result;
import com.mall.product.dto.ProductForm;
import com.mall.product.service.ProductService;
import com.mall.product.vo.ProductDetailVO;
import com.mall.product.vo.ProductVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 商家商品管理（网关已验证 type=2；商家身份取 X-User-Id 头，由网关转发） */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class MerchantProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public Result<Page<ProductVO>> list(
            @RequestHeader(MallConstants.HEADER_USER_ID) Long merchantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(productService.merchantPage(merchantId, page, size));
    }

    @GetMapping("/{id}")
    public Result<ProductDetailVO> detail(@PathVariable Long id) {
        return Result.ok(productService.detail(id));
    }

    @PostMapping
    public Result<Long> create(@RequestHeader(MallConstants.HEADER_USER_ID) Long merchantId,
                               @Valid @RequestBody ProductForm form) {
        return Result.ok(productService.create(merchantId, form));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@RequestHeader(MallConstants.HEADER_USER_ID) Long merchantId,
                               @PathVariable Long id,
                               @Valid @RequestBody ProductForm form) {
        productService.update(merchantId, id, form);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@RequestHeader(MallConstants.HEADER_USER_ID) Long merchantId,
                                     @PathVariable Long id,
                                     @RequestParam Integer status) {
        productService.updateStatus(merchantId, id, status);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader(MallConstants.HEADER_USER_ID) Long merchantId,
                               @PathVariable Long id) {
        productService.delete(merchantId, id);
        return Result.ok();
    }
}
