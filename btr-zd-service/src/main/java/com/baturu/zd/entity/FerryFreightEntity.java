package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * create by pengdi in 2019/3/14
 * 摆渡运费enetity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("zd_ferry_freight")
public class FerryFreightEntity extends AbstractBaseEntity {

    /**
     * 始发站 网点id
     */
    @TableField("service_point_id")
    private Integer servicePointId;

    /**
     * 目的地 仓库id
     */
    @TableField("dest_warehouse_id")
    private Integer warehouseId;

    /**
     * 特大规格运费
     */
    @TableField("huge_freight")
    private BigDecimal hugeFreight;

    /**
     * 大件规格运费
     */
    @TableField("big_freight")
    private BigDecimal bigFreight;

    /**
     * 中件规格运费
     */
    @TableField("medium_freight")
    private BigDecimal mediumFreight;

    /**
     * 小件规格运费
     */
    @TableField("small_freight")
    private BigDecimal smallFreight;

    /**
     * 最低一票规格运费
     */
    @TableField("tiny_freight")
    private BigDecimal tinyFreight;

    /**
     * 创建人
     */
    @TableField("create_user_name")
    private String createUserName;
}
