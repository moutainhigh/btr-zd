package com.baturu.zd.dto.app;

import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.dto.wx.WxAddressSnapshotDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 运单详情查询接口返回的数据项
 *
 * @author CaiZhuliang
 * @since 2019-3-25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppTransportOrderDTO implements Serializable {

    private Integer id;

    /**
     * 预约单id
     */
    private Integer reservationId;

    /**
     * 运单号
     */
    private String transportOrderNo;

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
    private WxAddressSnapshotDTO senderAddr;

    /**
     * 收件人地址簿id
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
     * 保价费
     */
    private BigDecimal supportValuePayment;

    /**
     * 钉箱费
     */
    private BigDecimal nailBoxPayment;

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
     * 运费
     */
    private BigDecimal freight;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 网点id
     */
    private Integer servicePointId;

    /**
     * 包裹列表
     */
    private List<PackageDTO> packages;

    /**
     * 第一个仓库名称
     */
    private String firstWarehouse;

    /**
     * 第一个仓位名称
     */
    private String firstPosition;

    /**
     * 第二个仓库名称
     */
    private String secondWarehouse;

    /**
     * 第二个仓位名称
     */
    private String secondPosition;

    /**
     * 第三个仓库名称
     */
    private String thirdWarehouse;

    /**
     * 第三个仓位名称
     */
    private String thirdPosition;

    /**
     * 第四个仓库名称
     */
    private String fourthWarehouse;

    /**
     * 第四个仓库编码
     */
    private String fourthPosition;

    /**
     * 收货点名称
     */
    private String bizName;

    /**
     * 合伙人名称
     */
    private String partnerName;

    /**
     * 合伙人电话
     */
    private String partnerPhone;

    /**
     * 运单类型 1:正向 2：逆向 默认值为1
     */
    private Integer type;

    public static AppTransportOrderDTO copy(TransportOrderDTO transportOrderDTO) {
        AppTransportOrderDTO appTransportOrderDTO = AppTransportOrderDTO.builder().build();
        BeanUtils.copyProperties(transportOrderDTO, appTransportOrderDTO);
        return appTransportOrderDTO;
    }

    /**
     * AppTransportOrderDTO转成TransportOrderDTO
     * @param appTransportOrderDTO
     * @return
     */
    public static TransportOrderDTO toTransportOrderDTO(AppTransportOrderDTO appTransportOrderDTO) {
        TransportOrderDTO transportOrderDTO = TransportOrderDTO.builder().build();
        BeanUtils.copyProperties(appTransportOrderDTO, transportOrderDTO);
        return transportOrderDTO;
    }
}
