package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 摆渡单明细DTO
 * @author liuduanyang
 * @since 2019/3/22 15:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FerryOrderDetailsDTO implements Serializable {

    private Integer id;

    /**
     * 摆渡单号
     */
    private String ferryNo;

    /**
     * 摆渡单id
     */
    private Integer ferryId;

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 总体积 单位立方米m3
     */
    private BigDecimal bulk;

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
}
