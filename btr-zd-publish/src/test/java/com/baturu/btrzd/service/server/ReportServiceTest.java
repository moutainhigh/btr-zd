package com.baturu.btrzd.service.server;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.DispatchReportDTO;
import com.baturu.zd.enums.TransportOrderStateEnum;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.service.business.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liuduanyang on 2019/5/8
 */
@Slf4j
public class ReportServiceTest extends BaseTest {

    @Autowired
    private ReportService reportService;

    @Test
    public void testGetDispatchReport() {
        TransportOrderQueryRequest transportOrderQueryRequest = new TransportOrderQueryRequest();
        transportOrderQueryRequest.setCurrent(1);
        transportOrderQueryRequest.setSize(20);
        transportOrderQueryRequest.setState(TransportOrderStateEnum.EXPRESSED.getType());
        transportOrderQueryRequest.setCreateUserId(19);

        ResultDTO<DispatchReportDTO> dispatchReport = reportService.getDispatchReport(transportOrderQueryRequest);
        log.info("getDispatchReport", dispatchReport);
        Assert.assertNotNull(dispatchReport.getModel());
    }
}
