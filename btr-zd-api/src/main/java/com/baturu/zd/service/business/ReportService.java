package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.DispatchReportDTO;
import com.baturu.zd.request.business.TransportOrderQueryRequest;

/**
 * 报表service
 * @author liuduanyang
 * @since 2019/5/8
 */
public interface ReportService {

    /**
     * 根据用户查询已配送的运单
     * @param transportOrderQueryRequest 查询条件
     * @return
     */
    ResultDTO<DispatchReportDTO> getDispatchReport(TransportOrderQueryRequest transportOrderQueryRequest);
}
