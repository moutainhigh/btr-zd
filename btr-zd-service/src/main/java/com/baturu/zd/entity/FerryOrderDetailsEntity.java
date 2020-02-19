package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 摆渡单明细entity
 * @author liuduanyang
 * @since 2019/3/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("zd_ferry_order_details")
public class FerryOrderDetailsEntity extends AbstractBaseEntity {

    /**
     * 摆渡单号
     */
    @TableField("ferry_no")
    private String ferryNo;

    /**
     * 摆渡单id
     */
    @TableField("ferry_id")
    private Integer ferryId;

    /**
     * 包裹号
     */
    @TableField("package_no")
    private String packageNo;

    /**
     * 总体积
     */
    @TableField("bulk")
    private BigDecimal bulk;

    /**
     * 创建人
     */
    @TableField("create_user_name")
    private String createUserName;
}
