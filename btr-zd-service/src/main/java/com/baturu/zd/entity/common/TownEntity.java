package com.baturu.zd.entity.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baturu.zd.entity.AbstractBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create by pengdi in 2019/3/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("zd_town")
public class TownEntity extends AbstractBaseEntity {
    /**
     * 省份id
     */
    @TableField("county_id")
    private Integer countyId;

    @TableField("name")
    private String name;
}
