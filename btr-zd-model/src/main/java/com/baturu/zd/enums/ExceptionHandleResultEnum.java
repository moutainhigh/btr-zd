package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常状态枚举类
 * @author LDy
 * @since 2019/6/5
 */
public enum ExceptionHandleResultEnum {

    // 处理结果 0：未处理 10：定责 20：退还 30：关闭
    UNHANDLE(0,"未处理"),
    BLAME(10,"定责"),
    refund(20,"退还"),
    CLOSED(30,"关闭");

    @Getter
    private Integer type;

    @Getter
    private String desc;

    ExceptionHandleResultEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private static Map<Integer, ExceptionHandleResultEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (ExceptionHandleResultEnum exceptionTypeEnum : ExceptionHandleResultEnum.values()) {
            valueMap.put(exceptionTypeEnum.type, exceptionTypeEnum);
            descMap.put(exceptionTypeEnum.type, exceptionTypeEnum.desc);
        }
    }
}
