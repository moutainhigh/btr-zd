package com.baturu.btrzd.rpc;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.btrzd.service.BaseTest;
import com.baturu.kit.dto.ResultDTO;
import com.baturu.message.constant.SmsType;
import com.baturu.message.request.SmsMessageRequest;
import com.baturu.message.service.MessageService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * 短信服务对接，单元测试类
 * @author CaiZhuliang
 * @date 2019-08-13
 */
@Slf4j
public class MessageServiceTest extends BaseTest {

    @Reference(check = false)
    private MessageService messageService;

    @Test
    public void testSendSmsBusiness() {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("verify", "CaiZhuliang test!");
        SmsMessageRequest request = SmsMessageRequest.builder()
                .templateName("verify")
                .parameters(parameters)
                .build();
        request.setAppKey("btr-zd-publish");
        request.setSmsType(SmsType.SMS_BUSINESS.getValue());
        request.setReceivers(Lists.newArrayList("18998365718"));
        sendMsg(request);
    }

    @Test
    public void testSendSmsMarketing() {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("content", "CaiZhuliang test!");
        SmsMessageRequest request = SmsMessageRequest.builder()
                .templateName("otherBussinessTemplate")
                .parameters(parameters)
                .build();
        request.setAppKey("btr-zd-publish");
        request.setSmsType(SmsType.SMS_MARKETING.getValue());
        request.setReceivers(Lists.newArrayList("18998365718"));
        sendMsg(request);
    }

    private void sendMsg(SmsMessageRequest request) {
        try {
            ResultDTO<Void> resultDTO = messageService.sendSms(request);
            log.info("sendMsg - resultDTO = {}", resultDTO);
            Assert.assertTrue(resultDTO.isSuccess());
        } catch (Exception e) {
            log.error("sendMsg - ERROR!", e);
            Assert.assertTrue(Boolean.FALSE);
        }
    }

}
