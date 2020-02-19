package com.baturu.zd.request.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * create by pengdi in 2019/3/21
 * 运单轨迹信息基础查询request
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderImprintBaseQueryRequest implements Serializable {
    /**
     * ids
     */
    private Set<Integer> ids;

    /**
     * 预约单号
     */
    private String reservationNo;

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 运单操作类型
     * @see com.baturu.zd.enums.TransportOrderOperateTypeEnum
     */
    private Integer operateType;

    /**
     * 是否有效
     */
    private Boolean active;
}
