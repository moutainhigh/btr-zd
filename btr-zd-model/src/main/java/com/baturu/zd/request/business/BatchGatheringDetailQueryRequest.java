package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CaiZhuliang
 * @since 2019-10-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchGatheringDetailQueryRequest extends BaseRequest {

    /**
     * 流水号
     */
    private String batchGatheringNo;

    /**
     * 运单号
     */
    private String transportOrderNo;

}
