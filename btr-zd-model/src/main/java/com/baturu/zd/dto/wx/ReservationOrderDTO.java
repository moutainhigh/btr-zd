package com.baturu.zd.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 微信预约单
 * created by ketao by 2019/03/06
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationOrderDTO extends BaseWxDTO implements Serializable {

    private Integer id;

    /**
     * 预约单号
     */
    private String reservationNo;

    /**
     * 运单号（预约单已开单状态下回填）
     */
    private String transportOrderNo;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     * 更新用户id
     */
    private Integer updateUserId;

    /**
     * 是否有效；0：否，1：是
     */
    private Boolean active;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 发货地址簿id
     */
    private Integer senderAddrId;

    /**
     * 发货地址簿
     */
    private WxAddressDTO senderAddress;


    /**
     * 收货地址簿id
     */
    private Integer recipientAddrId;

    /**
     * 收货地址簿
     */
    private WxAddressDTO recipientAddress;

    /**
     * 货物名称
     */
    private String goodName;

    /**
     * 件数
     */
    private Integer qty;


    /**
     * 重量(kg)
     */
    private BigDecimal weight;


    /**
     * 体积(立方米)
     */
    private BigDecimal bulk;

    /**
     * 付款方式 1：现付，2：到付，3：分摊
     */
    private Integer payType;

    /**
     * 钉箱数
     */
    private Integer nailBoxNum;

    /**
     * 是否钉箱
     */
    private Boolean nailBox;

    /**
     * 保价
     */
    private BigDecimal supportValue;

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
     * 配送方式，1：送货上门，2：自提
     */
    private Integer deliveryType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 预约单状态：10:已预约;20:已开单;30:已取消
     */
    private Integer state;

    /**
     * 下单人姓名
     */
    private String createUserName;
    /**
     * 下单人电话
     */
    private String createUserPhone;


    /**
     * 处理用户id（当前微信账号存在母账号即为母账号id）
     */
    private Integer operatorId;
}
