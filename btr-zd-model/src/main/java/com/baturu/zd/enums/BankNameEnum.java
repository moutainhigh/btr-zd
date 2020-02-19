package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 银行名称
 * created by ketao by 2019/03/14
 **/

public enum BankNameEnum {

    /**
     * 银行名称
     */
    CBC(1,"中国建设银行"),
    ICBC(2,"中国工商银行"),
    ABC(3,"中国农业银行"),
    PSBC(4,"中国邮政储蓄银行"),
    BC(5,"中国银行"),
    COMM(6,"交通银行"),
    SPABANK(7,"平安银行"),
    CMSB(8,"中国民生银行"),
    GDB(9,"广发银行"),
    PF(10,"浦发银行"),
    CEB(11,"中国光大银行"),
    CMBC(12,"招商银行"),
    BJBANK(13,"北京银行"),
    SDB(14,"深圳发展银行"),
    SHBANK(15,"上海银行");


    @Getter
    private Integer type;

    @Getter
    private String desc;

    private static Map<Integer, BankNameEnum> valueMap = new HashMap<>();
    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (BankNameEnum transportOrderEnum : BankNameEnum.values()) {
            valueMap.put(transportOrderEnum.type, transportOrderEnum);
            descMap.put(transportOrderEnum.type, transportOrderEnum.desc);
        }
    }

    BankNameEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public static boolean isLegal(Integer type) {
        for(BankNameEnum transportOrderEnum : BankNameEnum.values()) {
            if(transportOrderEnum.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static BankNameEnum getEnum(int type) {
        BankNameEnum result = valueMap.get(type);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + type);
        }
        return result;
    }
}
