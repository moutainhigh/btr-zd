package com.baturu.zd.vo.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 逐道WEB批量号查询控制器
 * @author CaiZhuliang
 * @since 2019-10-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryPayResultVO {

    /**
     * 运单支付订单实体主键id
     */
    private Integer payCarrierOrderId;

    /**
     * 运单id
     */
    private Integer carrierId;

    /**
     * 运单编号
     */
    private String carrierNo;

    /**
     * 运单金额
     */
    private BigDecimal carrierAmount;

    /**
     * 支付账户id
     */
    private Integer payAccountId;

    /**
     * 支付账号
     */
    private String payAccountNo;

    /**
     * 账户主体业务num
     */
    private Integer accountBodyNum;

    /**
     * 运单付款类型
     * @see com.baturu.paybuss.pay2.constants.CarrierPayTypeEnum
     */
    private Integer carrierType;

    /**
     * 大区id
     */
    private Integer carrierRegionId;

    /**
     * 渠道
     */
    private Integer channel;

    /**
     * 支付状态
     * @see com.baturu.paybuss.pay2.constants.PayOrderStatusEnum
     */
    private Integer payStatus;

    /**
     * 收款人
     */
    private String scanner;

    /**
     * 包裹号前缀
     */
    private String packSuffix;

    /**
     * 运单类型
     * @see com.baturu.paybuss.pay2.constants.CarrierOrderTypeEnum
     */
    private Integer orderType;

}
