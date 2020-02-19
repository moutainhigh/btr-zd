package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常定责枚举类
 * @author LDy
 * @since 2019/6/4
 */
public enum CollectAmountEnum {

    // 是否代收金额
    YES(1, "代收运单"),
    NO(0, "非代收运单"),
    ALL(-1, "全部");

    @Getter
    private Integer type;

    @Getter
    private String desc;

    CollectAmountEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    private static Map<Integer, CollectAmountEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (CollectAmountEnum blameStateEnum : CollectAmountEnum.values()) {
            valueMap.put(blameStateEnum.type, blameStateEnum);
            descMap.put(blameStateEnum.type, blameStateEnum.desc);
        }
    }
}
