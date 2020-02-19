package com.baturu.zd.dto;

import com.baturu.zd.dto.wx.WxAddressSnapshotDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * created by ketao by 2019/03/12
 **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransportOrderDTO implements Serializable {

    private Integer id;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     * 创建用户名称
     */
    private String createUserName;


    /**
     * 更新用户id
     */
    private Integer updateUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 网点id
     */
    private Integer servicePointId;

    /**
     * 是否有效
     */
    private Boolean active;


    /**
     * 运单状态 0：已开单 10：运输中 20：已验收 30：已配送 40：已取消
     *
     * @see com.baturu.zd.enums.TransportOrderStateEnum
     */
    private Integer state;


    /**
     * 运单收款状态
     * @see com.baturu.zd.enums.GatheringStatusEnum
     */
    private Integer gatheringStatus;


    /**
     * 寄件人地址簿id
     */
    private Integer senderAddrId;

    /**
     * 寄件人地址簿快照id
     */
    private Integer senderAddrSnapshotId;

    private WxAddressSnapshotDTO senderAddr;

    /**
     * 收件人地址簿id
     */
    private Integer recipientAddrId;

    /**
     * 收件人地址簿快照id
     */
    private Integer recipientAddrSnapshotId;

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
     * 中转费
     */
    private BigDecimal transferPayment;

    /**
     * 劳务费
     */
    private BigDecimal laborPayment;

    /**
     * 代收手续费
     */
    private BigDecimal collectPayment;

    /**
     * 其他收费
     */
    private BigDecimal otherPayment;

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
     * 签收时间
     */
    private Date signTime;

    /**
     *网点所绑定的仓库id
     */
    private Integer warehouseId;

    /**
     * 运单类型 1:正向 2：逆向 默认值为1
     */
    private Integer type;

    /**
     * 运单下的包裹列表
     */
    private List<PackageDTO> packageDTOList;
}
