package com.mall.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.product.entity.Advert;
import com.mall.product.mapper.AdvertMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertService {

    private final AdvertMapper advertMapper;

    /** 前台：启用中的广告位 */
    public List<Advert> listActive() {
        return advertMapper.selectList(new LambdaQueryWrapper<Advert>()
                .eq(Advert::getStatus, 0)
                .orderByAsc(Advert::getSort));
    }

    /** 后台：全部广告位 */
    public List<Advert> listAll() {
        return advertMapper.selectList(new LambdaQueryWrapper<Advert>()
                .orderByAsc(Advert::getSort));
    }

    public void create(Advert advert) {
        advert.setStatus(0);
        advertMapper.insert(advert);
    }

    public void update(Advert advert) {
        advertMapper.updateById(advert);
    }

    /** 逻辑删除（BaseLogicDO @TableLogic） */
    public void delete(Long id) {
        advertMapper.deleteById(id);
    }
}
