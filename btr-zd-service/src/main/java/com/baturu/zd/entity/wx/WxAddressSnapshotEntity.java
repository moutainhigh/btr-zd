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
 * create by pengdi in 2019/3/28
 * 微信地址簿 快照表实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("zd_wx_address_snapshot")
public class WxAddressSnapshotEntity extends AbstractBaseEntity {

    /**
     * 快照来源地址簿id
     */
    @TableField("source_id")
    private Integer sourceId;
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
     * 省份名称
     */
    @TableField("province_name")
    private String provinceName;

    /**
     * 城市id
     */
    @TableField("city_id")
    private Integer cityId;

    /**
     * 城市名称
     */
    @TableField("city_name")
    private String cityName;

    /**
     * 区id
     */
    @TableField("county_id")
    private Integer countyId;

    /**
     * 区县名称
     */
    @TableField("county_name")
    private String countyName;

    /**
     * 城镇id
     */
    @TableField("town_id")
    private Integer townId;

    /**
     * 城镇名称
     */
    @TableField("town_name")
    private String townName;

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
