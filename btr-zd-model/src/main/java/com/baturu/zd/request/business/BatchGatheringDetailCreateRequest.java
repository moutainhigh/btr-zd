package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CaiZhuliang
 * @since 2019-10-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchGatheringDetailCreateRequest {

    /**
     * 批量收款流水号
     */
    private String batchGatheringNo;

    /**
     * 运单号
     */
    private List<String> transportOrderNos;

    /**
     * 操作人
     */
    private Integer userId;

}
