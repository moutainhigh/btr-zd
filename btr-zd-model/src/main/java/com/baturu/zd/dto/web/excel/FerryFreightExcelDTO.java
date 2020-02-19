package com.baturu.zd.dto.web.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * create by pengdi in 2019/4/2
 * 摆渡运费（定价）excel导出DTO
 */
@Data
@ExcelTarget("ferryFreightExcelDTO")
public class FerryFreightExcelDTO implements Serializable {

    /**
     * 始发站 网点名称
     */
    @Excel(name = "始发站",width = 20.0)
    private String servicePointName;

    /**
     * 目的地名称
     */
    @Excel(name = "目的地",width = 20.0)
    private String warehouseName;

    /**
     * 最低一票规格运费
     */
    @Excel(name = "最低一票")
    private BigDecimal tinyFreight;

    /**
     * 特大规格运费
     */
    @Excel(name = "特大")
    private BigDecimal hugeFreight;

    /**
     * 大件规格运费
     */
    @Excel(name = "大")
    private BigDecimal bigFreight;

    /**
     * 中件规格运费
     */
    @Excel(name = "中")
    private BigDecimal mediumFreight;

    /**
     * 小件规格运费
     */
    @Excel(name = "小")
    private BigDecimal smallFreight;

    /**
     * 创建人
     */
    @Excel(name = "创建人")
    private String createUserName;

    /**
     *创建时间
     */
    @Excel(name = "创建时间", exportFormat = "yyyy-MM-dd HH:mm:SS",width = 30.0)
    private Date createTime;

}
