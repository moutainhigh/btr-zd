package com.baturu.zd.controller.app;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.DispatchReportDTO;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.enums.TransportOrderStateEnum;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.service.business.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 报表controller
 * @author liuduanyang
 * @since 2019/5/8 10:33
 */
@RestController
@Slf4j
@RequestMapping("app/report")
public class ReportController extends AbstractAppBaseController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/dispatchReport")
    public ResultDTO dispatchReport(TransportOrderQueryRequest transportOrderQueryRequest) {
        if (transportOrderQueryRequest == null) {
            transportOrderQueryRequest = new TransportOrderQueryRequest();
        }
        Integer current = transportOrderQueryRequest.getCurrent();
        Integer size = transportOrderQueryRequest.getSize();
        Integer days = transportOrderQueryRequest.getDays();

        if (current == null || current == 0) {
            transportOrderQueryRequest.setCurrent(1);
        }
        if (size == null || size == 0) {
            transportOrderQueryRequest.setSize(20);
        }
        if (days == null || days == 0) {
            transportOrderQueryRequest.setDays(1);
        }

        AppUserLoginInfoDTO appUserLoginInfoDTO = getCurrentUserInfo();
        transportOrderQueryRequest.setCreateUserId(appUserLoginInfoDTO.getUserId());
        transportOrderQueryRequest.setState(TransportOrderStateEnum.EXPRESSED.getType());

        ResultDTO<DispatchReportDTO> dispatchReport = reportService.getDispatchReport(transportOrderQueryRequest);
        return dispatchReport;
    }
}
