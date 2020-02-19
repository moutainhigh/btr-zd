package com.baturu.zd.enums;

import lombok.Getter;

/**
 * 运单收款状态枚举类
 * @author CaiZhuliang
 * @since 2019-6-12
 */
public enum GatheringStatusEnum {

    /**
     * 运单收款状态
     */
    UNPAY(0, "未付款"),
    PAID(1, "已付款"),
    NOW_PREPAID(2, "现付已收款"),
    ARRIVE_PREPAID(3, "到付已收款");

    @Getter
    private Integer type;
    @Getter
    private String desc;

    GatheringStatusEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

}
