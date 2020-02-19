package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 菜单级别
 * created by ketao by 2019/03/14
 **/

public enum ButtonEventEnum {

    ADD("add","新增"),
    EDIT("edit","编辑"),
    DELETE("delete","删除"),
    QUERY("query","查询"),
    EXPRESS_IMPORT("express_import","配送网点导入"),
    PRINT_ORDER("printWaybill","打印运单"),
    PRINT_TAG("printTag","打印标签"),
    EXPORT("export","导出");


    @Getter
    private String type;

    @Getter
    private String desc;

    private static Map<String, ButtonEventEnum> valueMap = new HashMap<>();
    public static Map<String, String> descMap = new HashMap<>();

    static {
        for (ButtonEventEnum transportOrderEnum : ButtonEventEnum.values()) {
            valueMap.put(transportOrderEnum.type, transportOrderEnum);
            descMap.put(transportOrderEnum.type, transportOrderEnum.desc);
        }
    }

    ButtonEventEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public static boolean isLegal(Integer type) {
        for(ButtonEventEnum transportOrderEnum : ButtonEventEnum.values()) {
            if(transportOrderEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static ButtonEventEnum getEnum(String type) {
        ButtonEventEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }
}
