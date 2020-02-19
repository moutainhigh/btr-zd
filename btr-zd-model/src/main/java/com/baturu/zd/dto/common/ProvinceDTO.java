package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * created by ketao by 2019/03/06
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProvinceDTO extends AbstractBaseDTO implements Serializable {

    private Integer id;
    /**
     * 省份名称
     */
    private String name;


    private List<CityDTO> children;
}
