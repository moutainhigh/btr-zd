package com.baturu.btrzd.service.server;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.EditLogController;
import com.baturu.zd.controller.app.TransportOrderController;
import com.baturu.zd.dto.DispatchReportDTO;
import com.baturu.zd.dto.TransportOrderLogDTO;
import com.baturu.zd.enums.TransportOrderStateEnum;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.service.business.EditLogService;
import com.baturu.zd.service.business.ReportService;
import com.baturu.zd.service.business.TransportOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * Created by liuduanyang on 2019/5/8
 */
@Slf4j
public class EditLogServiceTest extends BaseTest {

    @Autowired
    private EditLogService editLogService;

    @Autowired
    private TransportOrderService transportOrderService;

    @Test
    public void testGetEditLogs() throws Exception {
        String transportOrderNo = "W90401000001";
        String type = "transport_order";

        ResultDTO editLogs = editLogService.getEditLogs(transportOrderNo, type, 1, 10);
        log.info("日志信息:{}", editLogs);
    }

    @Test
    public void testCreate() throws Exception {
        TransportOrderLogDTO transportOrderLogDTO = TransportOrderLogDTO.builder()
                                                        .bulk(new BigDecimal(2.0))
                                                        .deliveryType(1)
                                                        .nowPayment(new BigDecimal(3.0))
                                                        .payType(1)
                                                        .recipientAddress("广州")
                                                        .recipientName("zcc")
                                                        .recipientPhone("1341002")
                                                        .senderAddress("中山")
                                                        .senderPhone("13415896")
                                                        .senderName("LDy")
                                                        .weight(new BigDecimal(1.0))
                                                        .build();
        transportOrderLogDTO.setIdentification("W90401000002");
        transportOrderLogDTO.setUpdateUserName("LDy");
        transportOrderLogDTO.setCreateUserName("LDy");
        transportOrderLogDTO.setCreateUserId(1);
        transportOrderLogDTO.setUpdateUserId(1);
        transportOrderLogDTO.setType("transport_order");

        transportOrderService.update(transportOrderLogDTO);
    }
}
