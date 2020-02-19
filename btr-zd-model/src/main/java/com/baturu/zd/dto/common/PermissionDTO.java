package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by caizhuliang on 2019/3/21.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionDTO extends AbstractBaseDTO implements Serializable {

    private Integer id;

    /** 权限编码（可带业务意义的唯一标识） */
    private String code;

    /** 所属上级 */
    private Integer pid;

    /** 名称 */
    private String name;

    /** 类型 1:菜单 2:按钮
     * @see com.baturu.zd.enums.PermissionTypeEnum
     * */
    private Integer type;

    /** 菜单级别（1：一级；2：二级；3：三级） */
    private Integer level;

    /** 权限值（如果用shiro，这里就可以存放与后台对应的权限注解值，预留字段） */
    private String permissionValue;

    /** 数据权限，用于数据隔离 */
    private String dataPermission;

    /** 访问路径 */
    private String url;


    /**备注*/
    private String remark;


    /**
     * 修改人
     */
    private String updateUserName;

    /**
     * 按钮事件
     */
    private String event;
}
