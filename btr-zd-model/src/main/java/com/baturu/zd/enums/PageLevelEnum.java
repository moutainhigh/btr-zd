package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 菜单级别
 * created by ketao by 2019/03/14
 **/

public enum PageLevelEnum {

    FIRST(1,"一级菜单"),
    SECOND(2,"二级菜单"),
    THIRD(3,"三级菜单");


    @Getter
    private Integer type;

    @Getter
    private String desc;

    private static Map<Integer, PageLevelEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (PageLevelEnum transportOrderEnum : PageLevelEnum.values()) {
            valueMap.put(transportOrderEnum.type, transportOrderEnum);
            descMap.put(transportOrderEnum.type, transportOrderEnum.desc);
        }
    }

    PageLevelEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public static boolean isLegal(Integer type) {
        for(PageLevelEnum transportOrderEnum : PageLevelEnum.values()) {
            if(transportOrderEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static PageLevelEnum getEnum(int type) {
        PageLevelEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }
}
