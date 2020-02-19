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
@TableName("zd_permission")
public class PermissionEntity extends AbstractBaseEntity {

    /** 权限编码（可带业务意义的唯一标识） */
    @TableField("code")
    private String code;

    /** 所属上级 */
    @TableField("pid")
    private Integer pid;

    /** 名称 */
    @TableField("name")
    private String name;

    /** 类型 1:菜单 2:按钮 */
    @TableField("type")
    private Integer type;

    /** 权限值（如果用shiro，这里就可以存放与后台对应的权限注解值，预留字段） */
    @TableField("permission_value")
    private String permissionValue;

    /** 数据权限，用于数据隔离 */
    @TableField("data_permission")
    private String dataPermission;

    /** 访问路径 */
    @TableField("url")
    private String url;

    /** 菜单级别（1：一级；2：二级） */
    @TableField("level")
    private Integer level;

    /**备注*/
    @TableField("remark")
    private String remark;

    /**按钮事件**/
    @TableField("event")
    private String event;
}
