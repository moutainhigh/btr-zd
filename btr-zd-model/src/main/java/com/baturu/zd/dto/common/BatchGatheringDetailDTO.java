package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CaiZhuliang
 * @since 2019-10-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchGatheringDetailDTO extends AbstractBaseDTO {

    /**
     * 批量收款流水号
     */
    private String batchGatheringNo;

    /**
     * 运单号
     */
    private String transportOrderNo;

}
