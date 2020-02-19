package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.FerryOrderDTO;
import com.baturu.zd.dto.web.excel.FerryOrderExcelDTO;
import com.baturu.zd.request.business.FerryOrderQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.List;

/**
 * @author liuduanyang
 * @since 2019/3/22
 */
public interface FerryOrderService {

    /**
     * 分页查询摆渡单(主表)
     * @param request
     * @return
     */
    ResultDTO<PageDTO> queryForPage(FerryOrderQueryRequest request);

    /**
     * 创建摆渡单
     * @param ferryOrderDTO
     * @return ResultDTO
     */
    ResultDTO<FerryOrderDTO> save(FerryOrderDTO ferryOrderDTO) throws Exception ;

    /**
     * 修改摆渡单
     * @param ferryOrderDTO
     * @return ResultDTO
     */
    ResultDTO<FerryOrderDTO> updateState(FerryOrderDTO ferryOrderDTO);

    /**
     * Excel导出摆渡单
     * @param request
     * @return
     */
    List<FerryOrderExcelDTO> exportFerryOrderExcel(FerryOrderQueryRequest request);
}
