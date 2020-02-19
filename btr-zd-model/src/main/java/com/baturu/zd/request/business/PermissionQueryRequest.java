package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/03/22
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionQueryRequest extends  BaseRequest{

    /**角色编码*/
    private String code;

    /**角色名称*/
    private String name;

    /**
     * 角色状态：1：启用，0：禁用，2：全部
     */
    private Integer status;

    /**
     *权限类型(类型 1:菜单 2:按钮)
     * @see com.baturu.zd.enums.PermissionTypeEnum
     */
    private Integer type;
}
