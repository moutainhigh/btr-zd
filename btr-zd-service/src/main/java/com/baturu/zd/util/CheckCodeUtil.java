package com.baturu.zd.util;

import com.baturu.zd.constant.WxSignConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * created by ketao by 2019/03/08
 **/
@Slf4j
public class CheckCodeUtil {

    //验证码短信模板key
    public static String CHECK_CODE_MODEL="model";
    //验证码redis key后缀
    public static String CHECK_CODE_SUFFIX="suffix";

    /**
     * 获取对应验证码短信模板，类型后缀
     * @param type 验证码类型
     * @see WxSignConstant
     * @return
     */
    public static Map<String,String> matchCheckCodeType(Integer type){
        Map<String,String> map=new HashMap<>();
        if(WxSignConstant.REGISTER_CHECK_CODE.equals(type)){
            map.put(CHECK_CODE_MODEL,WxSignConstant.WX_SIGN_CHECK_CODE_RE);
            map.put(CHECK_CODE_SUFFIX,WxSignConstant.REGISTER_SUFFIX);
            return map;
        }
        if(WxSignConstant.MODIFY_CHECK_CODE.equals(type)){
            map.put(CHECK_CODE_MODEL,WxSignConstant.WX_SIGN_CHECK_CODE_MO);
            map.put(CHECK_CODE_SUFFIX,WxSignConstant.MODIFY_SUFFIX);
            return map;
        }
        if(WxSignConstant.ALTER_CHECK_CODE.equals(type)){
            map.put(CHECK_CODE_MODEL,WxSignConstant.WX_SIGN_CHECK_CODE_AL);
            map.put(CHECK_CODE_SUFFIX,WxSignConstant.ALTER_SUFFIX);
            return map;
        }
        if(WxSignConstant.LOGIN_CHECK_CODE.equals(type)){
            map.put(CHECK_CODE_MODEL,WxSignConstant.WX_SIGN_CHECK_CODE_LO);
            map.put(CHECK_CODE_SUFFIX,WxSignConstant.LOGIN_SUFFIX);
            return map;
        }
        return null;
    }
}
