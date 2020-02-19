package com.baturu.zd.dto;

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
 * 运单异常excelDTO
 * @author liuduanyang
 * @since 2019/5/31
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ExcelTarget("orderExceptionExcel")
public class OrderExceptionExcelDTO implements Serializable {

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
     * 包裹状态
     * 0:已开单；10：已装车；20：已收货；30：已发货；40：已装运；50：已验收；60：已配送；70：已取消
     */
    @Excel(name = "包裹状态", replace = {"已开单_0", "已装车_10", "已收货_20", "已发货_30", "已装运_40, 已验收_50, 已配送_60, 已取消_70"})
    private Integer packageState;

    /**
     * 体积
     */
    @Excel(name = "体积")
    private BigDecimal bulk;

    /**
     * 费用
     */
    @Excel(name = "费用")
    private BigDecimal payment;

    /**
     * 异常类型 10：货物破损 20：丢失 30：验收异常 40：配送异常 50：其他
     */
    @Excel(name = "异常类型", replace = {"货物破损_10", "丢失_20", "验收异常_30", "配送异常_40", "其他_50"})
    private Integer type;

    /**
     * 异常备注
     */
    @Excel(name = "异常备注")
    private String remark;

    /**
     * 状态
     */
    @Excel(name = "状态", replace = {"待处理_0", "处理中_1", "已处理_2"})
    private Integer state;

    /**
     * 处理结果  0：未处理 10：定责 20：退还 30：关闭
     */
    @Excel(name = "处理结果", replace = {"未处理_0", "定责_10", "退还_20", "关闭_30"})
    private Integer handleResult;

    /**
     * 异常上报时间
     */
    @Excel(name = "异常上报时间", exportFormat = "yyyy-MM-dd HH:mm:SS")
    private Date reportTime;
}
