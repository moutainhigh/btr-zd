package com.baturu.zd.request.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * created by ketao by 2019/03/25
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleBaseQueryRequest implements Serializable {


    private Integer id;

    private Set<Integer> ids;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编号
     */
    private String code;

    /**
     * 0:禁用；1：启用
     */
    private Integer valid;

    /**
     * 角色有效性
     */
    private  Boolean active;
}
