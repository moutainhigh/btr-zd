package com.baturu.zd.controller.web;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.common.RoleDTO;
import com.baturu.zd.request.business.RoleQueryRequest;
import com.baturu.zd.request.server.RoleBaseQueryRequest;
import com.baturu.zd.service.business.RoleService;
import com.baturu.zd.service.server.RoleQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * created by ketao by 2019/03/22
 **/
@RestController
@RequestMapping("web/role")
@Slf4j
public class RoleController extends AbstractAppBaseController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleQueryService roleQueryService;


    @RequestMapping(value = "save",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO saveRole(@RequestBody RoleDTO roleDTO){
        AppUserLoginInfoDTO user = getCurrentUserInfo();
        if (roleDTO.getId() == null) {
            roleDTO.setCreateUserId(user.getUserId());
        } else {
            roleDTO.setUpdateUserId(user.getUserId());
        }
        return roleService.saveRole(roleDTO);
    }



    @RequestMapping(value = "queryPage",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryRolesInPage(RoleQueryRequest roleQueryRequest){
        return roleService.queryRolesInPage(roleQueryRequest);
    }


    @RequestMapping(value = "query",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryRoles(RoleBaseQueryRequest roleBaseQueryRequest){
        return roleQueryService.queryByParam(roleBaseQueryRequest);
    }




    @RequestMapping(value = "queryById",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryRoleById(Integer id){
        return roleService.queryRoleById(id);
    }



}
