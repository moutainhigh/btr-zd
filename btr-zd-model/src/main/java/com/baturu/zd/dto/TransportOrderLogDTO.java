package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 运单更新请求
 * @author liuduanyang
 * @since 2019/5/17
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportOrderLogDTO extends AbstractLogDTO {

    /**
     * 寄件人姓名
     */
    private String senderName;

    /**
     * 寄件人联系电话
     */
    private String senderPhone;

    /**
     * 寄件人详细地址
     */
    private String senderAddress;

    /**
     * 收件人姓名
     */
    private String recipientName;

    /**
     * 收件人联系电话
     */
    private String recipientPhone;

    /**
     * 收件人详细地址
     */
    private String recipientAddress;

    /**
     * 配送方式
     */
    private Integer deliveryType;

    /**
     * 货物重量
     */
    private BigDecimal weight;

    /**
     * 货物体积
     */
    private BigDecimal bulk;

    /**
     * 付款方式
     */
    private Integer payType;

    /**
     * 现收费用
     */
    private BigDecimal nowPayment;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 配送费
     */
    private BigDecimal dispatchPayment;

    /**
     * 钉箱费
     */
    private BigDecimal nailBoxPayment;

    /**
     * 保价费
     */
    private BigDecimal supportValuePayment;

    /**
     * 代收手续费
     */
    private BigDecimal collectPayment;

    /**
     * 总费用
     */
    private BigDecimal totalPayment;

    /**
     * 到付金额
     */
    private BigDecimal arrivePayment;

    /**
     * 是否同步修改tms的运单数据
     */
    private boolean sync;
}
