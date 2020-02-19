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
@ExcelTarget("blameExcel")
public class BlameExcelDTO implements Serializable {

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
     * 异常类型 10：货物破损 20：丢失 30：验收异常 40：配送异常 50：其他
     */
    @Excel(name = "异常类型", replace = {"货物破损_10", "丢失_20", "验收异常_30", "配送异常_40", "其他_50"})
    private Integer type;

    /**
     * 责任部门
     */
    @Excel(name = "责任部门")
    private String blameName;

    /**
     * 赔偿金额
     */
    @Excel(name = "定责金额")
    private BigDecimal indemnity;

    /**
     * 定责备注
     */
    @Excel(name = "备注")
    private String blameRemark;

    /**
     * 状态
     */
    @Excel(name = "状态", replace = {"待审核_0", "已审核_10", "已驳回_20"})
    private Integer state;

    /**
     * 定责时间
     */
    @Excel(name = "定责时间", exportFormat = "yyyy-MM-dd HH:mm:SS")
    private Date blameTime;

    /**
     * 审核备注
     */
    @Excel(name = "审核备注")
    private String reviewRemark;
}
