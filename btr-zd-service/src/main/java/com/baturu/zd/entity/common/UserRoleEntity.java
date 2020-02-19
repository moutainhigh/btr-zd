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
@TableName("zd_user_role")
public class UserRoleEntity extends AbstractBaseEntity {

    /** 角色ID */
    @TableField("role_id")
    private Integer roleId;

    /** 角色ID */
    @TableField("user_id")
    private Integer userId;

}
