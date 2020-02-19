package com.baturu.zd.constant;

/**
 * created by laijinjie by 2019/03/21
 **/
public interface PermissionConstant {


    //=========实体对应数据库字段start========//
    String ID = "id";
    /**
     * 权限名
     */
    String NAME = "name";
    /**
     * 权限名
     */
    String TYPE = "type";
    /**
     * 权限编码
     */
    String CODE = "code";
    /**
     *是否有效
     */
    String ACTIVE="active";
    /**
    /**
     *菜单级别（1：一级；2：二级；3：三级）
     */
    String LEVEL="level";
    /**
     * 创建时间
     */
    String CREATE_TIME = "create_time";
    //=========实体对应数据库字段end========//

    //=========权限类型==========//
    /**菜单*/
    Integer PERMISSION_PAGE=1;
    /**按钮*/
    Integer PERMISSION_BUTTON=2;
    //=========权限类型end==========//

}
