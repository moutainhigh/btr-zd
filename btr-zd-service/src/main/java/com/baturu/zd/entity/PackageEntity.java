package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * created by ketao by 2019/03/14
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("zd_package")
public class PackageEntity extends AbstractBaseEntity {

    /**
     *逐道包裹码
     */
    @TableField("package_no")
    private String packageNo;

    /**
     *运单号
     */
    @TableField("transport_order_no")
    private String transportOrderNo;

    /**
     *运单id
     */
    @TableField("transport_order_id")
    private Integer transportOrderId;

    /**
     *状态
     * @see com.baturu.zd.enums.PackageStateEnum
     */
    @TableField("state")
    private Integer state;

    /**
     * 费用
     */
    @TableField("payment")
    private BigDecimal payment;

    /**
     * 体积
     */
    @TableField("bulk")
    private BigDecimal bulk;

    /**
     * 重量
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 是否钉箱 0：否 1：是
     */
    @TableField("nail_box")
    private Boolean isNailBox;

    /**
     * '盘点状态(与盘点信息表对映)：0-未盘点,2-已盘点'
     */
    //todo tms2.0收发货标准化 上线后该字段删除
    @TableField("inventoried_state")
    private Boolean inventoriedState;

}
