package com.baturu.zd.entity.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baturu.zd.entity.AbstractBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/03/06
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("zd_city")
public class CityEntity extends AbstractBaseEntity {

    /**
     * 省份id
     */
    @TableField("province_id")
    private Integer provinceId;

    @TableField("name")
    private String name;

}
