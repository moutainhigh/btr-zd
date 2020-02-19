package com.baturu.zd.entity.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baturu.zd.entity.AbstractBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/03/27
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("zd_service_area")
public class ServiceAreaEntity extends AbstractBaseEntity {

    /**服务网点（zd_service_point）id**/
    @TableField("service_point_id")
    private Integer servicePointId;
    /**省份名称**/
    @TableField("province_name")
    private String provinceName;
    /**省份id**/
    @TableField("province_id")
    private Integer provinceId;
    /**城市名称**/
    @TableField("city_name")
    private String cityName;
    /**城市id**/
    @TableField("city_id")
    private Integer cityId;
    /**区县id**/
    @TableField("county_id")
    private Integer countyId;
    /**区县名称**/
    @TableField("county_name")
    private String countyName;
    /**城镇id**/
    @TableField("town_id")
    private Integer townId;
    /**城镇名称**/
    @TableField("town_name")
    private String townName;
    /**是否是默认地址**/
    @TableField("is_default")
    private String isDefault;
}
