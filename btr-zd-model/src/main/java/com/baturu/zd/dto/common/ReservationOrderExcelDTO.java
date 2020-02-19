package com.baturu.zd.dto.common;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预约单导出
 * created by laijinjie by 2019/03/28
 **/
@Data
@ExcelTarget("reservationOrderExcel")
public class ReservationOrderExcelDTO implements Serializable {

    /**
     * 预约单号
     */
    @Excel(name = "预约单号 ")
    private String reservationNo;

    /**
     * 下单人姓名
     */
    @Excel(name = "下单人姓名")
    private String createUserName;
    /**
     * 下单人电话
     */
    @Excel(name = "下单人手机")
    private String createUserPhone;

    /**
     * 寄件人姓名
     */
    @Excel(name = "寄件人姓名")
    private String senderName;
    /**
     * 寄件人电话
     */
    @Excel(name = "寄件人电话")
    private String sendderPhone;
    /**
     * 寄件人详细地址
     */
    @Excel(name = "寄件人详细地址")
    private String senderAddress;
    /**
     * 寄件人省份
     */
    @Excel(name = "寄件人省份")
    private String senderProvinceName;
    /**
     * 寄件人城市
     */
    @Excel(name = "寄件人城市")
    private String senderCityName;
    /**
     * 寄件人区域
     */
    @Excel(name = "寄件人区域")
    private String senderCountyName;

    /**
     * 寄件人公司
     */
    @Excel(name = "寄件人公司")
    private String senderCompany;
    /**
     * 收件人姓名
     */
    @Excel(name = "收件人姓名")
    private String recipientName;
    /**
     * 收件人电话
     */
    @Excel(name = "收件人电话")
    private String recipientPhone;
    /**
     * 收件人详细地址
     */
    @Excel(name = "收件人详细地址")
    private String recipientAddress;
    /**
     * 收件人省份
     */
    @Excel(name = "收件人省份")
    private String recipientProvinceName;
    /**
     * 收件人城市
     */
    @Excel(name = "收件人城市")
    private String recipientCityName;
    /**
     * 收件人区域
     */
    @Excel(name = "收件人区域")
    private String recipientCountyName;

    /**
     * 收件人公司
     */
    @Excel(name = "收件人公司")
    private String recipientCompany;

    /**
     * 件数
     */
    @Excel(name = "件数")
    private Integer qty;

    /**
     * 货物品名
     */
    @Excel(name = "货物品名")
    private String goodName;

    /**
     * 重量(kg)
     */
    @Excel(name = "重量(kg)")
    private BigDecimal weight;

    /**
     * 体积(立方米)
     */
    @Excel(name = "体积(立方米)")
    private BigDecimal bulk;
    /**
     * 是否钉箱
     */
    @Excel(name = "钉箱")
    private Boolean nailBox;

    /**
     * 保价
     */
    @Excel(name = "保价")
    private BigDecimal supportValue;
    /**
     * 代收金额
     */
    @Excel(name = "代收金额")
    private BigDecimal collectAmount;

    /**
     * 开户行
     */
    @Excel(name = "开户行")
    private String bankName;

    /**
     * 代收款账号名称
     */
    @Excel(name = "代收款账号名称")
    private String collectAccountName;

    /**
     * 代收款账号
     */
    @Excel(name = "代收款账号")
    private String collectAccount;


    @Excel(name = "预约下单时间", exportFormat = "yyyy-MM-dd HH:mm:SS")
    private Date createTime;

    /**
     * 配送方式，1：送货上门，2：自提
     */
    @Excel(name = "配送方式", replace = {"送货上门_1", "自提_2"})
    private Integer deliveryType;

    /**
     * 付款方式 1：现付，2：到付，3：分摊
     */
    @Excel(name = "付款方式", replace = {"现付_1", "到付_2", "分摊_3"})
    private Integer payType;

    /**
     * 预约单状态：10:已预约;20:已开单;30:已取消
     */
    @Excel(name = "预约单状态", replace = {"已预约_10", "已开单_20", "已取消_30"})
    private String state;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

}
