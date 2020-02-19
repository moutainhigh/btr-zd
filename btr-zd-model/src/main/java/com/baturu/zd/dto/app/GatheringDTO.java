package com.baturu.zd.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 预支付请求参数
 * @author CaiZhuliang
 * @since 2019-3-27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatheringDTO implements Serializable {

    /** 区域id 默认是4 : 华南*/
    private Integer regionId = 4;

    /** 运单号/摆渡单号 */
    private String payOrderNo;

    /** 运单id/摆渡单id */
    private Integer payOrderId;

    /** 运单类型 1：普通运单 2：摆渡单 */
    private Integer orderType;

    /** 支付订单类型 默认是2。1:交易订单 2：物流订单 */
    private Integer payOrderType = 2;

    /** 支付渠道 12:微信APP;18：通联（微信）;20:支付宝 */
    private Integer payChannel;

    /** 付款ip */
    private String customerIp;

    /** 交易金额 */
    private BigDecimal trxAmount;

    /** 支付具体下单终端类型 3：安卓；4：IOS */
    private Integer platform;

    /** 商品信息或运单信息描述，备用字段 */
    private String subject;

}
