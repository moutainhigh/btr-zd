package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * created by laijinjie by 2019/03/22
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicePointDTO extends AbstractBaseDTO implements Serializable {

    private Integer id;
    /**
     * 网点编号
     */
    private String num;
    /**
     * 1收单网点，2配送网点
     */
    private Integer type;
    /**
     * 网点名称
     */
    private String name;
    /**
     * 是否配送
     */
    private Boolean needShip;
    /**
     * 是否代收  这个字段作用是代收金额是走线上还是走线下（逐道代收费用收取可配置需求）
     */
    private Boolean needCollect;
    /**
     * 仓库id
     */
    private Integer warehouseId;
    /**
     * 仓库编码
     */
    private String warehouseCode;
    /**
     * 仓库编码
     */
    private String warehouseName;
    /**
     * 区域id
     */
    private Integer regionId;
    /**
     * 联系人
     */
    private String contact;
    /**
     * 联系电话
     */
    private String contactTel;
    /**
     * 网点地址
     */
    private String address;

    /**
     * 更新人名称
     */
    private String updateUserName;

    /**
     * 服务范围数组
     */
    private List<ServiceAreaDTO> serviceAreas;
    /**
     * 覆盖范围默认地址
     */
    private ServiceAreaDTO defaultServiceArea;

    /**
     * 合伙人id
     */
//    private Long partnerId;
    private Integer partnerId;




}
