package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * created by ketao by 2019/03/12
 **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("zd_transport_order")
public class TransportOrderEntity extends AbstractBaseEntity {

    /**
     *运单号
     */
    @TableField("transport_order_no")
    private String transportOrderNo;

     /**
     *网点id
     */
    @TableField("service_point_id")
    private Integer servicePointId;

    /**
     *运单状态 0：已开单 10：运输中 20：已验收 30：已配送 40：已取消
     * @see com.baturu.zd.enums.TransportOrderStateEnum
     */
    @TableField("state")
    private Integer state;

    /**
     * 运单收款状态
     * @see com.baturu.zd.enums.GatheringStatusEnum
     */
    @TableField("gathering_status")
    private Integer gatheringStatus;

    /**
     *寄件人地址簿快照id
     */
    @TableField("sender_addr_snapshot_id")
    private Integer senderAddrSnapshotId;

    /**
     *收件人地址簿id
     */
    @TableField("recipient_addr_snapshot_id")
    private Integer recipientAddrSnapshotId;

    /**
     *货物名称
     */
    @TableField("good_name")
    private String goodName;

    /**
     *件数
     */
    @TableField("qty")
    private Integer qty;

    /**
     *重量
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     *体积
     */
    @TableField("bulk")
    private BigDecimal bulk;


    /**
     *包装
     */
    @TableField("wrapping")
    private String wrapping;

    /**
     *付款方式 1：现付，2：到付，3：分摊
     */
    @TableField("pay_type")
    private Integer payType;

    /**
     *保价
     */
    @TableField("support_value")
    private BigDecimal supportValue;

    /**
     *钉箱数
     */
    @TableField("nail_box_num")
    private Integer nailBoxNum;

    /**
     *是否钉箱 0:否，1：是
     */
    @TableField("nail_box")
    private Boolean nailBox;

    /**
     *代收金额
     */
    @TableField("collect_amount")
    private BigDecimal collectAmount;

    /**
     *开户行
     */
    @TableField("bank_name")
    private String bankName;

    /**
     *代收款账号名称
     */
    @TableField("collect_account_name")
    private String collectAccountName;

    /**
     *代收款账号
     */
    @TableField("collect_account")
    private String collectAccount;

    /**
     *代收款状态 0：未收款；1：已收款
     */
    @TableField("collect_status")
    private Integer collectStatus;

    /**
     *配送方式 1：送货上门；2：自提
     */
    @TableField("delivery_type")
    private Integer deliveryType;

    /**
     *备注
     */
    @TableField("remark")
    private String remark;

    /**
     *配送费
     */
    @TableField("dispatch_payment")
    private BigDecimal dispatchPayment;

    /**
     *运费
     */
    @TableField("freight")
    private BigDecimal freight;

    /**
     *保价费
     */
    @TableField("support_value_payment")
    private BigDecimal supportValuePayment;

    /**
     *钉箱费
     */
    @TableField("nail_box_payment")
    private BigDecimal nailBoxPayment;

    /**
     *保险费
     */
    @TableField("insurance_payment")
    private BigDecimal insurancePayment;

    /**
     *中转费
     */
    @TableField("transfer_payment")
    private BigDecimal transferPayment;

    /**
     *劳务费
     */
    @TableField("labor_payment")
    private BigDecimal laborPayment;

    /**
     *代收手续费
     */
    @TableField("collect_payment")
    private BigDecimal collectPayment;

    /**
     *其他收费
     */
    @TableField("other_payment")
    private BigDecimal otherPayment;

    /**
     *应收费用
     */
    @TableField("total_payment")
    private BigDecimal totalPayment;

    /**
     *现收费用
     */
    @TableField("now_payment")
    private BigDecimal nowPayment;

    /**
     *到付费用
     */
    @TableField("arrive_payment")
    private BigDecimal arrivePayment;

    /**
     *图片一
     */
    @TableField("icon_1")
    private String icon1;

    /**
     *图片二
     */
    @TableField("icon_2")
    private String icon2;

    /**
     *图片三
     */
    @TableField("icon_3")
    private String icon3;

    /**
     *网点所绑定的仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouseId;

    /**
     * 运单类型 1:正向 2：逆向 默认值为1
     */
    @TableField("type")
    private Integer type;
}
