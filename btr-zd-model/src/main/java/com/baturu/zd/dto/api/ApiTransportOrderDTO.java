package com.baturu.zd.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author CaiZhuliang
 * @since 2019-4-17
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiTransportOrderDTO implements Serializable {

    /**
     * 合伙人APP传的操作单号。运单号和包裹号会放在一起用逗号隔开，需要做处理，可以是运单号，也可以是包裹号
     */
    private String handleNo;

    /**
     * 操作类型.0:预约下单 1:开单 2:收货 3:发货 4:验收（合伙人收货）5：配送 6：收款 7:拒收
     */
    private Integer operateType;

    /**
     * 更新用户ID
     */
    private String updateUserId;

    /**
     * 更新用户名
     */
    private String updateUserName;

    /**
     * 当前位置
     */
    private String location;

    private Integer teamId;

}
