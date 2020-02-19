package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 装车单状态
 * created by ketao by 2019/03/14
 **/

public enum EntruckingStateEnum {

    DELIVERY(1,"已发车"),
    RECEIVE(2,"已收货");


    @Getter
    private Integer type;

    @Getter
    private String desc;

    private static Map<Integer, EntruckingStateEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (EntruckingStateEnum transportOrderEnum : EntruckingStateEnum.values()) {
            valueMap.put(transportOrderEnum.type, transportOrderEnum);
            descMap.put(transportOrderEnum.type, transportOrderEnum.desc);
        }
    }

    EntruckingStateEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public static boolean isLegal(Integer type) {
        for(EntruckingStateEnum transportOrderEnum : EntruckingStateEnum.values()) {
            if(transportOrderEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static EntruckingStateEnum getEnum(int type) {
        EntruckingStateEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }
}
