package com.baturu.zd.request.server;

import com.baturu.zd.request.business.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * create by pengdi in 2019/3/14
 * 逐道摆渡运费服务查询参数
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FerryFreightBaseQueryRequest implements Serializable {
    /**
     * ids
     */
    private Set<Integer> ids;

    /**
     * 始发站 网点id
     */
    private Set<Integer> servicePointIds;

    /**
     * 目的地 仓库id
     */
    private Set<Integer> warehouseIds;

    /**
     * 起始时间  创建时间
     */
    private Date startTime;

    /**
     * 终止时间 创建时间
     */
    private Date endTime;

    /**
     * 是否有效
     */
    private Boolean active;

}
