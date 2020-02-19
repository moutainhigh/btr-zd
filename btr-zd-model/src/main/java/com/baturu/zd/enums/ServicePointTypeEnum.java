package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务网点类型
 * created by ketao by 2019/03/14
 **/

public enum ServicePointTypeEnum {

    RECEIVE(1,"收单网点"),
    EXPRESS(2,"配送网点");


    @Getter
    private Integer type;

    @Getter
    private String desc;

    private static Map<Integer, ServicePointTypeEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (ServicePointTypeEnum transportOrderEnum : ServicePointTypeEnum.values()) {
            valueMap.put(transportOrderEnum.type, transportOrderEnum);
            descMap.put(transportOrderEnum.type, transportOrderEnum.desc);
        }
    }

    ServicePointTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public static boolean isLegal(Integer type) {
        for(ServicePointTypeEnum transportOrderEnum : ServicePointTypeEnum.values()) {
            if(transportOrderEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static ServicePointTypeEnum getEnum(int type) {
        ServicePointTypeEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }
}
