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
public class CountyDTO extends AbstractBaseDTO implements Serializable {

    private Integer id;
    /**
     * 城市id
     */
    private Integer cityId;

    /**
     * 区县名称
     */
    private String name;

    /**
     * 城镇集合
     */
    private List<TownDTO> children;

}
