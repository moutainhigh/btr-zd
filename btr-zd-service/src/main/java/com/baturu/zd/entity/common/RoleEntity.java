package com.baturu.zd.entity.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baturu.zd.entity.AbstractBaseEntity;
import lombok.*;

/**
 * created by ketao by 2019/03/22
 **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("zd_role")
public class RoleEntity extends AbstractBaseEntity {


    @TableField("name")
    private String name;

    /**
     * 角色编码
     */
    @TableField("code")
    private String code;

    /**0:禁用；1：启用*/
    @TableField("valid")
    private Integer valid;
}
