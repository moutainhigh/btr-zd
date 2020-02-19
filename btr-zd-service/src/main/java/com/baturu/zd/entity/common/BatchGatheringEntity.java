package com.baturu.zd.entity.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baturu.zd.entity.AbstractBaseEntity;
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
@TableName("zd_batch_gathering")
public class BatchGatheringEntity extends AbstractBaseEntity {

    /**
     * 批量收款流水号
     */
    @TableField("batch_gathering_no")
    private String batchGatheringNo;

    /**
     * 交易金额
     */
    @TableField("trx_amount")
    private BigDecimal trxAmount;

    /**
     * 收款状态 0:未收款 1:支付成功 2:支付失败
     */
    @TableField("status")
    private Integer status;

}
