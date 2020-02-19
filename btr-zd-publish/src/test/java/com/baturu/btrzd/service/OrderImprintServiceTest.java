package com.baturu.btrzd.service;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.OrderImprintDTO;
import com.baturu.zd.request.server.OrderImprintBaseQueryRequest;
import com.baturu.zd.service.business.OrderImprintService;
import com.baturu.zd.service.server.OrderImprintQueryService;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class OrderImprintServiceTest extends BaseTest{

    @Autowired
    private OrderImprintService orderImprintService;

    @Autowired
    private OrderImprintQueryService orderImprintQueryService;

    @Test
    public void m0(){
        OrderImprintDTO orderImprintDTO = OrderImprintDTO.builder()
                .transportOrderNo("test")
                .operator("test")
                .reservationNo("test")
                .remark("test")
                .operator("pd")
                .build();
        ResultDTO<OrderImprintDTO> resultDTO = orderImprintService.saveOrUpdate(orderImprintDTO);
        System.out.println(resultDTO);
    }

    @Test
    public void m1(){
        ResultDTO<List<OrderImprintDTO>> resultDTO = orderImprintQueryService.queryByParam(OrderImprintBaseQueryRequest.builder()
                .active(Boolean.TRUE)
                .build());
        if(resultDTO.isSuccess()){
            resultDTO.getModel().stream().forEach(o->System.out.println(o));
        }
    }
}