package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * 摆渡单entity
 * @author liuduanyang
 * @since 2019/3/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("zd_ferry_order")
public class FerryOrderEntity extends AbstractBaseEntity {

    /**
     * 摆渡单号
     */
    @TableField("ferry_no")
    private String ferryNo;

    /**
     * 总件数
     */
    @TableField("qty")
    private Integer qty;

    /**
     * 总体积
     */
    @TableField("bulk")
    private BigDecimal bulk;

    /**
     * 支付状态
     */
    @TableField("pay_state")
    private Integer payState;

    /**
     * 总费用
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 创建人
     */
    @TableField("create_user_name")
    private String createUserName;
}
