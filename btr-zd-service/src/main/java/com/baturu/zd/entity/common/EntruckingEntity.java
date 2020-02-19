package com.baturu.zd.entity.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baturu.zd.entity.AbstractBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * created by laijinjie by 2019/03/21
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("zd_entrucking")
public class EntruckingEntity extends AbstractBaseEntity {

    /**
     * 装车单号
     */
    @TableField("entrucking_no")
    private String entruckingNo;
    /**
     * 车牌号
     */
    @TableField("plate_number")
    private String plateNumber;
    /**
     * 运单数
     */
    @TableField("transport_order_num")
    private Integer transportOrderNum;
    /**
     * 装车单明细状态   1:已发车;2:已收货;
     */
    @TableField("state")
    private Integer state;
    /**
     * 总件数
     */
    @TableField("qty")
    private Integer qty;
    /**
     * 服务网点id
     */
    @TableField("service_point_id")
    private Integer servicePointId;
    /**
     * 总体积
     */
    @TableField("bulk")
    private BigDecimal bulk;
    /**
     * 收货仓库（目的地）
     */
    @TableField("receiving_warehouse")
    private String receivingWarehouse;
    /**
     * 总费用
     */
    @TableField("total_payment")
    private BigDecimal totalPayment;
    /**
     * 目的仓（仓库编码）
     */
    @TableField("receiving_warehouse_code")
    private String receivingWarehouseCode;

    /**
     * 创建人名称（定义为装车人名称）
     */
    @TableField(exist = false)
    private String createUserName;

}
