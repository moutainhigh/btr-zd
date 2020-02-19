package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 运单状态
 * created by ketao by 2019/03/14
 **/

public enum PermissionTypeEnum {

    MENU(1,"菜单"),
    BUTTON(2,"按钮");


    @Getter
    private Integer type;

    @Getter
    private String desc;

    private static Map<Integer, PermissionTypeEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (PermissionTypeEnum transportOrderEnum : PermissionTypeEnum.values()) {
            valueMap.put(transportOrderEnum.type, transportOrderEnum);
            descMap.put(transportOrderEnum.type, transportOrderEnum.desc);
        }
    }

    PermissionTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public static boolean isLegal(Integer type) {
        for(PermissionTypeEnum transportOrderEnum : PermissionTypeEnum.values()) {
            if(transportOrderEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static PermissionTypeEnum getEnum(int type) {
        PermissionTypeEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }
}
