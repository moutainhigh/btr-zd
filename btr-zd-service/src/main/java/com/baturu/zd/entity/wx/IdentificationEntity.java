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
 * 实名认证
 * created by ketao by 2019/03/05
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("zd_wx_identification")
public class IdentificationEntity extends AbstractBaseEntity {

    /**
     * 个人姓名
     */
    @TableField("name")
    private String name;

    /**
     * 身份证号码
     */
    @TableField("id_card")
    private String idCard;

    /**
     *手机号
     */
    @TableField("phone")
    private String phone;

    /**
     *省份id
     */
    @TableField("province_id")
    private Integer provinceId;

    /**
     *城市id
     */
    @TableField("city_id")
    private Integer cityId;

    /**
     *区id
     */
    @TableField("county_id")
    private Integer countyId;

    /**
     * 城镇id
     */
    @TableField("town_id")
    private Integer townId;

    /**
     *详细地址
     */
    @TableField("address")
    private String address;

    /**
     *银行卡号
     */
    @TableField("bank_card")
    private String bankCard;


    /**
     *银行名称
     */
    @TableField("bank_name")
    private String bankName;

    /**
     *持卡人姓名
     */
    @TableField("bank_card_owner")
    private String bankCardOwner;

    /**
     *财务手机号
     */
    @TableField("financial_phone")
    private String financialPhone;

    /**
     *是否黑名单（1：是；0：否）
     */
    @TableField("black")
    private Boolean black;

    /**
     *是否月结（1：是；0：否）
     */
    @TableField("monthly_knots")
    private Boolean monthlyKnots;
}
