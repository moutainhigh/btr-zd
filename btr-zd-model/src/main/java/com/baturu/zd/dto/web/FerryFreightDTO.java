package com.baturu.zd.dto.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * create by pengdi in 2019/3/14
 * 摆渡运费DTO
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FerryFreightDTO implements Serializable {

    private Integer id;

    /**
     * 始发站 网点id
     */
    private Integer servicePointId;

    /**
     * 始发站 网点名称
     */
    private String servicePointName;

    /**
     * 目的地 仓库id
     */
    private Integer warehouseId;

    /**
     * 目的地名称
     */
    private String warehouseName;

    /**
     * 特大规格运费
     */
    private BigDecimal hugeFreight;

    /**
     * 大件规格运费
     */
    private BigDecimal bigFreight;

    /**
     * 中件规格运费
     */
    private BigDecimal mediumFreight;

    /**
     * 小件规格运费
     */
    private BigDecimal smallFreight;

    /**
     * 最低一票规格运费
     */
    private BigDecimal tinyFreight;

    /**
     * 创建人
     */
    private String createUserName;

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
