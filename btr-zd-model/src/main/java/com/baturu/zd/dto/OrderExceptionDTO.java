package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 运单异常DTO
 * @author liuduanyang
 * @since 2019/5/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderExceptionDTO implements Serializable {

    private Integer id;

    /**
     * 运单id
     */
    private Integer transportOrderId;

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 包裹id
     */
    private Integer packageId;

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 部门id
     */
    private Integer departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 状态 0: 待处理 1: 处理中 2: 已处理
     */
    private Integer state;

    /**
     * 异常类型 10：货物破损 20：丢失 30：验收异常 40：配送异常 50：其他
     */
    private Integer type;

    /**
     * 处理意愿 10：退货 20：退款 30：补发 40：换货 50：折价 60：仅报备
     */
    private Integer handleDesire;

    /**
     * 备注
     */
    private String remark;

    /**
     * 责任部门
     */
    private String blameName;

    /**
     * 图片1
     */
    private String icon1;

    /**
     * 图片2
     */
    private String icon2;

    /**
     * 图片3
     */
    private String icon3;

    /**
     * 图片4
     */
    private String icon4;

    /**
     * 处理结果 0：未处理 10：定责 20：退还 30：关闭
     */
    private Integer handleResult;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     *更新用户id
     */
    private Integer updateUserId;

    /**
     *创建时间
     */
    private Date createTime;

    /**
     *更新时间
     */
    private Date updateTime;

    /**
     *是否有效 0：否 1：是
     */
    private Boolean active;

    /**
     * 创建用户名称
     */
    private String createUserName;

    /**
     * 图片链接list
     */
    List<String> images;
}
