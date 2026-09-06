package com.mall.order.controller;

import com.mall.common.MallConstants;
import com.mall.common.Result;
import com.mall.order.dto.CartMergeRequest;
import com.mall.order.service.CartService;
import com.mall.order.vo.CartVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** 购物车（用户；网关已验证 type=0，X-User-Id 由网关转发） */
@RestController
@RequestMapping("/api/portal/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result<List<CartVO>> list(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId) {
        return Result.ok(cartService.list(userId));
    }

    @PostMapping
    public Result<Void> add(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId,
                            @RequestBody Map<String, Long> body) {
        cartService.add(userId, body.get("skuId"), body.get("count").intValue());
        return Result.ok();
    }

    /** 游客购物车合并（localStorage -> cart 表，同 SKU 累加） */
    @PostMapping("/merge")
    public Result<Void> merge(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId,
                              @Valid @RequestBody CartMergeRequest req) {
        cartService.merge(userId, req.getItems());
        return Result.ok();
    }

    @PutMapping("/{skuId}/count")
    public Result<Void> updateCount(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId,
                                    @PathVariable Long skuId,
                                    @RequestParam Integer count) {
        cartService.updateCount(userId, skuId, count);
        return Result.ok();
    }

    @PutMapping("/{skuId}/checked")
    public Result<Void> updateChecked(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId,
                                      @PathVariable Long skuId,
                                      @RequestParam Integer checked) {
        cartService.updateChecked(userId, skuId, checked);
        return Result.ok();
    }

    @DeleteMapping("/{skuId}")
    public Result<Void> remove(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId,
                               @PathVariable Long skuId) {
        cartService.remove(userId, skuId);
        return Result.ok();
    }
}
