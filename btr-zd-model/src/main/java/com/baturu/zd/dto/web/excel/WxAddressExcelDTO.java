package com.baturu.zd.dto.web.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * create by pengdi in 2019/4/9
 * 地址簿导出对象
 */
@Data
@ExcelTarget("wxAddressExcelDTO")
public class WxAddressExcelDTO implements Serializable {
    /**
     * 微信注册手机号/客户id
     */
    @Excel(name = "客户id",width = 20.0)
    private String signPhone;

    /**
     * 类型 1：发货地址，2：收货地址
     * @see com.baturu.zd.enums.WxAddressTypeEnum
     */
    @Excel(name = "类型",width = 15.0,replace = {"发货地址_1","收货地址_2"})
    private Integer type;

    /**
     * 姓名
     */
    @Excel(name = "姓名",width = 20.0)
    private String name;

    /**
     * 联系方式
     */
    @Excel(name = "联系方式",width = 20.0)
    private String phone;

    /**
     * 客户公司
     */
    @Excel(name = "客户公司",width = 30.0)
    private String company;

    /**
     * 客户四级地址
     */
    @Excel(name = "客户省市区",width = 40.0)
    private String levelAddress;

    /**
     * 详细地址
     */
    @Excel(name = "详细地址",width = 40.0)
    private String address;

}
