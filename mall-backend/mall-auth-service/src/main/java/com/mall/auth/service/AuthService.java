package com.mall.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.auth.dto.LoginRequest;
import com.mall.auth.dto.MerchantApplyRequest;
import com.mall.auth.dto.RegisterRequest;
import com.mall.auth.entity.Merchant;
import com.mall.auth.entity.User;
import com.mall.auth.mapper.MerchantMapper;
import com.mall.auth.mapper.UserMapper;
import com.mall.auth.vo.LoginResponse;
import com.mall.common.BizException;
import com.mall.common.JwtUtil;
import com.mall.common.MallConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 登录/注册。账号双表认证：普通用户/管理员查 user 表，商家查 merchant 表，
 * 登录成功后签发含 type 的 JWT（网关按 type+路径前缀鉴权）。
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final MerchantMapper merchantMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest req) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername()));
        if (user != null) {
            if (user.getStatus() != 0) {
                throw new BizException("账号已被禁用");
            }
            if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                throw new BizException("密码错误");
            }
            // 平台管理员（user.role=1）签发 type=1，普通用户 type=0
            Integer type = user.getRole() != null && user.getRole() == 1
                    ? MallConstants.TYPE_ADMIN : MallConstants.TYPE_USER;
            return new LoginResponse(
                    jwtUtil.create(user.getId(), type, user.getUsername()),
                    type, user.getId(), user.getUsername(), user.getNickname());
        }

        Merchant merchant = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getUsername, req.getUsername()));
        if (merchant != null) {
            if (merchant.getStatus() != 0) {
                throw new BizException("账号已被禁用");
            }
            if (merchant.getApplyStatus() == null || merchant.getApplyStatus() != 1) {
                throw new BizException("商家入驻审核中/未通过，暂不能登录");
            }
            if (!passwordEncoder.matches(req.getPassword(), merchant.getPassword())) {
                throw new BizException("密码错误");
            }
            return new LoginResponse(
                    jwtUtil.create(merchant.getId(), MallConstants.TYPE_MERCHANT, merchant.getUsername()),
                    MallConstants.TYPE_MERCHANT, merchant.getId(), merchant.getUsername(), merchant.getMerchantName());
        }
        throw new BizException("账号不存在");
    }

    /** 普通用户注册（role=0） */
    public void register(RegisterRequest req) {
        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername())) > 0) {
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getNickname());
        user.setPhone(req.getPhone());
        user.setRole(MallConstants.TYPE_USER);
        user.setStatus(0);
        userMapper.insert(user);
    }

    /** 商家入驻申请：创建 merchant，applyStatus=0 待审核（管理员审核后才能登录） */
    public void merchantApply(MerchantApplyRequest req) {
        if (merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getUsername, req.getUsername())) > 0) {
            throw new BizException("用户名已存在");
        }
        Merchant merchant = new Merchant();
        merchant.setUsername(req.getUsername());
        merchant.setPassword(passwordEncoder.encode(req.getPassword()));
        merchant.setMerchantName(req.getMerchantName());
        merchant.setPhone(req.getPhone());
        merchant.setContact(req.getContact());
        merchant.setShopName(req.getShopName());
        merchant.setApplyStatus(0);
        merchant.setShopStatus(0);
        merchant.setStatus(0);
        merchant.setBalance(java.math.BigDecimal.ZERO);
        merchantMapper.insert(merchant);
    }
}
