package com.baturu.zd.dto.web.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * create by pengdi in 2019/4/9
 * 摆渡单导出对象
 */
@Data
@ExcelTarget("ferryOrderExcelDTO")
public class FerryOrderExcelDTO implements Serializable {
    /**
     * 摆渡单号
     */
    @Excel(name = "摆渡单号",width = 30.0)
    private String ferryNo;

    /**
     * 总件数
     */
    @Excel(name = "总件数")
    private Integer qty;

    /**
     * 总体积  单位立方米m3
     */
    @Excel(name = "总体积",width = 15.0)
    private BigDecimal bulk;

    /**
     * 总费用
     */
    @Excel(name = "总费用",width = 15.0)
    private BigDecimal amount;

    /**
     * 支付状态（0：未支付；1：已支付）
     */
    @Excel(name = "支付状态",width = 20.0,replace = {"未支付_0","已支付_1"})
    private Integer payState;

    /**
     * 创建人 / 揽收员
     */
    @Excel(name = "揽收员",width = 20.0)
    private String createUserName;

    /**
     *创建时间 / 揽收时间
     */
    @Excel(name = "揽收时间",width = 30.0,format = "yyyy-MM-dd HH:mm:SS")
    private Date createTime;

}
