package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * created by ketao by 2019/03/22
 **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleDTO extends AbstractBaseDTO {

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色对应权限id集合(页面展示)
     */
    private Set<Integer> permissionIds;

    /**
     * 修改人
     */
    private String updateUserName;

    /**1:禁用；0：启用*/
    private Integer valid;
}
