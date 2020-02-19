package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CaiZhuliang
 * @since 2019-10-17
 */
public enum BatchGatheringStatusEnum {

    /**
     * zd_batch_gathering  status
     */
    UNPAY(0, "未付款"),
    PAID(1, "已付款"),
    PAID_FAILED(2, "支付失败");

    private static Map<Integer, BatchGatheringStatusEnum> statusEnumMap = new HashMap<>();
    static {
        for (BatchGatheringStatusEnum statusEnum : BatchGatheringStatusEnum.values()) {
            statusEnumMap.put(statusEnum.type, statusEnum);
        }
    }

    @Getter
    private Integer type;

    @Getter
    private String desc;

    BatchGatheringStatusEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static BatchGatheringStatusEnum getEnum(int type) {
        BatchGatheringStatusEnum result = statusEnumMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }

}
