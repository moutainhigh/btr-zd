package com.baturu.zd.controller.wx;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.wx.WxSignDTO;
import com.baturu.zd.service.business.WxSignService;
import com.baturu.zd.service.common.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

/**
 //"微信公众号公共api(不需验证用户登录信息)"
 * created by ketao by 2019/03/05
 **/
@RestController
@RequestMapping("wx")
@Slf4j
public class WeChatController {
    @Autowired
    private WxSignService wxSignService;

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * @param phone
     * @param type
     * @return
     */
    @RequestMapping(value = "checkCode",method = RequestMethod.GET)
    public ResultDTO getCheckCode(String phone,Integer type){
        return wxSignService.getCheckCode(phone,type);
    }

    @RequestMapping(value = "sign",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO sign(@RequestBody WxSignDTO wxSignDTO){
        return wxSignService.sign(wxSignDTO);
    }

    @RequestMapping(value = "wxLogin",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO wxLogin(@RequestBody WxSignDTO wxSignDTO){
        log.info("微信账号登录：{}",wxSignDTO);
        return wxSignService.wxLogin(wxSignDTO);
    }


    @RequestMapping(value = "wxInfo",method = RequestMethod.GET)
    public ResultDTO wxInfo(String code) throws UnsupportedEncodingException {
        return authenticationService.getWxUserInfo(code);
    }



    @RequestMapping(value = "deleteChildAccount", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO deleteChildAccount(Integer id) {
        return wxSignService.deleteChildAccount(id);
    }

}
