package com.baturu.btrzd.service.common;

import com.alibaba.fastjson.JSON;
import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.TransportOrderConstant;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.dto.TransportOrderLogDTO;
import com.baturu.zd.dto.web.TransportOrderWebDTO;
import com.baturu.zd.enums.GatheringStatusEnum;
import com.baturu.zd.enums.TransportOrderStateEnum;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.service.business.TransportOrderService;
import com.baturu.zd.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author CaiZhuliang
 * @since 2019-3-25
 */
@Slf4j
public class TransportOrderServiceTest extends BaseTest {

    @Autowired
    private TransportOrderService transportOrderService;

    @Test
    public void testQueryTransportOrdersExcel() {
        TransportOrderQueryRequest request = TransportOrderQueryRequest.builder()
                .state(TransportOrderStateEnum.ORDERED.getType())
                .updateTimeStart(DateUtil.countdown(7))
                .updateTimeEnd(DateUtil.getTodayEndTime())
                .build();
        List<TransportOrderWebDTO> list = transportOrderService.queryTransportOrdersExcel(request);
        log.info("TransportOrderServiceTest testQueryTransportOrdersExcel ----------------------->>>> list = {}, size = {}", list, list.size());
        Assert.assertFalse(CollectionUtils.isEmpty(list));
    }

    @Test
    public void testQueryTransportOrdersForWebInPage() {
        TransportOrderQueryRequest request = TransportOrderQueryRequest.builder()
                .state(TransportOrderStateEnum.ORDERED.getType())
                .updateTimeStart(DateUtil.countdown(7))
                .updateTimeEnd(DateUtil.getTodayEndTime())
                .build();
        ResultDTO resultDTO = transportOrderService.queryTransportOrdersForWebInPage(request);
        log.info("TransportOrderServiceTest testQueryTransportOrdersForWebInPage ----------------------->>>> resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

    @Test
    public void testQueryTransportOrdersInPage() {
        TransportOrderQueryRequest queryRequest = TransportOrderQueryRequest.builder().days(1).build();
        ResultDTO resultDTO = transportOrderService.queryTransportOrdersInPage(queryRequest);
        log.info("TransportOrderServiceTest testQueryTransportOrdersInPage ----------------------->>>> resultDTO = {}", JSON.toJSON(resultDTO));
        Assert.assertTrue(resultDTO.isSuccess());
    }

    @Test
    public void testQueryTransportOrdersByTransportOrderNo() {
        ResultDTO resultDTO = transportOrderService.queryTransportOrdersByTransportOrderNo("W20190318");
        log.info("TransportOrderServiceTest testQueryTransportOrdersByTransportOrderNo ----------------------->>>> resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

    @Test
    public void testInsertTransportOrder() {
        TransportOrderDTO transportOrderDTO = TransportOrderDTO.builder().senderAddrId(59).recipientAddrId(60).qty(3).payType(0).servicePointId(32)
                .gatheringStatus(GatheringStatusEnum.UNPAY.getType())
                .payType(1)
                .collectAmount(new BigDecimal("0.00")).createUserId(1)
                .freight(new BigDecimal("100.00"))
                .nailBox(false)
                .totalPayment(new BigDecimal("100.00"))
                .nowPayment(new BigDecimal("100.00")).arrivePayment(new BigDecimal("0.00"))
                .bulk(new BigDecimal("30.00"))
                .build();
        log.info(JSON.toJSONString(transportOrderDTO));
        ResultDTO resultDTO = transportOrderService.insertTransportOrder(transportOrderDTO, null, TransportOrderConstant.APP);
        log.info("TransportOrderServiceTest testInsertTransportOrder ----------------------->>>> resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }


    @Test
    public void syncTransportOrderTest() throws Exception {
        TransportOrderLogDTO transportOrderLogDTO = TransportOrderLogDTO.builder().build();
        transportOrderLogDTO.setIdentification("W90418000012");
        transportOrderLogDTO.setWeight(new BigDecimal("200"));
        transportOrderLogDTO.setBulk(new BigDecimal("0.16"));
        transportOrderLogDTO.setFreight(new BigDecimal("16"));
        transportOrderLogDTO.setUpdateUserId(8);
//        ResultDTO resultDTO = transportOrderService.update(transportOrderLogDTO);
        TransportOrderDTO transportOrderDTO = TransportOrderDTO.builder().id(299).build();
        ResultDTO resultDTO = transportOrderService.abolishById(transportOrderDTO, 8);
        return;
    }

}
