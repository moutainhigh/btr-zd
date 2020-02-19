package com.baturu.zd.dto.web.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * create by pengdi in 2019/4/8
 * 客户资料（注册+实名认证）Excel导出对象
 */
@Data
@ExcelTarget("identificationExcelDTO")
public class IdentificationExcelDTO implements Serializable {

    /**
     * 注册号码/客户id
     */
    @Excel(name = "客户id",width = 20.0)
    private String signPhone;

    /**
     * 客户姓名
     */
    @Excel(name = "客户姓名",width = 30.0)
    private String name;

    /**
     *客户手机
     */
    @Excel(name = "客户手机",width = 20.0)
    private String phone;

    /**
     * 客户省市区
     */
    @Excel(name = "客户省市区",width = 40.0)
    private String levelAddress;

    /**
     *详细地址
     */
    @Excel(name = "详细地址",width = 40.0)
    private String address;

    /**
     * 证件号码
     */
    @Excel(name = "证件号码",width = 20.0)
    private String idCard;

    /**
     *银行名称
     */
    @Excel(name = "银行名称",width = 20.0)
    private String bankName;

    /**
     *持卡人姓名
     */
    @Excel(name = "持卡人姓名",width = 20.0)
    private String bankCardOwner;

    /**
     *银行账号
     */
    @Excel(name = "银行账号",width = 30.0)
    private String bankCard;

    /**
     *财务手机号
     */
    @Excel(name = "财务手机号",width = 20.0)
    private String financialPhone;

    /**
     * 注册时间 取zd_wx_sign表的create_time字段
     */
    @Excel(name = "注册时间",exportFormat = "yyyy-MM-dd HH:mm:SS",width = 30.0)
    private Date signTime;

    /**
     * 是否黑名单（1：是；0：否）
     */
    @Excel(name = "黑名单",replace = {"否_false","是_true","_null"})
    private Boolean black;

    /**
     * 是否月结（1：是；0：否）
     */
    @Excel(name = "月结",replace = {"否_false","是_true","_null"})
    private Boolean monthlyKnots;

}
