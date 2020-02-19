package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 运单异常entity
 * @author liuduanyang
 * @since 2019/5/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("zd_order_exception")
public class OrderExceptionEntity extends AbstractBaseEntity {

    /**
     * 运单id
     */
    @TableField("transport_order_id")
    private Integer transportOrderId;

    /**
     * 运单号
     */
    @TableField("transport_order_no")
    private String transportOrderNo;

    /**
     * 包裹id
     */
    @TableField("package_id")
    private Integer packageId;

    /**
     * 包裹号
     */
    @TableField("package_no")
    private String packageNo;

    /**
     * 部门id
     */
    @TableField("department_id")
    private Integer departmentId;

    /**
     * 部门名称
     */
    @TableField("department_name")
    private String departmentName;

    /**
     * 状态
     */
    @TableField("state")
    private Integer state;

    /**
     * 异常类型 10：货物破损 20：丢失 30：验收异常 40：配送异常 50：其他
     */
    @TableField("type")
    private Integer type;

    /**
     * 处理意愿 10：退货 20：退款 30：补发 40：换货 50：折价 60：仅报备
     */
    @TableField("handle_desire")
    private Integer handleDesire;

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
     * 处理结果 0：未处理 10：定责 20：退还 30：关闭
     */
    @TableField("handle_result")
    private Integer handleResult;

    /**
     * 创建用户名
     */
    @TableField("create_user_name")
    private String createUserName;
}
