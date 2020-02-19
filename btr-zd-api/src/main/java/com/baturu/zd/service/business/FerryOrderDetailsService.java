package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.web.excel.FerryOrderDetailsExcelDTO;
import com.baturu.zd.request.business.FerryOrderDetailQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.List;

/**
 * create by pengdi in 2019/3/25
 * 摆渡单明细 biz 服务
 */
public interface FerryOrderDetailsService {
    /**
     * 分页查询摆渡单明细
     * @param request
     * @return
     */
    ResultDTO<PageDTO> queryForPage(FerryOrderDetailQueryRequest request);

    /**
     * Excel导出摆渡单明细
     * @param request
     * @return
     */
    List<FerryOrderDetailsExcelDTO> exportFerryOrderDetails(FerryOrderDetailQueryRequest  request);

    /**
     * 根据摆渡单号查摆渡单包裹号
     */
    List<String> getPackageNoList(Integer ferryOrderId);
}
