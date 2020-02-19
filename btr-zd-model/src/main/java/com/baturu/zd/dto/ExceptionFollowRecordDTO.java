package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 异常跟踪记录DTO
 * @author liuduanyang
 * @since 2019/5/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionFollowRecordDTO implements Serializable {

    private Integer id;

    /**
     * 运单异常id
     */
    private Integer orderExceptionId;

    /**
     * 备注
     */
    private String remark;

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
     * 图片list
     */
    private List<String> images;
}
