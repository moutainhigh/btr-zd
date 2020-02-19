package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 运单状态
 * created by ketao by 2019/03/14
 **/

public enum TransportOrderStateEnum {

    All(-1,"全部"),
    ORDERED(0,"已开单"),
    TRANSPORTING(10,"运输中"),
    CHECKED(20,"已验收"),
    EXPRESSED(30,"已配送"),
    CANCELED(40,"已取消");


    @Getter
    private Integer type;

    @Getter
    private String desc;

    private static Map<Integer, TransportOrderStateEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (TransportOrderStateEnum transportOrderStateEnum : TransportOrderStateEnum.values()) {
            valueMap.put(transportOrderStateEnum.type, transportOrderStateEnum);
            descMap.put(transportOrderStateEnum.type, transportOrderStateEnum.desc);
        }
    }

    TransportOrderStateEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public static boolean isLegal(Integer type) {
        for(TransportOrderStateEnum transportOrderEnum : TransportOrderStateEnum.values()) {
            if(transportOrderEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static TransportOrderStateEnum getEnum(int type) {
        TransportOrderStateEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }
}
