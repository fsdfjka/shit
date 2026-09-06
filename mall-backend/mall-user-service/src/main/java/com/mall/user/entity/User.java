package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 用户/平台管理员（物理保留；user 为保留字须反引号） */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("`user`")
public class User extends BaseDO {

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String phone;

    private String email;

    /** 0 用户 1 平台管理员 */
    private Integer role;

    /** 0 正常 1 禁用 */
    private Integer status;
}
