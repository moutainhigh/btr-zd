package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create by pengdi in 2019/3/22
 * 包裹轨迹信息entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("zd_package_imprint")
public class PackageImprintEntity extends AbstractBaseEntity{

    /**
     * 运单号
     */
    @TableField("transport_order_no")
    private String transportOrderNo;

    /**
     * 逐道包裹号
     */
    @TableField("package_no")
    private String packageNo;

    /**
     * 操作类型.0:预约下单 1:开单 2:收货 3:发货 4:验收（合伙人收货）5：配送 6：收款 7:拒收
     */
    @TableField("operate_type")
    private Integer operateType;

    /**
     * 操作说明
     */
    @TableField("remark")
    private String remark;

    /**
     * 当前位置
     */
    @TableField("location")
    private String location;

    /**
     * 当前仓位(当前位置在仓库时有值)
     */
    @TableField("position")
    private String position;

    /**
     * 当前仓位id(当前位置在仓库时有值)
     */
    @TableField("position_id")
    private Integer positionId;

    /**
     * 操作人
     */
    @TableField("operator")
    private String operator;

    /**
     * 当前位置id
     */
    @TableField("location_id")
    private Integer locationId;

}
