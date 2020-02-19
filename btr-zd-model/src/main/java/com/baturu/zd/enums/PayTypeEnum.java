package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 付款方式枚举
 * @author ketao
 * @since 2019-03-14
 */
public enum PayTypeEnum {

    /**
     * 付款方式
     */
    NOW(1,"现付"),
    ARRIVE(2,"到付"),
    DISCONFIG(3,"分摊");


    @Getter
    private Integer type;
    @Getter
    private String desc;

    private static Map<Integer, PayTypeEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (PayTypeEnum transportOrderEnum : PayTypeEnum.values()) {
            valueMap.put(transportOrderEnum.type, transportOrderEnum);
            descMap.put(transportOrderEnum.type, transportOrderEnum.desc);
        }
    }

    PayTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static boolean isLegal(Integer type) {
        for(PayTypeEnum transportOrderEnum : PayTypeEnum.values()) {
            if(transportOrderEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static PayTypeEnum getEnum(int type) {
        PayTypeEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }

}
