package com.baturu.zd.request.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * created by ketao by 2019/04/25
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartnerListBaseQueryRequest implements Serializable {

    /**
     * 仓库id
     */
    private Integer warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 收货点id
     */
    private Integer bizId;

    /**
     * 合伙人id
     */
    private Integer partnerId;

    /**
     * 发货开始时间
     */
    private String deliverStartTime;
    /**
     * 发货结束时间
     */
    private String deliverEndTime;

}
