package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 派送报表DTO
 * @author liuduanyang
 * @since 2019/5/8
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchReportDTO implements Serializable {

    /**
     * 运单数
     */
    private Long transportOrderNum;

    /**
     * 包裹数
     */
    private Integer packageNum;

    /**
     * 总提成
     */
    private BigDecimal commision;

    /**
     * 报表详细
     */
    private List<DispatchReportDetailDTO> dispatchReportDetailList;
}
