package com.baturu.zd.controller.app;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.service.business.SendMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 逐道收单APP发送消息控制器
 * @author CaiZhuliang
 * @since 2019-4-3
 */
@RestController
@Slf4j
@RequestMapping("app/sendMsg")
public class AppSendMsgController extends AbstractAppBaseController {

    @Autowired
    private SendMsgService sendMsgService;

    @RequestMapping(value = "/pay/{payOrderNo}",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO pay(@PathVariable String payOrderNo){
        log.info("************************************逐道收单APP发送消息控制器发送模拟支付消息************************************");
        ResultDTO<String> resultDTO = sendMsgService.sendMsg(payOrderNo);
        log.info("AppSendMsgController##pay : resultDTO = {}", resultDTO);
        return resultDTO;
    }

}
