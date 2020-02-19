package com.baturu.zd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum WxAddressTypeEnum {
    /**
     * 发货地址
     */
    SENDER(1,"发货地址"),
    /**
     * 收货地址
     */
    RECIPIENT(2,"收货地址");

    @Getter
    private Integer type;

    @Getter
    private String desc;

    WxAddressTypeEnum(Integer type, String desc){
        this.type = type;
        this.desc = desc;
    }

    public static Map<Integer, String> descMap = new HashMap<>();

    static {
        for (WxAddressTypeEnum wxAddressTypeEnum : WxAddressTypeEnum.values()) {
            descMap.put(wxAddressTypeEnum.type, wxAddressTypeEnum.desc);
        }
    }

    public static boolean isLegal(Integer type){
        for(WxAddressTypeEnum wxAddressTypeEnum : WxAddressTypeEnum.values()) {
            if(wxAddressTypeEnum.type.equals(type)){
                return true;
            }
        }
        return false;
    }

}
