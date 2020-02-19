package com.baturu.zd.request.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * created by ketao by 2019/06/25
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAreaBaseQueryRequest implements Serializable {

    /**省份id**/
    private Integer provinceId;

    /**城市id**/
    private Integer cityId;

    /**区县id**/
    private Integer countyId;

    /**城镇id*/
    private Integer townId;

}
