package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * create by pengdi in 2019/3/18
 * 摆渡运费 业务查询request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FerryFreightQueryRequest extends BaseRequest implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 始发站 网点id
     */
    private Integer servicePointId;

    /**
     * 目的地 仓库id
     */
    private Integer warehouseId;

    /**
     * 起始时间  创建时间
     */
    private Date startTime;

    /**
     * 终止时间 创建时间
     */
    private Date endTime;
}
