package com.baturu.zd.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * create by pengdi in 2019/3/4
 * 微信地址簿DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxAddressDTO implements Serializable {
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机
     */
    private String phone;

    /**
     * 省份id
     */
    private Integer provinceId;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 城市id
     */
    private Integer cityId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 区县id
     */
    private Integer countyId;

    /**
     * 区县名称
     */
    private String countyName;

    /**
     * 城镇id
     */
    private Integer townId;

    /**
     * 城镇名称
     */
    private String townName;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 是否默认地址
     */
    private Boolean isDefault;

    /**
     * 是否有效
     */
    private Boolean active;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     * 更新用户id
     */
    private Integer updateUserId;
    /**
     * 地址类型 1：发货地址，2：收货地址
     * @see com.baturu.zd.enums.WxAddressTypeEnum
     */
    private Integer type;

    //=================================其他表字段==============================//
    /**
     * 微信注册id
     */
    private Integer signId;
    /**
     * 微信注册手机号
     */
    private String signPhone;

    //======================================================//
    private Boolean isSelf;
}
