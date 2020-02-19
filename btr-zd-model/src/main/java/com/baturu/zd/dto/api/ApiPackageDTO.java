package com.baturu.zd.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @author CaiZhuliang
 * @since 2019-4-23
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiPackageDTO implements Serializable {

    /**
     * 操作类型 0:预约下单 1:开单 2:收货 3:发货 4:验收（合伙人收货）5：配送 6：收款 7:拒收
     */
    private Integer operateType;

    /**
     * 当前位置
     */
    private String location;
    /**
     * 当前位置id
     */
    private Integer locationId;

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 到达的运单状态
     */
    private Integer transportOrderState;

    /**
     * 需要被更改状态的包裹号集合
     */
    private Collection<String> packageNos;

    /**
     * 到达的包裹状态
     */
    private Integer packageState;

    /**
     * 操作人ID
     */
    private Integer userId;

    /**
     * 操作人名称
     */
    private String userName;

    /**
     * 操作时间
     */
    private Date dateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 仓位
     */
    private String position;
    /**
     * 仓位id
     */
    private Integer positionId;

}
