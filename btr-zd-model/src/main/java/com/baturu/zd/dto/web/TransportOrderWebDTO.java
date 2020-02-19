package com.baturu.zd.dto.web;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * PC前端内容展示,EXCEL导出DTO
 * created by ketao by 2019/03/28
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ExcelTarget("reservationOrderExcel")
public class TransportOrderWebDTO implements Serializable {

    /**
     * 运单号
     */
    @Excel(name = "运单号")
    private String transportOrderNo;

    /**
     * 运单类型
     */
    @Excel(name = "运单类型", replace = {"正向开单_1", "逆向开单_2"})
    private Integer type;

    /**
     * 件数
     */
    @Excel(name = "件数")
    private String qty;

    /**
     * 总费用
     */
    @Excel(name = "总费用")
    private BigDecimal totalPayment;

    /**
     * 运费
     */
    @Excel(name = "运费")
    private BigDecimal freight;

    /**
     * 配送费
     */
    @Excel(name = "配送费")
    private BigDecimal dispatchPayment;

    /**
     * 钉箱费
     */
    @Excel(name = "钉箱费")
    private BigDecimal nailBoxPayment;

    /**
     * 保价
     */
    @Excel(name = "保价")
    private BigDecimal supportValue;

    /**
     * 保价费
     */
    @Excel(name = "保价费")
    private BigDecimal supportValuePayment;

    /**
     * 代收金额
     */
    @Excel(name = "代收金额")
    private BigDecimal collectAmount;

    /**
     * 代收手续费
     */
    @Excel(name = "代收手续费")
    private BigDecimal collectPayment;

    /**
     * 开户行
     */
    @Excel(name = "开户行")
    private String bankName;

    /**
     * 收款账号
     */
    @Excel(name = "收款账号")
    private String collectAccount;

    /**
     * 账户名
     */
    @Excel(name = "账户名")
    private String collectAccountName;

    /**
     * 现收费用
     */
    @Excel(name = "现收")
    private BigDecimal nowPayment;

    /**
     * 到付费用
     */
    @Excel(name = "到收")
    private BigDecimal arrivePayment;

    /**
     * 揽收网点
     */
    @Excel(name = "揽收网点")
    private String servicePoint;

    /**
     * 起始仓
     */
    @Excel(name = "起始仓")
    private String warehouseName;

    /**
     *当前位置
     */
    @Excel(name = "当前位置")
    private String location;

    /**
     * 运单状态
     */
    @Excel(name = "运单状态", replace = {"已开单_0", "运输中_10", "已验收_20", "已配送_30", "已取消_40"})
    private Integer state;

    /**
     * 配送方式
     */
    @Excel(name = "配送方式", replace = {"自提_1", "送货上门_2"})
    private Integer deliveryType;

    /**
     * 付款方式
     */
    @Excel(name = "付款方式", replace = {"现付_1", "到付_2", "分摊_3"})
    private Integer payType;


    /**
     * 支付状态
     */
    @Excel(name = "支付状态", replace = {"未付款_0", "已付款_1"})
    private Integer gatheringStatus;

    /**
     *发货人
     */
    @Excel(name = "发货人")
    private String sender;

    /**
     *发货人电话
     */
    @Excel(name = "发货人电话")
    private String senderPhone;

    /**
     * 收货人
     */
    @Excel(name = "收货人")
    private String recipient;

    /**
     * 收货人电话
     */
    @Excel(name = "收货人电话")
    private String recipientPhone;

    /**
     * 仓位
     */
    @Excel(name = "仓位")
    private String position;

    /**
     * 收货点
     */
    @Excel(name = "收货点")
    private String bizName;

    /**
     * 收货地址
     */
    @Excel(name = "收货地址")
    private String recipientAddr;

    /**
     * 合伙人(合伙人团队)
     */
    @Excel(name = "合伙人(合伙人团队)")
    private String partner;

    /**
     * 配送员
     */
    @Excel(name = "配送员")
    private String shipper;

    /**
     * 配送人联系方式
     */
    @Excel(name = "配送人联系方式")
    private String shipperPhone;

    /**
     *  备注
     */
    @Excel(name = "备注")
    private String remark;

    @Excel(name = "创建人")
    private String createUserName;

    @Excel(name = "创建时间", exportFormat = "yyyy-MM-dd HH:mm:SS")
    private Date createTime;



    private Integer createUserId;

    /**
     * 最后更新人
     */
    @Excel(name = "最后更新人")
    private String updateUserName;

    /**
     * 最后更新时间
     */
    @Excel(name = "最后更新时间", exportFormat = "yyyy-MM-dd HH:mm:SS")
    private Date updateTime;


}
