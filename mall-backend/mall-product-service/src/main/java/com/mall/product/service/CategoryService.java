package com.mall.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.BizException;
import com.mall.product.entity.Category;
import com.mall.product.entity.Product;
import com.mall.product.mapper.CategoryMapper;
import com.mall.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    /** 类目列表（前台/后台共用，全量按 sort 排序，两级由 parentId 表达） */
    public List<Category> list() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSort));
    }

    public void create(Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        category.setStatus(0);
        categoryMapper.insert(category);
    }

    public void update(Category category) {
        categoryMapper.updateById(category);
    }

    /** 删除校验：无子类目且无在售商品（防悬挂引用，见数据库文档注） */
    public void delete(Long id) {
        long children = categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, id));
        if (children > 0) {
            throw new BizException("存在子类目，不能删除");
        }
        long products = productMapper.selectCount(new LambdaQueryWrapper<Product>()
                .eq(Product::getCategoryId, id));
        if (products > 0) {
            throw new BizException("类目下存在商品，不能删除");
        }
        categoryMapper.deleteById(id);
    }
}
