package com.baturu.zd.entity.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baturu.zd.entity.AbstractBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * created by laijinjie by 2019/03/22
 * 业务网点 实体类
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("zd_service_point")
public class ServicePointEntity extends AbstractBaseEntity {

    /**
     * 网点编号
     */
    @TableField("num")
    private String num;
    /**
     * 1收单网点，2配送网点
     */
    @TableField("type")
    private Integer type;
    /**
     * 网点名称
     */
    @TableField("name")
    private String name;
    /**
     * 是否配送
     */
    @TableField("need_ship")
    private Boolean needShip;
    /**
     * 是否代收 这个字段作用是代收金额是走线上还是走线下（逐道代收费用收取可配置需求）
     */
    @TableField("need_collect")
    private Boolean needCollect;
    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouseId;
    /**
     * 仓库编码
     */
    @TableField("warehouse_code")
    private String warehouseCode;
    /**
     * 区域id
     */
    @TableField("region_id")
    private Integer regionId;
    /**
     * 联系人
     */
    @TableField("contact")
    private String contact;
    /**
     * 联系电话
     */
    @TableField("contact_tel")
    private String contactTel;
    /**
     * 网点地址
     */
    @TableField("address")
    private String address;

    /**
     * 合伙人id
     */
    @TableField("partner_id")
    private Integer partnerId;
}
