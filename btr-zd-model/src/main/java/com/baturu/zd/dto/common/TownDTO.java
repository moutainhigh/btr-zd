package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * create by pengdi in 2019/3/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TownDTO implements Serializable {

    private Integer id;
    /**
     * 区县id
     */
    private Integer countyId;

    /**
     * 城镇名称
     */
    private String name;
}
