package com.baturu.zd.entity.wx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baturu.zd.entity.AbstractBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 微信预约单
 * created by ketao by 2019/03/06
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("zd_wx_reservation_order")
public class ReservationOrderEntity extends AbstractBaseEntity {

    /**
     * 预约单号
     */
    @TableField("reservation_no")
    private String reservationNo;

    /**
     * 运单号（预约单已开单状态下回填）
     */
    @TableField("transport_order_no")
    private String transportOrderNo;

    /**
     * 发货地址簿JSON
     */
    @TableField("sender_addr_id")
    private Integer senderAddrId;

    /**
     * 收货地址簿JSON
     */
    @TableField("recipient_addr_id")
    private Integer recipientAddrId;

    /**
     * 货物名称
     */
    @TableField("good_name")
    private String goodName;

    /**
     * 件数
     */
    @TableField("qty")
    private Integer qty;


    /**
     * 重量(kg)
     */
    @TableField("weight")
    private BigDecimal weight;


    /**
     * 体积(立方米)
     */
    @TableField("bulk")
    private BigDecimal bulk;

    /**
     * 付款方式 1：现付，2：到付，3：分摊
     */
    @TableField("pay_type")
    private Integer payType;

    /**
     * 钉箱数*
     */
    @TableField("nail_box_num")
    private Integer nailBoxNum;

    /**
     * 是否钉箱
     */
    @TableField("nail_box")
    private Boolean nailBox;


    /**
     * 保价
     */
    @TableField("support_value")
    private BigDecimal supportValue;

    /**
     * 代收金额
     */
    @TableField("collect_amount")
    private BigDecimal collectAmount;

    /**
     * 开户行
     */
    @TableField("bank_name")
    private String bankName;

    /**
     * 代收款账号名称
     */
    @TableField("collect_account_name")
    private String collectAccountName;

    /**
     * 代收款账号
     */
    @TableField("collect_account")
    private String collectAccount;

    /**
     * 配送方式，1：送货上门，2：自提
     */
    @TableField("delivery_type")
    private Integer deliveryType;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


    /**
     * 预约单状态：10:已预约;20:已开单;30:已取消
     */
    private Integer state;

    /**
     * 下单人姓名
     */
    @TableField(exist = false)
    private String createUserName;

    /**
     * 下单人电话
     */
    @TableField(exist = false)
    private String createUserPhone;

    /**
     * 处理用户id（当前微信账号存在母账号即为母账号id）
     */
    @TableField("operator_id")
    private Integer operatorId;
}
