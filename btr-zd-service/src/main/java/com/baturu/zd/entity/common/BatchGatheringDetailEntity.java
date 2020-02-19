package com.baturu.zd.entity.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baturu.zd.entity.AbstractBaseEntity;
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
@TableName("zd_batch_gathering_detail")
public class BatchGatheringDetailEntity extends AbstractBaseEntity {

    /**
     * 批量收款流水号
     */
    @TableField("batch_gathering_no")
    private String batchGatheringNo;

    /**
     *运单号
     */
    @TableField("transport_order_no")
    private String transportOrderNo;

}
