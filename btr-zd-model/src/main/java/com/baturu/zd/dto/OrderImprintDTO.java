package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * create by pengdi in 2019/3/21
 * 运单轨迹信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderImprintDTO implements Serializable {

    private Integer id;

    /**
     * 预约单号
     */
    private String reservationNo;

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 当前位置
     */
    private String location;

    /**
     * 当前仓位(当前位置在仓库时有值)
     */
    private String position;

    /**
     * 操作类型.0:预约下单 1:开单 2:收货 3:发货 4:验收（合伙人收货）5：配送 6：收款 7:拒收
     */
    private Integer operateType;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作说明
     */
    private String remark;
    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     *更新用户id
     */
    private Integer updateUserId;

    /**
     *是否有效；0：否，1：是
     */
    private Boolean active;

    /**
     *创建时间
     */
    private Date createTime;

    /**
     *更新时间
     */
    private Date updateTime;
}
