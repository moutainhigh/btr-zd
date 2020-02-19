package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.RoleDTO;
import com.baturu.zd.request.business.RoleQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

/**
 * created by ketao by 2019/03/22
 **/
public interface RoleService {

    /**
     * 分页查找角色信息
     * @param request
     * @return
     */
    ResultDTO<PageDTO> queryRolesInPage(RoleQueryRequest request);

    /**
     * 根据角色名获取角色
     * @param name
     * @return
     */
    ResultDTO<RoleDTO> queryRoleByName(String name);

    /**
     * 角色保存
     * @param roleDTO
     * @return
     */
    ResultDTO<RoleDTO> saveRole(RoleDTO roleDTO);

    /**
     * 根据角色id获取角色及其权限id集合
     * @param id
     * @return
     */
    ResultDTO<RoleDTO> queryRoleById(Integer id);
}
