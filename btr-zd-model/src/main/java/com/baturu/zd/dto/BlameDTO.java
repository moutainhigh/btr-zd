package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 异常定责记录DTO
 * @author liuduanyang
 * @since 2019/6/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlameDTO implements Serializable {

    private Integer id;

    private Integer orderExceptionId;

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 异常类型
     */
    private Integer type;

    /**
     * 定责对象
     */
    private String blameName;

    /**
     * 赔偿金额
     */
    private BigDecimal indemnity;

    /**
     * 定责备注
     */
    private String blameRemark;

    /**
     * 定责状态 0：待审核 10：已审核 20：已驳回
     */
    private Integer state;

    /**
     * 审核备注
     */
    private String reviewRemark;

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
     * 图片list
     */
    private List<String> images;

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
}
