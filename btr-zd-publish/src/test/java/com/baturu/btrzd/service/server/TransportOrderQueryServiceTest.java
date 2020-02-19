package com.baturu.btrzd.service.server;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.enums.PackageStateEnum;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.service.server.TransportOrderQueryService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caizhuliang on 2019/4/22.
 */
@Slf4j
public class TransportOrderQueryServiceTest extends BaseTest {

    @Reference
    private TransportOrderQueryService transportOrderQueryService;

    @Test
    public void testQueryTransportOrderByPartnerId() {
        ResultDTO<List<String>> resultDTO = transportOrderQueryService.queryTransportOrderByPartnerId("74", 30);
        log.info("testQueryTransportOrderByPartnerId resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

    @Test
    public void testQueryTransportOrdersByTransportOrderNo() {
        ResultDTO<TransportOrderDTO> resultDTO = transportOrderQueryService.queryTransportOrdersByTransportOrderNo("W90425000050");
        log.info("testQueryTransportOrdersByTransportOrderNo resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

    @Test
    public void testQueryTransportOrdersInPage() {
        List<String> list = Lists.newArrayList("W90410000006", "W90425000004", "W90425000042");
        TransportOrderQueryRequest transportOrderQueryRequest = TransportOrderQueryRequest.builder().transportOrderNos(list).days(7).build();
//                .states(getZdState(1)).warehouseId(0).build();
        ResultDTO<PageDTO> resultDTO = transportOrderQueryService.queryTransportOrdersInPage(transportOrderQueryRequest);
        log.info("testQueryTransportOrdersInPage resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

    private List<Integer> getZdState(Integer packState) {
        List<Integer> states = new ArrayList<>(4);
        // 1:待验收，2:待配送，3:已配送
        if (packState.equals(1)) {
            states.add(PackageStateEnum.DELIVERED.getType());
            states.add(PackageStateEnum.SHIPMENT.getType());
        } else if (packState.equals(2)) {
            states.add(PackageStateEnum.CHECKED.getType());
        } else if (packState.equals(3)) {
            states.add(PackageStateEnum.EXPRESSED.getType());
        }
        return states;
    }
}
