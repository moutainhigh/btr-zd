package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * created by ketao by 2019/03/27
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAreaDTO extends AbstractBaseDTO implements Serializable {

    /**服务网点（zd_service_point）id**/
    private Integer servicePointId;
    /**省份名称**/
    private String provinceName;
    /**省份id**/
    private Integer provinceId;
    /**城市名称**/
    private String cityName;
    /**城市id**/
    private Integer cityId;
    /**区县id**/
    private Integer countyId;
    /**区县名称**/
    private String countyName;
    /**城镇id**/
    private Integer townId;
    /**城镇名称**/
    private String townName;

    /**
     * 是否是默认地址
     */
    private Integer isDefault;

    /**
     * 是否在服务范围内
     */
    private Integer isServiceArea;
}
