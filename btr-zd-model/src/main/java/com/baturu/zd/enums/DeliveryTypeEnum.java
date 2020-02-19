package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 运单状态
 * created by ketao by 2019/03/14
 **/

public enum DeliveryTypeEnum {

    HOME(2,"送货上门"),
    SELF(1,"自提");


    @Getter
    private Integer type;

    @Getter
    private String desc;

    private static Map<Integer, DeliveryTypeEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (DeliveryTypeEnum transportOrderEnum : DeliveryTypeEnum.values()) {
            valueMap.put(transportOrderEnum.type, transportOrderEnum);
            descMap.put(transportOrderEnum.type, transportOrderEnum.desc);
        }
    }

    DeliveryTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public static boolean isLegal(Integer type) {
        for(DeliveryTypeEnum transportOrderEnum : DeliveryTypeEnum.values()) {
            if(transportOrderEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static DeliveryTypeEnum getEnum(int type) {
        DeliveryTypeEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }
}
