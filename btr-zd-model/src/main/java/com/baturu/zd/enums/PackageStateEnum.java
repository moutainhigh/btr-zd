package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 运单状态
 * created by ketao by 2019/03/14
 **/

public enum PackageStateEnum {

    ALL(-1,"全部"),
    ORDERED(0,"已开单"),
    LOADED(10,"已装车"),
    RECEIVED(20,"已收货"),
    DELIVERED(30,"已发货"),
    SHIPMENT(40,"已装运"),
    CHECKED(50,"已验收"),
    EXPRESSED(60,"已配送"),
    CANCELED(70,"已取消");


    @Getter
    private Integer type;

    @Getter
    private String desc;

    private static Map<Integer, PackageStateEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (PackageStateEnum packageStateEnum : PackageStateEnum.values()) {
            valueMap.put(packageStateEnum.type, packageStateEnum);
            descMap.put(packageStateEnum.type, packageStateEnum.desc);
        }
    }

    PackageStateEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public static boolean isLegal(Integer type) {
        for(PackageStateEnum transportOrderEnum : PackageStateEnum.values()) {
            if(transportOrderEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static PackageStateEnum getEnum(int type) {
        PackageStateEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }
}
