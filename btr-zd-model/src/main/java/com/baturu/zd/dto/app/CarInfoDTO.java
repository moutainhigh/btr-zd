package com.baturu.zd.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 装车车辆信息
 * @author CaiZhuliang
 * @since 2019-3-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarInfoDTO implements Serializable {

    /**
     * 车牌号
     */
    private String vehiclePlate;

    /**
     * 司机名称
     */
    private String driverName;

    /**
     * 手机号码
     */
    private String mobilePhone;

}
