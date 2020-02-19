package com.baturu.zd.dto.app;

import com.baturu.zd.dto.wx.WxAddressDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * APP预约单
 * @author liuduanyang
 * @since 2019-3-21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppReservationOrderDTO implements Serializable {

    private Integer id;

    /**
     * 预约单号
     */
    private String reservationNo;

    /**
     * 预约单状态
     */
    private Integer state;

    /**
     * 货物数量
     */
    private Integer qty;

    private WxAddressDTO senderAddr;

    /**
     * 发货地址簿快照id
     */
    private Integer senderAddrId;

    /**
     * 收货地址簿快照id
     */
    private Integer recipientAddrId;

    private WxAddressDTO recipientAddr;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 体积
     */
    private BigDecimal bulk;

    /**
     * 货物名称
     */
    private String goodName;

    /**
     * 保价
     */
    private BigDecimal supportValue;

    /**
     * 代收金额
     */
    private BigDecimal collectAmount;

    /**
     * 代收账户
     */
    private String collectAccount;

    /**
     * 代收款账号人
     */
    private String collectAccountName;

    /**
     * 付款方式
     */
    private Integer payType;

    /**
     * 代收款账号人
     */
    private Integer deliveryType;

    /**
     * 是否钉箱
     */
    private Boolean nailBox;

    /**
     * 钉箱数
     */
    private Integer nailBoxNum;

    /**
     * 备注
     */
    private String remark;

    /**
     * 开户行
     */
    private String bankName;

    /**
     *创建时间
     */
    private Date createTime;

    /**
     *更新时间
     */
    private Date updateTime;
}
