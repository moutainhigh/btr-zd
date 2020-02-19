package com.baturu.zd.controller;

import com.alibaba.fastjson.JSONObject;
import com.baturu.logistics.api.requestParam.SocialTmsQueryRequest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.service.common.TransLineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * created by ketao by 2019/03/26
 **/
@RestController
@RequestMapping("zd/route")
@Slf4j
public class TransLineController {

    @Autowired
    private TransLineService transLineService;

    /**
     * 根据仓库四级地址查询物流面单信息
     * @param socialTmsQueryRequest
     * @return
     */
    @RequestMapping(value = "query",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO transLines(SocialTmsQueryRequest socialTmsQueryRequest) {
        return transLineService.queryTransLine(socialTmsQueryRequest);
    }


    public static void main(String[] args){
        WxAddressDTO wxAddressDTO = WxAddressDTO.builder()
                .id(7)
                .provinceId(1)
                .provinceName("广东")
                .cityId(110000)
                .cityName("汕头")
                .countyId(1)
                .countyName("潮南")
                .address("我的天12号")
                .company("我的天哈哈哈1")
                .isDefault(false)
                .phone("18812345677")
                .name("哇的吧")
                .build();
        System.out.println(JSONObject.toJSONString(wxAddressDTO));
    }
}
