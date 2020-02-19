package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/03/27
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("zd_transline")
public class TransLineEntity extends  AbstractBaseEntity {

    /**第一个仓位名称**/
    @TableField("transport_order_no")
    private String transportOrderNo;
    /**第一个仓位名称**/
    @TableField("first_warehouse")
    private String firstWarehouse;
    /**第一个仓库编码**/
    @TableField("first_warehouse_code")
    private String firstWarehouseCode;
    /**第一个仓位名称**/
    @TableField("first_position")
    private String firstPosition;
    /**第一个仓位编码**/
    @TableField("first_position_code")
    private String firstPositionCode;
    /**第二个仓库名称**/
    @TableField("second_warehouse")
    private String secondWarehouse;
    /**第二个仓库编码**/
    @TableField("second_warehouse_code")
    private String secondWarehouseCode;
    /**第二个仓位名称**/
    @TableField("second_position")
    private String secondPosition;
    /**第二个仓位编码**/
    @TableField("second_position_code")
    private String secondPositionCode;
    /**第三个仓库名称**/
    @TableField("third_warehouse")
    private String thirdWarehouse;
    /**第三个仓位名称**/
    @TableField("third_position")
    private String thirdPosition;
    /**第三个仓库编码**/
    @TableField("third_warehouse_code")
    private String thirdWarehouseCode;
    /**第三个仓位编码**/
    @TableField("third_position_code")
    private String thirdPositionCode;
    /**第四个仓位名称**/
    @TableField("fourth_warehouse")
    private String fourthWarehouse;
    /**第四个仓库编码**/
    @TableField("fourth_warehouse_code")
    private String fourthWarehouseCode;
    /**第四个仓位名称**/
    @TableField("fourth_position")
    private String fourthPosition;
    /**第四个仓位编码**/
    @TableField("fourth_position_code")
    private String fourthPositionCode;
    /**收货点id**/
    @TableField("biz_id")
    private Integer bizId;
    /**收货点名称**/
    @TableField("biz_name")
    private String bizName;
    /**合伙人id**/
    @TableField("partner_id")
    private Integer partnerId;
    /**合伙人名称**/
    @TableField("partner_name")
    private String partnerName;
    /**合伙人电话**/
    @TableField("partner_phone")
    private String partnerPhone;
    /**省id**/
    @TableField("province_id")
    private Integer provinceId;
    /**市id**/
    @TableField("city_id")
    private Integer cityId;
    /**区id**/
    @TableField("county_id")
    private Integer countyId;
    /**城镇id**/
    @TableField("town_id")
    private Integer townId;
}
