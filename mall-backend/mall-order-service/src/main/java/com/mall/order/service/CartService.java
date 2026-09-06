package com.mall.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.BizException;
import com.mall.order.entity.Cart;
import com.mall.order.entity.SkuOrder;
import com.mall.order.mapper.CartMapper;
import com.mall.order.mapper.CartViewMapper;
import com.mall.order.mapper.SkuOrderMapper;
import com.mall.order.vo.CartVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 线上购物车（登录后）。游客车在 localStorage，登录后前端合并调 merge（同 SKU 累加入库）。
 * cart 有 uk_user_sku，同一 SKU 仅一行——重复加购走累加。
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;
    private final CartViewMapper cartViewMapper;
    private final SkuOrderMapper skuOrderMapper;

    public List<CartVO> list(Long userId) {
        return cartViewMapper.selectCartByUser(userId);
    }

    @Transactional
    public void add(Long userId, Long skuId, Integer count) {
        SkuOrder sku = skuOrderMapper.selectActive(skuId);
        if (sku == null) {
            throw new BizException("商品已下架");
        }
        Cart exist = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId).eq(Cart::getSkuId, skuId));
        if (exist != null) {
            exist.setCount(exist.getCount() + count);
            cartMapper.updateById(exist);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setSkuId(skuId);
            cart.setProductId(sku.getProductId());
            cart.setCount(count);
            cart.setChecked(1);
            cartMapper.insert(cart);
        }
    }

    @Transactional
    public void merge(Long userId, List<com.mall.order.dto.CartMergeRequest.Item> items) {
        for (com.mall.order.dto.CartMergeRequest.Item it : items) {
            add(userId, it.getSkuId(), it.getCount());
        }
    }

    public void updateCount(Long userId, Long skuId, Integer count) {
        if (count == null || count <= 0) {
            throw new BizException("数量必须大于 0");
        }
        Cart cart = require(userId, skuId);
        cart.setCount(count);
        cartMapper.updateById(cart);
    }

    public void updateChecked(Long userId, Long skuId, Integer checked) {
        Cart cart = require(userId, skuId);
        cart.setChecked(checked);
        cartMapper.updateById(cart);
    }

    public void remove(Long userId, Long skuId) {
        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId).eq(Cart::getSkuId, skuId));
    }

    private Cart require(Long userId, Long skuId) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId).eq(Cart::getSkuId, skuId));
        if (cart == null) {
            throw new BizException("购物车不存在该商品");
        }
        return cart;
    }
}
