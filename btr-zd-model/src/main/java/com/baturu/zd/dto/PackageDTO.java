package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * created by ketao by 2019/03/14
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageDTO implements Serializable {

    private Integer id;

    /**
     *逐道包裹码
     */
    private String packageNo;

    /**
     *运单号
     */
    private String transportOrderNo;

    /**
     *运单id
     */
    private Integer transportOrderId;

    /**
     *创建时间
     */
    private Date createTime;

    /**
     *更新时间
     */
    private Date updateTime;

    /**
     *创建用户id
     */
    private Integer createUserId;

    /**
     *更新用户id
     */
    private Integer updateUserId;

    /**
     *是否有效 0：否；1：是
     */
    private Boolean active;

    /**
     *状态
     * @see com.baturu.zd.enums.PackageStateEnum
     */
    private Integer state;

    /**
     * 费用
     */
    private BigDecimal payment;

    /**
     * 体积
     */
    private BigDecimal bulk;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 是否钉箱 0：否 1：是
     */
    private Boolean isNailBox;

    /**
     * '盘点状态(与盘点信息表对映)：0-未盘点,2-已盘点'
     */
    private Integer inventoriedState;
}
