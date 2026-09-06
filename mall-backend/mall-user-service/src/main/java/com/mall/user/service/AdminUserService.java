package com.mall.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.BizException;
import com.mall.common.MallConstants;
import com.mall.user.entity.User;
import com.mall.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/** 平台员工管理（user.role=1；物理保留，禁用即 status=1） */
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public Page<User> page(int page, int size) {
        Page<User> p = userMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<User>()
                        .eq(User::getRole, MallConstants.TYPE_ADMIN)
                        .orderByDesc(User::getCreateTime));
        p.getRecords().forEach(u -> u.setPassword(null));
        return p;
    }

    public void create(User employee) {
        if (employee.getUsername() == null || employee.getUsername().isBlank()
                || employee.getPassword() == null) {
            throw new BizException("用户名/密码不能为空");
        }
        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, employee.getUsername())) > 0) {
            throw new BizException("用户名已存在");
        }
        User u = new User();
        u.setUsername(employee.getUsername());
        u.setPassword(passwordEncoder.encode(employee.getPassword()));
        u.setNickname(employee.getNickname());
        u.setPhone(employee.getPhone());
        u.setRole(MallConstants.TYPE_ADMIN);
        u.setStatus(0);
        userMapper.insert(u);
    }

    public void updateStatus(Long id, Integer status) {
        User u = userMapper.selectById(id);
        if (u == null || u.getRole() != 1) {
            throw new BizException("员工不存在");
        }
        u.setStatus(status);
        userMapper.updateById(u);
    }
}
