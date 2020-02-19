package com.baturu.zd.dto.app;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 对APP返回权限数据的类
 * @author CaiZhuliang
 * @since 2019-3-21
 */
@Data
@Builder
public class AppMenuPermissionDTO implements Serializable {

    /** 主键 */
    private Integer id;
    /** 权限编码（可带业务意义的唯一标识） */
    private String code;
    /** 所属上级 */
    private Integer pid;
    /** 菜单级别（1：一级；2：二级；3：三级） */
    private Integer level;
    /** 名称 */
    private String name;
    /** 类型 1:菜单 2:按钮 */
    private Integer type;
    /** 访问路径 */
    private String url;
    /** 数据权限，用于数据隔离 */
    private String dataPermission;
    /** 当前页面的按钮 */
    private List<AppButtonPermissionDTO> buttons;
    /** 当前页面的子页面 */
    private List<AppMenuPermissionDTO> menus;

}
