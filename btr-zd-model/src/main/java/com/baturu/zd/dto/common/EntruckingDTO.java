package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * created by laijinjie by 2019/03/21
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntruckingDTO implements Serializable {

    /**
     * 装车单号
     */
    private String entruckingNo;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 运单数
     */
    private Integer transportOrderNum;
    /**
     * 装车单明细状态   1:已发车;2:已收货;
     */
    private Integer state;
    /**
     * 总件数
     */
    private Integer qty;
    /**
     * 服务网点id
     */
    private Integer servicePointId;
    /**
     * 总体积
     */
    private BigDecimal bulk;
    /**
     * 收货仓库（目的地）
     */
    private String receivingWarehouse;
    /**
     * 总费用
     */
    private BigDecimal totalPayment;
    /**
     * 目的仓（仓库编码）
     */
    private String receivingWarehouseCode;

    /**
     * 创建人名称（定义为装车人名称）
     */
    private String createUserName;

    /**
     * 创建时间（定义为装车时间）
     */
    private Date createTime;

}
