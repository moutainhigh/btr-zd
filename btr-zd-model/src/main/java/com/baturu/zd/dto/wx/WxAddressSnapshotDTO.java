package com.baturu.zd.dto.wx;

import com.baturu.zd.dto.common.AbstractBaseDTO;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * create by pengdi in 2019/3/28
 * 微信地址簿快照表DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class WxAddressSnapshotDTO extends AbstractBaseDTO implements Serializable {
    private Integer id;

    /**
     * 快照来源地址簿id
     */
    private Integer sourceId;
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
     * 地址类型 1：发货地址，2：收货地址
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建用户id
     */
    private Integer createUserId;

}
