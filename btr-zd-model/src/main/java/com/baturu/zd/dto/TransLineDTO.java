package com.baturu.zd.dto;

import com.baturu.zd.dto.common.AbstractBaseDTO;
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
public class TransLineDTO extends AbstractBaseDTO {
    /**运单号**/
    private String transportOrderNo;
    /**第一个仓位名称**/
    private String firstWarehouse;
    /**第一个仓库编码**/
    private String firstWarehouseCode;
    /**第一个仓位名称**/
    private String firstPosition;
    /**第一个仓位编码**/
    private String firstPositionCode;
    /**第二个仓库名称**/
    private String secondWarehouse;
    /**第二个仓库编码**/
    private String secondWarehouseCode;
    /**第二个仓位名称**/
    private String secondPosition;
    /**第二个仓位编码**/
    private String secondPositionCode;
    /**第三个仓库名称**/
    private String thirdWarehouse;
    /**第三个仓位名称**/
    private String thirdPosition;
    /**第三个仓库编码**/
    private String thirdWarehouseCode;
    /**第三个仓位编码**/
    private String thirdPositionCode;
    /**第四个仓位名称**/
    private String fourthWarehouse;
    /**第四个仓库编码**/
    private String fourthWarehouseCode;
    /**第四个仓位名称**/
    private String fourthPosition;
    /**第四个仓位编码**/
    private String fourthPositionCode;
    /**收货点id**/
    private Integer bizId;
    /**收货点名称**/
    private String bizName;
    /**合伙人id**/
    private Integer partnerId;
    /**合伙人名称**/
    private String partnerName;
    /**合伙人电话**/
    private String partnerPhone;
    /**省id**/
    private Integer provinceId;
    /**市id**/
    private Integer cityId;
    /**区id**/
    private Integer countyId;
    /**城镇id**/
    private Integer townId;
}
