package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * created by ketao by 2019/04/23
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryPackageDTO implements Serializable {

    private Integer id;

    /**
     *逐道包裹码
     */
    private String packageNo;

    /**
     *逐道运单号 /
     */
    private String transportOrderNo;

    /**
     *运单id
     */
    private Integer transportOrderId;

    /**
     * 操作类型
     * @see com.baturu.zd.enums.PackageOperateTypeEnum
     */
    private Integer operateType;


    /**
     * 当前位置id
     */
    private Integer locationId;


    /**
     * 当前位置
     */
    private String location;


    /**
     * '盘点状态(与盘点信息表对映)：0-未盘点,2-已盘点'
     */
    private Integer inventoriedState;

    /**
     * 收货人
     */
    private String receiveUserName;

    /**
     * 收货时间
     */
    private Date receiveTime;

    /**
     * 仓位id
     */
    private Integer positionId;

    /**
     * 仓位
     */
    private String position;

    /**
     * 第二个仓库
     */
    private String secondWarehouse;

    /**
     * 重量
     */
    private BigDecimal weight;
    /**
     * 体积
     */
    private BigDecimal bulk;

    /**
     * 总订单数
     */
    private Integer orderTotal;

    /**
     * 总体积
     */
    private BigDecimal totalBulk;

    /**
     * 总重量
     */
    private BigDecimal totalWeight;
}
