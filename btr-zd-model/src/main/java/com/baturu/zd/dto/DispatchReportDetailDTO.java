package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 派送报表详情DTO
 * @author liuduanyang
 * @since 2019/5/8
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchReportDetailDTO {

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 包裹数
     */
    private Integer packageNum;

    /**
     * 客户名称
     */
    private String customer;

    /**
     * 提成
     */
    private BigDecimal commision;
}
