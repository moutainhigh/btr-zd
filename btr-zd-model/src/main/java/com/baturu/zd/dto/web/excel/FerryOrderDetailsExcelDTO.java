package com.baturu.zd.dto.web.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * create by pengdi in 2019/4/9
 * 摆渡单明细导出对象
 */
@Data
@ExcelTarget("ferryOrderDetailsExcelDTO")
public class FerryOrderDetailsExcelDTO {
    /**
     * 摆渡单号
     */
    @Excel(name = "摆渡单号",width = 30.0)
    private String ferryNo;

    /**
     * 包裹号
     */
    @Excel(name = "包裹号",width = 30.0)
    private String packageNo;

    /**
     * 体积 单位立方米m3
     */
    @Excel(name = "体积",width = 15.0)
    private BigDecimal bulk;

    /**
     * 创建人 / 揽收员
     */
    @Excel(name = "揽收员",width = 20.0)
    private String createUserName;


    /**
     *创建时间 / 揽收时间
     */
    @Excel(name = "揽收时间",format = "yyyy-MM-dd HH:mm:SS",width = 30.0)
    private Date createTime;
}
