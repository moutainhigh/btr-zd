package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 运单状态
 * created by ketao by 2019/03/14
 **/

public enum RegionEnum {

    NORTH(1,"华北"),
    EAST_NORTH(2,"东北"),
    EAST(3,"华东"),
    SOUTH(4,"华南"),
    WEST_SOUTH(5,"西南"),
    WEST_NORTH(6,"西北"),
    CENTER(7,"华中"),
    SPECIAL(8,"特区");


    @Getter
    private Integer type;

    @Getter
    private String desc;

    private static Map<Integer, RegionEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (RegionEnum packageStateEnum : RegionEnum.values()) {
            valueMap.put(packageStateEnum.type, packageStateEnum);
            descMap.put(packageStateEnum.type, packageStateEnum.desc);
        }
    }

    RegionEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public static boolean isLegal(Integer type) {
        for(RegionEnum transportOrderEnum : RegionEnum.values()) {
            if(transportOrderEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static RegionEnum getEnum(int type) {
        RegionEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }
}
