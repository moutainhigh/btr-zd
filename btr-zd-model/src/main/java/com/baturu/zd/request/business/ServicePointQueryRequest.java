package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * create by laijinjie in 2019/3/21
 * 业务网点业务查询request 对前端
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePointQueryRequest extends BaseRequest implements Serializable {

    /**
     * 网点id
     */
    private Set<Integer> ids;

    /**
     * 网点名称
     */
    private String name;

    /**
     * 网点类型（1：收单网点；2：配送网点）
     */
    private Integer type;

    /**
     * 省份id
     */
    private Integer provinceId;

    /**
     * 城市id
     */
    private Integer cityId;

    /**
     * 县区id
     */
    private Integer countyId;

    /**
     * 城镇id
     */
    private Integer townId;
}
