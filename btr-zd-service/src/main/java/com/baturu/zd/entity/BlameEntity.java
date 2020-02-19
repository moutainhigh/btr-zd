package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 异常定责entity
 * @author liuduanyang
 * @since 2019/5/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("zd_blame")
public class BlameEntity extends AbstractBaseEntity {

    /**
     * 异常id，zd_order_exception主键，和异常单1对1关系
     */
    @TableField("order_exception_id")
    private Integer orderExceptionId;

    /**
     * 定责对象
     */
    @TableField("blame_name")
    private String blameName;

    /**
     * 赔偿金额
     */
    @TableField("indemnity")
    private BigDecimal indemnity;

    /**
     * 定责备注
     */
    @TableField("blame_remark")
    private String blameRemark;

    /**
     * 定责状态 0：待审核 10：已审核 20：已驳回
     */
    @TableField("state")
    private Integer state;

    /**
     * 审核备注
     */
    @TableField("review_remark")
    private String reviewRemark;

    /**
     * 图片1
     */
    @TableField("icon_1")
    private String icon1;

    /**
     * 图片2
     */
    @TableField("icon_2")
    private String icon2;

    /**
     * 图片3
     */
    @TableField("icon_3")
    private String icon3;

    /**
     * 图片4
     */
    @TableField("icon_4")
    private String icon4;

    /**
     * 创建用户名
     */
    @TableField("create_user_name")
    private String createUserName;
}
