package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.web.FerryFreightDTO;
import com.baturu.zd.dto.web.excel.FerryFreightExcelDTO;
import com.baturu.zd.request.business.FerryFreightQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.List;

/**
 * create by pengdi in 2019/3/18
 * 摆渡运费  biz服务
 */
public interface FerryFreightService {
    /**
     * 分页查询
     * @param request
     * @return
     */
    ResultDTO<PageDTO> selectForPage(FerryFreightQueryRequest request);

    /**
     * 新增/更新运费配置
     * @param ferryFreightDTO
     * @return
     */
    ResultDTO<FerryFreightDTO> saveOrUpdate(FerryFreightDTO ferryFreightDTO);

    /**
     * excel导出
     */
    List<FerryFreightExcelDTO> exportFerryFreightExcel(FerryFreightQueryRequest request);




}
