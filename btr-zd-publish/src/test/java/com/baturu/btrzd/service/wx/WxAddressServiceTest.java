package com.baturu.btrzd.service.wx;

import com.alibaba.fastjson.JSON;
import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.request.business.WxAddressQueryRequest;
import com.baturu.zd.request.server.WxAddressBaseQueryRequest;
import com.baturu.zd.service.business.WxAddressService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.service.server.WxAddressQueryService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class WxAddressServiceTest extends BaseTest {
    @Autowired
    private WxAddressService wxAddressService;

    @Autowired
    private WxAddressQueryService wxAddressQueryService;

    @Test
    public void selectById() {
        ResultDTO<WxAddressDTO> resultDTO = wxAddressQueryService.queryById(2);
        System.out.println(resultDTO.getModel());
    }

    @Test
    public void selectByParam1() {
        WxAddressQueryRequest request = WxAddressQueryRequest.builder()
                .type(2)
                .phone("7869")
                .build();
        request.setCreateUserIds(Sets.newHashSet(1,0));
        ResultDTO<PageDTO> resultDTO = wxAddressService.selectWxAddressDTOForPage(request);
        PageDTO pageDTO = resultDTO.getModel();
        pageDTO.getRecords().stream().forEach(o->System.out.println(o));

    }

    @Test
    public void save() {
        WxAddressDTO wxAddressDTO = WxAddressDTO.builder()
                .name("最新默认地址")
                .phone("phone")
                .provinceId(1)
                .cityId(1)
                .countyId(1)
                .address("address")
                .company("公司")
                .isDefault(true)
                .createUserId(0)
                .updateUserId(0)
                .type(2)
                .build();
//        ResultDTO resultDTO = wxAddressQueryService.insert(wxAddressDTO);
//        System.out.println(resultDTO);
    }

    @Test
    public void updateById() {
        WxAddressDTO wxAddressDTO = WxAddressDTO.builder()
                .id(2)
                .isDefault(false)
                .build();
        ResultDTO resultDTO = wxAddressService.saveOrUpdate(wxAddressDTO);
        System.out.println(resultDTO);
    }

    @Test
    public void m5() {
        WxAddressBaseQueryRequest request = WxAddressBaseQueryRequest.builder()
                .ids(Sets.newHashSet(25))
                .build();
        ResultDTO<List<WxAddressDTO>> resultDTO = wxAddressQueryService.queryByParam(request);
        if(resultDTO.isSuccess()){
            resultDTO.getModel().stream().forEach(o -> {
                System.out.println(JSON.toJSONString(o));
            });
        }
    }

}