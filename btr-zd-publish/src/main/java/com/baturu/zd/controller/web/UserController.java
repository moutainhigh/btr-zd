package com.baturu.zd.controller.web;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.common.UserDTO;
import com.baturu.zd.request.business.UserQueryRequest;
import com.baturu.zd.service.business.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * created by ketao by 2019/03/22
 **/
@RestController
@RequestMapping("web/user")
@Slf4j
public class UserController extends AbstractAppBaseController {

    @Autowired
    private UserService userService;


    @RequestMapping(value = "save",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO saveUser(@RequestBody UserDTO userDTO) {
        AppUserLoginInfoDTO user = getCurrentUserInfo();
        if (userDTO.getId() == null) {
            userDTO.setCreateUserId(user.getUserId());
        } else {
            userDTO.setUpdateUserId(user.getUserId());
        }
        return userService.saveUser(userDTO);
    }



    @RequestMapping(value = "query",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryUsersInPage(UserQueryRequest userQueryRequest) {
        return userService.queryUsersInPage(userQueryRequest);
    }


    @RequestMapping(value = "queryById", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryUserById(Integer id) {
        return userService.queryUserById(id);
    }


}
