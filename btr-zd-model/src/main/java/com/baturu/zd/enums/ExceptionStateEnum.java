package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常状态枚举类
 * @author LDy
 * @since 2019/6/5
 */
public enum ExceptionStateEnum {

    // 异常状态 0: 待处理 1: 处理中 2: 已处理
    WAIT_HANDLE(0,"待处理"),
    HANDLING(1, "处理中"),
    FINISHED_HANDLE(2, "已处理");

    @Getter
    private Integer type;

    @Getter
    private String desc;

    ExceptionStateEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private static Map<Integer, ExceptionStateEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (ExceptionStateEnum exceptionTypeEnum : ExceptionStateEnum.values()) {
            valueMap.put(exceptionTypeEnum.type, exceptionTypeEnum);
            descMap.put(exceptionTypeEnum.type, exceptionTypeEnum.desc);
        }
    }
}
