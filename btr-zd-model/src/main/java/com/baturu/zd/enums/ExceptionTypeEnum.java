package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常类型枚举类
 * @author LDy
 * @since 2019/6/5
 */
public enum ExceptionTypeEnum {

    // 定责状态
    CARGO_DAMAGE(10, "货物破损"),
    LOSS(20, "丢失"),
    ACCEPTANCE_EXCEPTION(30, "验收异常"),
    DELIVERY_EXCEPTION(40, "配送异常 "),
    OTHER(50, "其他");

    @Getter
    private Integer type;

    @Getter
    private String desc;

    ExceptionTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private static Map<Integer, ExceptionTypeEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (ExceptionTypeEnum exceptionTypeEnum : ExceptionTypeEnum.values()) {
            valueMap.put(exceptionTypeEnum.type, exceptionTypeEnum);
            descMap.put(exceptionTypeEnum.type, exceptionTypeEnum.desc);
        }
    }
}
