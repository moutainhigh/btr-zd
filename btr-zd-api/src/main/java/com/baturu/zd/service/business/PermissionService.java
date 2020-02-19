package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.app.AppMenuPermissionDTO;
import com.baturu.zd.dto.common.PermissionDTO;
import com.baturu.zd.request.business.PermissionQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.List;

/**
 * 对zd_permission操作的API
 * @author CaiZhuliang
 * @since 2019-3-21
 */
public interface PermissionService {

    /**
     * 根据userId获取对应的权限数据
     * @param userId
     * @return model里面是json格式的
     */
    ResultDTO<List<AppMenuPermissionDTO>> queryPermissionByUserId(Integer userId);

    /**
     * 获取层级结构的权限数据
     * @return
     */
    ResultDTO<List<AppMenuPermissionDTO>>  queryPermissionTree();

    /**
     * 分页查询权限数据
     * @param request
     * @return
     */
    ResultDTO<PageDTO> queryPermissionsInPage(PermissionQueryRequest request);

    /**
     * 权限保存
     * @param permissionDTO
     * @return
     */
    ResultDTO<PermissionDTO> savePermission(PermissionDTO permissionDTO);
}
