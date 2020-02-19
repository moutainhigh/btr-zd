package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常定责枚举类
 * @author LDy
 * @since 2019/6/4
 */
public enum BlameStateEnum {

    // 定责状态
    WAIT_AUDIT(0, "待审核"),
    FINISH_AUDIT(10, "已审核"),
    REJECT(20, "已驳回");

    @Getter
    private Integer type;

    @Getter
    private String desc;

    BlameStateEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    private static Map<Integer, BlameStateEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (BlameStateEnum blameStateEnum : BlameStateEnum.values()) {
            valueMap.put(blameStateEnum.type, blameStateEnum);
            descMap.put(blameStateEnum.type, blameStateEnum.desc);
        }
    }
}
