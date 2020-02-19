package com.baturu.zd.service.dto.web;

import com.baturu.zd.dto.web.TransportOrderWebDTO;
import com.baturu.zd.service.dto.common.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 运单查询返回
 * @author CaiZhuliang
 * @since 2019-10-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebQueryTransportOrderDTO implements Serializable {

    /**
     * 分页信息
     */
    private PageDTO<TransportOrderWebDTO> pageDTO;

    /**
     * 查询出的运单底下所有包裹数量
     */
    private Integer sumPackageNum;

    /**
     * 这次查询结果的统计总费用
     */
    private BigDecimal sumTotalPayment;

    /**
     * 这次查询结果的统计总运费
     */
    private BigDecimal sumFreight;

    /**
     * 这次查询结果的统计总钉箱
     */
    private BigDecimal sumNailBoxPayment;

    /**
     * 这次查询结果的统计总保价费
     */
    private BigDecimal sumSupportValuePayment;

    /**
     * 这次查询结果的统计总代收手续费
     */
    private BigDecimal sumCollectPayment;

    /**
     * 这次查询结果的统计总代收金额
     */
    private BigDecimal sumCollectAmount;

}
