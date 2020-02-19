package com.baturu.zd.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 实名认证
 * created by ketao by 2019/03/05
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdentificationDTO extends BaseWxDTO implements Serializable {

    private Integer id;

    /**
     * 个人姓名
     */
    private String name;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     *手机号
     */
    private String phone;

    /**
     *省份id
     */
    private Integer provinceId;
    /**
     * 省份名称
     */
    private String provinceName;

    /**
     *城市id
     */
    private Integer cityId;
    /**
     * 城市名称
     */
    private String cityName;

    /**
     *区id
     */
    private Integer countyId;
    /**
     * 区名称
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
     *详细地址
     */
    private String address;

    /**
     *银行卡号
     */
    private String bankCard;


    /**
     *银行名称
     */
    private String bankName;

    /**
     *持卡人姓名
     */
    private String bankCardOwner;

    /**
     *财务手机号
     */
    private String financialPhone;

    /**
     *创建时间
     */
    private Date createTime;

    /**
     *更新时间
     */
    private Date updateTime;

    /**
     *创建用户id
     */
    private Integer createUserId;

    /**
     *更新用户id
     */
    private Integer updateUserId;

    /**
     *是否有效 1：是；0：否
     */
    private Boolean active;

    /**
     * 是否黑名单（1：是；0：否）
     */
    private Boolean black;

    /**
     * 是否月结（1：是；0：否）
     */
    private Boolean monthlyKnots;

    //===========以下为联表查出来的字段============================//

    /**
     * 注册号码/绑定号码
     */
    private String signPhone;

    /**
     * 微信注册id
     */
    private Integer signId;

    /**
     * 注册时间 取zd_wx_sign表的create_time字段
     */
    private Date signTime;
}
