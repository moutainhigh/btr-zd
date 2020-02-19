package com.baturu.zd.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 装车单
 * @author CaiZhuliang
 * @since 2019-3-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppEntruckingDTO implements Serializable {

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 运单数
     */
    private Integer transportOrderNum;

    /**
     * 总件数
     */
    private Integer qty;

    /**
     * 总体积
     */
    private BigDecimal bulk;

    /**
     * 目的仓（仓库编码）
     */
    private String receivingWarehouseCode;

    /**
     * 运单明细
     */
    private List<AppEntruckingDetailsDTO> entruckingDetails;

    /**
     * 装车人ID
     */
    private Integer createUserId;

    /**
     * 创建人名称（定义为装车人名称）
     */
    private String createUserName;

    /**
     * 服务网点id
     */
    private Integer servicePointId;

}
