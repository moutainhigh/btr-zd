package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 异常跟踪记录entity
 * @author liuduanyang
 * @since 2019/5/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("zd_exception_follow_record")
public class ExceptionFollowRecordEntity extends AbstractBaseEntity {

    /**
     * 运单异常id
     */
    @TableField("order_exception_id")
    private Integer orderExceptionId;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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
     * 创建人名称
     */
    @TableField("create_user_name")
    private String createUserName;
}
