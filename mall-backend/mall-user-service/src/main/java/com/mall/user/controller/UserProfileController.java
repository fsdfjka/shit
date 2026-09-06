package com.mall.user.controller;

import com.mall.common.MallConstants;
import com.mall.common.Result;
import com.mall.user.entity.User;
import com.mall.user.entity.UserAddress;
import com.mall.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 用户个人中心（网关已验证 type=0）：资料 + 收货地址 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    public Result<User> profile(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId) {
        return Result.ok(userProfileService.profile(userId));
    }

    @PutMapping("/profile")
    public Result<Void> update(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId,
                               @RequestBody User patch) {
        userProfileService.updateProfile(userId, patch);
        return Result.ok();
    }

    @GetMapping("/addresses")
    public Result<List<UserAddress>> addresses(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId) {
        return Result.ok(userProfileService.addresses(userId));
    }

    @PostMapping("/addresses")
    public Result<Void> addAddress(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId,
                                   @RequestBody UserAddress addr) {
        userProfileService.saveAddress(userId, addr);
        return Result.ok();
    }

    @PutMapping("/addresses/{id}")
    public Result<Void> updateAddress(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId,
                                      @PathVariable Long id, @RequestBody UserAddress addr) {
        addr.setId(id);
        userProfileService.saveAddress(userId, addr);
        return Result.ok();
    }

    @DeleteMapping("/addresses/{id}")
    public Result<Void> deleteAddress(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId,
                                      @PathVariable Long id) {
        userProfileService.deleteAddress(userId, id);
        return Result.ok();
    }
}
