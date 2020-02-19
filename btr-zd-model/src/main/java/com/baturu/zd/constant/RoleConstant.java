package com.baturu.zd.constant;

/**
 * created by laijinjie by 2019/03/21
 **/
public interface RoleConstant {


    //=========实体对应数据库字段start========//
    String ID = "id";
    /**
     * 角色名
     */
    String NAME = "name";
   /**
     * 角色编码
     */
    String CODE = "code";

    /**
     *是否有效
     */
    String ACTIVE="active";
    /**
     * 创建时间
     */
    String CREATE_TIME = "create_time";
    /**
     * 0:禁用；1：启用
     */
    String VALID = "valid";


    //=========实体对应数据库字段end========//

    //=======角色页面启用状态========//

    /**
     * 角色状态：1：启用，0：禁用，2：全部
     */
    Integer ROLE_BOTH=2;
    Integer ROLE_VALID=1;
    Integer ROLE_INVALID=0;
    //=======角色页面启用状态end========//

}
