package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author CaiZhuliang
 * @date 2019-5-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceAreaQueryRequest extends BaseRequest implements Serializable {

    /**
     * 服务网点（zd_service_point）id
     **/
    private Integer servicePointId;

    /**
     * 省份名称
     **/
    private String provinceName;

    /**
     * 省份id
     **/
    private Integer provinceId;

    /**
     * 城市名称
     **/
    private String cityName;

    /**
     * 城市id
     **/
    private Integer cityId;

    /**
     * 区县id
     **/
    private Integer countyId;

    /**
     * 区县名称
     **/
    private String countyName;

    /**
     * 城镇id
     **/
    private Integer townId;

    /**
     * 城镇名称
     **/
    private String townName;

}
