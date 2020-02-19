package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 运单状态
 * created by ketao by 2019/03/14
 **/

public enum PackageOperateTypeEnum {

    RESERVATION(0,"预约下单"),
    ORDER(1,"开单"),
    RECEIVE(2,"收货"),
    DELIVER(3,"发货"),
    CHECK(4,"验收"),
    EXPRESS(5,"配送"),
    PAY(6,"收款"),
    REFUSE(7,"拒收");


    @Getter
    private Integer type;

    @Getter
    private String desc;

    private static Map<Integer, PackageOperateTypeEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (PackageOperateTypeEnum packageOperateTypeEnum : PackageOperateTypeEnum.values()) {
            valueMap.put(packageOperateTypeEnum.type, packageOperateTypeEnum);
            descMap.put(packageOperateTypeEnum.type, packageOperateTypeEnum.desc);
        }
    }

    PackageOperateTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public static boolean isLegal(Integer type) {
        for(PackageOperateTypeEnum packageOperateTypeEnum : PackageOperateTypeEnum.values()) {
            if(packageOperateTypeEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static PackageOperateTypeEnum getEnum(int type) {
        PackageOperateTypeEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }
}
