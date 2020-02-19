package com.baturu.zd.entity.wx;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baturu.zd.entity.AbstractBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * create by pengdi in 2019/3/4
 * 微信地址簿实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("zd_wx_address")
public class WxAddressEntity extends AbstractBaseEntity {


    /**
     * 姓名
     */
    @TableField("name")
    private String name;

    /**
     * 手机
     */
    @TableField("phone")
    private String phone;

    /**
     * 省份id
     */
    @TableField("province_id")
    private Integer provinceId;

    /**
     * 城市id
     */
    @TableField("city_id")
    private Integer cityId;

    /**
     * 区id
     */
    @TableField("county_id")
    private Integer countyId;

    /**
     * 城镇id
     */
    @TableField("town_id")
    private Integer townId;

    /**
     * 详细地址
     */
    @TableField("address")
    private String address;

    /**
     * 公司名称
     */
    @TableField("company")
    private String company;

    /**
     * 是否默认地址
     */
    @TableField("default_address")
    private Boolean isDefault;



    /**
     * 地址类型；1：发货地址，2：收货地址
     */
    @TableField("type")
    private Integer type;

}
