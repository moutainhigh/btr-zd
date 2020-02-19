package com.baturu.zd.request.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * created by ketao by 2019/04/23
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryPackageBaseQueryRequest implements Serializable {

    /**
     * 开始时间
     */
    private String receiveTimeStart;

    /**
     * 结束时间
     */
    private String receiveTimeEnd;

    /**
     * 包裹操作类型
     * @see com.baturu.zd.enums.PackageOperateTypeEnum
     */
    private Integer operateType;

    /**
     * 仓库id
     */
    private Integer warehouseId;
    private Set<Integer> warehouseIds;


    /**
     * 当前页
     */
    private Integer current;

    /**
     * 页行数
     */
    private Integer size;


    /**
     * '盘点状态(与盘点信息表对映)：0-未盘点,2-已盘点'
     */
    private Integer inventoriedState;

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 包裹号
     */
    private String packageNo;

}
