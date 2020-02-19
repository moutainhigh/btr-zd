package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author CaiZhuliang
 * @since 2019-10-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchGatheringDTO extends AbstractBaseDTO {

    /**
     * 批量收款流水号
     */
    private String batchGatheringNo;

    /**
     * 交易金额
     */
    private BigDecimal trxAmount;

    /**
     * 收款状态 0:未收款 1:支付成功 2:支付失败
     */
    private Integer status;

}
