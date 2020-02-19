package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * create by pengdi in 2019/4/16
 * 四级地址返回前端VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LevelAddressVO implements Serializable {
    private Integer id;
    /**
     * 省份名称
     */
    private String name;
}
