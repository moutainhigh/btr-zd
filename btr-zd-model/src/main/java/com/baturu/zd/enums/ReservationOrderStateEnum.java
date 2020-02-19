package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 预约单状态
 * created by laijinjie by 2019/03/27
 **/

public enum ReservationOrderStateEnum {


    RESERVE(10, "已预约"),
    ORDERED(20, "已开单"),
    CANCELED(30, "已取消");


    @Getter
    private Integer type;

    @Getter
    private String desc;

    private static Map<Integer, ReservationOrderStateEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (ReservationOrderStateEnum transportOrderStateEnum : ReservationOrderStateEnum.values()) {
            valueMap.put(transportOrderStateEnum.type, transportOrderStateEnum);
            descMap.put(transportOrderStateEnum.type, transportOrderStateEnum.desc);
        }
    }

    ReservationOrderStateEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static boolean isLegal(Integer type) {
        for (ReservationOrderStateEnum transportOrderEnum : ReservationOrderStateEnum.values()) {
            if (transportOrderEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static ReservationOrderStateEnum getEnum(int type) {
        ReservationOrderStateEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }
}
