package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.BatchGatheringDetailDTO;
import com.baturu.zd.request.business.BatchGatheringDetailCreateRequest;
import com.baturu.zd.request.business.BatchGatheringDetailQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.List;

/**
 * @author CaiZhuliang
 * @since 2019-10-17
 */
public interface BatchGatheringDetailService {

    /**
     * 创建批量收款明细
     * @param request 流水号，运单号，操作人是必填
     */
    ResultDTO<List<BatchGatheringDetailDTO>> batchCreate(BatchGatheringDetailCreateRequest request);

    /**
     * 根据流水号查明细
     * @param batchGatheringNo 流水号
     */
    ResultDTO<List<BatchGatheringDetailDTO>> queryByBatchGatheringNo(String batchGatheringNo);

    /**
     * 根据流水号分页查询明细
     */
    ResultDTO<PageDTO<BatchGatheringDetailDTO>> queryInPage(BatchGatheringDetailQueryRequest queryRequest);

    /**
     * 根据流水号删除明细记录
     * @param batchGatheringNo 流水号
     */
    ResultDTO<List<BatchGatheringDetailDTO>> deleteByBatchGatheringNo(String batchGatheringNo);

    /**
     * 根据运单查询流水明细
     * @param transportOrderNos 运单号
     */
    ResultDTO<List<BatchGatheringDetailDTO>> queryByTransportOrderNo(List<String> transportOrderNos);
}
