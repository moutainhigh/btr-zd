package com.baturu.btrzd.service.wx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.wx.ReservationOrderDTO;
import com.baturu.zd.request.business.ReservationOrderQueryRequest;
import com.baturu.zd.service.business.ReservationOrderService;
import com.baturu.zd.service.dto.common.PageDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReservationOrderServiceTest extends BaseTest {

    @Autowired
    ReservationOrderService reservationOrderService;

    @Test
    public void m0(){
        ReservationOrderQueryRequest request = ReservationOrderQueryRequest.builder()
                .build();
//        request.setId(4);
        ResultDTO<PageDTO> resultDTO = reservationOrderService.queryReservationOrdersInPage(request);
        if(resultDTO.isSuccess()){
            System.out.println(JSON.toJSONString(resultDTO.getModel()));
        }
    }

    @Test
    public void m1(){
        String s = "{" +
                "\"active\": true," +
                "\"bulk\": 12.00," +
                "\"createTime\": 1552819281000," +
                "\"createUserId\": 4," +
                "\"deliveryType\": 1," +
                "\"goodName\": \"sss\"," +
                "\"id\": 4," +
                "\"nailBox\": false," +
                "\"payType\": 2," +
                "\"qty\": 12," +
                "\"remark\": \"我嘎达\"," +
                "\"reservationNo\": \"Y0681\"," +
                "\"state\": \"10\"," +
                "\"supportValue\": 10," +
                "\"updateTime\": 1552824502000," +
                "\"weight\": 45.00" +
                "}";
        ReservationOrderDTO pd = JSON.parseObject(s,new TypeReference<ReservationOrderDTO>(){});
        pd.setSenderAddrId(20);
        pd.setRecipientAddrId(23);

        ResultDTO resultDTO = reservationOrderService.saveReservationOrder(pd);
        if(resultDTO.isSuccess()){
            System.out.println(resultDTO.getModel());
        }
    }
}