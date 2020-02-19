package com.baturu.zd.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 预约单的统计信息
 * created by laijinjie by 2019/03/28
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationOrderSummaryDTO implements Serializable {

    /**
     * 预约单数量
     */
    private Integer totalOrderCount;
    /**
     * 已开单数量
     */
    private Integer orderedCount;
    /**
     * 预约单总重量
     */
    private BigDecimal totalWeight;
    /**
     * 预约单总体积
     */
    private BigDecimal totalBulk;
}
