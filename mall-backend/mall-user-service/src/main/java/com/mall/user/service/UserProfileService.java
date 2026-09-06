package com.mall.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.BizException;
import com.mall.user.entity.User;
import com.mall.user.entity.UserAddress;
import com.mall.user.mapper.UserAddressMapper;
import com.mall.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 用户资料 + 收货地址（7.3） */
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserMapper userMapper;
    private final UserAddressMapper userAddressMapper;

    public User profile(Long userId) {
        User u = userMapper.selectById(userId);
        if (u == null) {
            throw new BizException(404, "用户不存在");
        }
        u.setPassword(null);
        return u;
    }

    public void updateProfile(Long userId, User patch) {
        User u = userMapper.selectById(userId);
        if (u == null) {
            throw new BizException(404, "用户不存在");
        }
        if (patch.getNickname() != null) u.setNickname(patch.getNickname());
        if (patch.getPhone() != null) u.setPhone(patch.getPhone());
        if (patch.getEmail() != null) u.setEmail(patch.getEmail());
        if (patch.getAvatar() != null) u.setAvatar(patch.getAvatar());
        userMapper.updateById(u);
    }

    public List<UserAddress> addresses(Long userId) {
        return userAddressMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId));
    }

    @Transactional
    public void saveAddress(Long userId, UserAddress addr) {
        addr.setUserId(userId);
        if (addr.getIsDefault() != null && addr.getIsDefault() == 1) {
            // 先清旧默认再设新默认（单事务，应用层保唯一默认）
            UserAddress old = new UserAddress();
            old.setIsDefault(0);
            userAddressMapper.update(old, new LambdaQueryWrapper<UserAddress>()
                    .eq(UserAddress::getUserId, userId).eq(UserAddress::getIsDefault, 1));
        }
        if (addr.getId() == null) {
            userAddressMapper.insert(addr);
        } else {
            // 归属校验
            UserAddress exist = userAddressMapper.selectById(addr.getId());
            if (exist == null || !exist.getUserId().equals(userId)) {
                throw new BizException(403, "地址不存在");
            }
            userAddressMapper.updateById(addr);
        }
    }

    /** 逻辑删除（BaseLogicDO @TableLogic） */
    public void deleteAddress(Long userId, Long id) {
        UserAddress exist = userAddressMapper.selectById(id);
        if (exist == null || !exist.getUserId().equals(userId)) {
            throw new BizException(403, "地址不存在");
        }
        userAddressMapper.deleteById(id);
    }
}
