package com.baturu.zd.dto.web;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * PC前端内容展示DTO
 * created by laijinjie by 2019/04/09
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ExcelTarget("packageWebExcelDTO")
public class PackageWebExcelDTO implements Serializable {

    /**
     * 运单号
     */
    @Excel(name = "运单号")
    private String transportOrderNo;

    /**
     * 包裹号
     */
    @Excel(name = "包裹号")
    private String packageNo;

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
     * 当前位置
     */
    @Excel(name = "当前位置")
    private String location;

    /**
     * 包裹状态
     */
    @Excel(name = "包裹状态", replace = {"已开单_0", "已装车_10", "已收货_20", "已发货_30", "已装运_40", "已验收_50", "已配送_60", "已取消_70"})
    private Integer state;

    /**
     * 配送方式
     */
    @Excel(name = "配送方式", replace = {"送货上门_1", "自提_2"})
    private Integer deliveryType;

    /**
     * 发货人
     */
    @Excel(name = "发货人")
    private String sender;

    /**
     * 发货人电话
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
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

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
