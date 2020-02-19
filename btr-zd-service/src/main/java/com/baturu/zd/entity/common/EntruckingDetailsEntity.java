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
 * created by laijinjie by 2019/03/21
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("zd_entrucking_details")
public class EntruckingDetailsEntity extends AbstractBaseEntity {

    /**
     * 装车单号
     */
    @TableField("entrucking_no")
    private String entruckingNo;
    /**
     * 运单号
     */
    @TableField("transport_order_no")
    private String transportOrderNo;
    /**
     * 包裹号
     */
    @TableField("package_no")
    private String packageNo;
    /**
     * 收货员
     */
    @TableField("receiving_guy")
    private String receivingGuy;
    /**
     * 收货时间
     */
    @TableField("receiving_time")
    private Date receivingTime;

    /**
     * 是否有效
     */
    private Boolean active;

    /**
     * 车牌号 关于装车表
     */
    @TableField(exist = false)
    private String plateNumber;

    /**
     * 装车单明细状态   1:已发车;2:已收货; 关于装车表
     */
    @TableField(exist = false)
    private Integer state;

    /**
     * 收货仓库（目的地） 关联装车表
     */
    @TableField(exist = false)
    private String receivingWarehouse;

    /**
     * 创建人名称（定义为装车人名称）
     */
    @TableField(exist = false)
    private String createUserName;

}
