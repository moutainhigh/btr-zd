package com.baturu.zd.entity.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baturu.zd.entity.AbstractBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CaiZhuliang
 * @since 2019-3-21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("zd_user")
public class UserEntity extends AbstractBaseEntity {

    /** 帐号 */
    @TableField("username")
    private String username;

    /** 密码 */
    @TableField("password")
    private String password;

    /** 用户姓名 */
    @TableField("name")
    private String name;

    /** 联系电话 */
    @TableField("phone")
    private String phone;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 状态 0:正常 1:锁定 */
    @TableField("locked")
    private Integer locked;

    /** 是否admin；0：否，1：是 */
    @TableField("root")
    private Integer root;

    /** 服务网点id */
    @TableField("service_point_id")
    private Integer servicePointId;



}
