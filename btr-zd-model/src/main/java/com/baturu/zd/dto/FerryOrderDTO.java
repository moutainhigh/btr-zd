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
 * 摆渡单DTO
 * @author liuduanyang
 * @since 2019/3/22 15:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FerryOrderDTO implements Serializable {

    private Integer id;

    /**
     * 摆渡单号
     */
    private String ferryNo;

    /**
     * 总件数
     */
    private Integer qty;

    /**
     * 总体积  单位立方米m3
     */
    private BigDecimal bulk;

    /**
     * 支付状态（0：未支付；1：已支付）
     */
    private Integer payState;

    /**
     * 总费用
     */
    private BigDecimal amount;

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
     * 摆渡单明细列表
     */
    private List<FerryOrderDetailsDTO> ferryOrderDetails;

    /**
     * 包裹号列表
     */
    private List<String> packageNoList;
}
