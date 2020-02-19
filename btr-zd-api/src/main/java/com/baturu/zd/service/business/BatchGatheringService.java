package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.BatchGatheringDTO;
import com.baturu.zd.dto.common.BatchGatheringDetailDTO;
import com.baturu.zd.request.business.BatchGatheringQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.List;

/**
 * @author CaiZhuliang
 * @since 2019-10-17
 */
public interface BatchGatheringService {

    /**
     * 创建批量收款记录及其明细
     * @param batchGatheringDTO 批量收款记录(不用填写流水号)
     * @param transportOrderNos 批量收款的运单号
     */
    ResultDTO<BatchGatheringDTO> create(BatchGatheringDTO batchGatheringDTO, List<String> transportOrderNos);

    /**
     * 根据流水号查询记录
     * @param batchGatheringNo 流水号
     */
    ResultDTO<BatchGatheringDTO> queryByBatchGatheringNo(String batchGatheringNo);

    /**
     * 将对应的流水号的记录更改支付状态为支付成功，并返回流水号对应的明细记录
     * @param batchGatheringNo 流水号
     */
    ResultDTO<List<BatchGatheringDetailDTO>> updateStatusByBatchGatheringNo(String batchGatheringNo);

    /**
     * 分页查询批量收款记录
     */
    ResultDTO<PageDTO<BatchGatheringDTO>> queryInPage(BatchGatheringQueryRequest queryRequest);

    /**
     * 根据流水号删除批量收款记录
     * @param batchGatheringNo 流水号
     */
    ResultDTO<BatchGatheringDTO> deleteByBatchGatheringNo(String batchGatheringNo);
}
