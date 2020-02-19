package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 比logistics-api返回的多四级地址
 * @author CaiZhuliang
 * @date 2019-5-22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZdSocialDeliverDatasDTO implements Serializable {

    /**
     * 第一个仓库名称
     */
    private String firstWarehouse;

    /**
     * 第一个仓库编码
     */
    private String firstWarehouseCode;

    /**
     * 第一个仓位名称
     */
    private String firstPosition;

    /**
     * 第一个仓位代码
     */
    private String firstPositionCode;


    /**
     * 第二个仓库名称
     */
    private String secWarehouse;


    /**
     * 第二个仓库编码
     */
    private String secWarehouseCode;

    /**
     * 第二个仓位名称
     */
    private String secPosition;


    /**
     * 第二个仓位代码
     */
    private String secPositionCode;


    /**
     * 第三个仓库名称
     */
    private String thirdWarehouse;


    /**
     * 第三个仓位名称
     */
    private String thirdPosition;


    /**
     * 第三个仓库编码
     */
    private String thirdWarehouseCode;

    /**
     * 第四个仓库名称
     */
    private String thirdPositionCode;

    /**
     * 第四个仓库编码
     */
    private String fourthWarehouse;

    /**
     * 第四个仓库编码
     */
    private String fourthWarehouseCode;

    /**
     * 第四个仓位名称
     */
    private String fourthPosition;

    /**
     * 第四个仓位代码
     */
    private String fourthPositionCode;

    /**
     * 收货点名称
     */
    private String bizName;

    /**
     * 收货点id
     */
    private Integer bizId;

    /**
     * 配送方式1:客户自提,2:配送上门
     */
    private Integer expressType;

    /**
     * 合伙人id
     */
    private Integer partnerId;

    /**
     *合伙人名称
     */
    private String partnerName;

    /**
     * 合伙人手机
     */
    private String partnerPhone;

    /**
     * 省id
     */
    private Integer provinceId;

    /**
     * 市id
     */
    private Integer cityId;

    /**
     * 区id
     */
    private Integer countyId;

    /**
     * 城镇id
     */
    private Integer townId;

}
