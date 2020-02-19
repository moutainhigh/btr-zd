package com.baturu.zd.dto;

import com.baturu.zd.dto.wx.WxAddressSnapshotDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

/**
 * created by laijinjie by 2019/03/29
 **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WebTransportOrderDTO {

    private Integer id;

    /**
     * 预约单id
     */
    private Integer reservationId;

    /**
     * 寄件人地址簿id，从预约单跳转新增运单时必传
     */
    private Integer senderAddrId;

    private WxAddressSnapshotDTO senderAddr;

    /**
     * 收件人地址簿id，从预约单跳转新增运单时必传
     */
    private Integer recipientAddrId;

    private WxAddressSnapshotDTO recipientAddr;

    /**
     * 货物名称
     */
    private String goodName;

    /**
     * 件数
     */
    private Integer qty;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 体积
     */
    private BigDecimal bulk;

    /**
     * 包装
     */
    private String wrapping;


    /**
     * 付款方式 1：现付，2：到付，3：分摊
     */
    private Integer payType;

    /**
     * 保价
     */
    private BigDecimal supportValue;

    /**
     * 钉箱数
     */
    private Integer nailBoxNum;

    /**
     * 是否钉箱 0:否，1：是
     */
    private Boolean nailBox;


    /**
     * 代收金额
     */
    private BigDecimal collectAmount;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 代收款账号名称
     */
    private String collectAccountName;

    /**
     * 代收款账号
     */
    private String collectAccount;

    /**
     * 代收款状态 0：未收款；1：已收款
     */
    private Integer collectStatus;

    /**
     * 配送方式 1：送货上门；2：自提
     */
    private Integer deliveryType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 配送费
     */
    private BigDecimal dispatchPayment;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 保价费
     */
    private BigDecimal supportValuePayment;

    /**
     * 钉箱费
     */
    private BigDecimal nailBoxPayment;

    /**
     * 保险费
     */
    private BigDecimal insurancePayment;

    /**
     * 代收手续费
     */
    private BigDecimal collectPayment;

    /**
     * 应收费用
     */
    private BigDecimal totalPayment;

    /**
     * 现收费用
     */
    private BigDecimal nowPayment;

    /**
     * 到付费用
     */
    private BigDecimal arrivePayment;

    /**
     * 图片一
     */
    private String icon1;

    /**
     * 图片二
     */
    private String icon2;

    /**
     * 图片三
     */
    private String icon3;

    /**
     * 是否有效
     */
    private Boolean active;

    /**
     * WebTransportOrderDTO
     *
     * @param webTransportOrderDTO
     * @return
     */
    public static TransportOrderDTO toTransportOrderDTO(WebTransportOrderDTO webTransportOrderDTO) {
        TransportOrderDTO transportOrderDTO = TransportOrderDTO.builder().build();
        BeanUtils.copyProperties(webTransportOrderDTO, transportOrderDTO);
        return transportOrderDTO;
    }
}
