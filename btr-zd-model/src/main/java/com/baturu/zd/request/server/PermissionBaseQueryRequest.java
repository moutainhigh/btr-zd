package com.baturu.zd.request.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * created by ketao by 2019/03/22
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionBaseQueryRequest implements Serializable {

    private Integer id;
    private Set<Integer> ids;

    /**角色编码*/
    private String code;

    /**角色名称*/
    private String name;

    /**
     * 角色状态：1：有效，0：无效
     */
    private Boolean active;

    /**
     *权限类型(类型 1:菜单 2:按钮)
     */
    private Integer type;

    /**
     *菜单级别
     */
    private Integer level;
}
