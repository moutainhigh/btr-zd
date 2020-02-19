package com.baturu.zd.controller.web;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.common.PermissionDTO;
import com.baturu.zd.dto.common.UserDTO;
import com.baturu.zd.request.business.PermissionQueryRequest;
import com.baturu.zd.request.server.PermissionBaseQueryRequest;
import com.baturu.zd.service.business.PermissionService;
import com.baturu.zd.service.common.AuthenticationService;
import com.baturu.zd.service.server.PermissionQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * created by ketao by 2019/03/22
 **/
@RestController
@RequestMapping("web/permission")
@Slf4j
public class PermissionController extends AbstractAppBaseController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionQueryService permissionQueryService;

    @Autowired
    private AuthenticationService authenticationService;


    @RequestMapping(value = "save",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO savePermission(@RequestBody PermissionDTO permissionDTO){
        AppUserLoginInfoDTO userLoginInfoDTO = getCurrentUserInfo();
        if (permissionDTO.getId() == null) {
            permissionDTO.setCreateUserId(userLoginInfoDTO.getUserId());
        } else {
            permissionDTO.setUpdateUserId(userLoginInfoDTO.getUserId());
        }

        return permissionService.savePermission(permissionDTO);
    }



    @RequestMapping(value = "queryPage",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryPermissionsInPage(PermissionQueryRequest permissionQueryRequest){
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        log.info("权限分页耗时:"+(end-start));
        return permissionService.queryPermissionsInPage(permissionQueryRequest);
    }


    @RequestMapping(value = "query",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryPermissions(PermissionBaseQueryRequest permissionBaseQueryRequest){
        return permissionQueryService.queryByParam(permissionBaseQueryRequest);
    }


    @RequestMapping(value = "userPermissions",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryUserPermission() throws Exception {
        UserDTO userDTO = authenticationService.getCurrentUser();
        return permissionService.queryPermissionByUserId(userDTO.getId());
    }

    @RequestMapping(value = "queryPermissionTree",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryPermissionTree() throws Exception {
        return permissionService.queryPermissionTree();
    }




    @RequestMapping(value = "queryById",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryPermissionById(Integer id){
        return permissionQueryService.queryById(id);
    }



}
