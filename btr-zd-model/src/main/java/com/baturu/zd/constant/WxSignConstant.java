package com.baturu.zd.constant;

import java.util.Set;

/**
 * created by ketao by 2019/02/27
 **/
public interface WxSignConstant {


    //=========实体对应数据库字段start========//
    String ID="id";

    /**
     * 微信key
     */
    String OPEN_ID="open_id";

    /**
     * 母账号id
     */
    String OWNER_ID="owner_id";

    /**
     * 手机号/微信账号
     */
    String SIGNPHONE="sign_phone";

    /**
     * 密码
     */
    String PASSWORD="password";

    /**
     * 是否有效
     */
    String ACTIVE="active";
    //=========实体对应数据库字段end========//


    /**
     * 逐道预约单前缀
     */
    String RESERVATION_NO_PREFIX="Y";




    /**
     * 短信验证码短信模板名称（qpu_smstemplate表的templateName）
     * 后缀：re：注册；mo：密码修改；al：账号绑定修改；lo：登录
     */
    String WX_SIGN_CHECK_CODE_RE="zd_re_checkCode";
    String WX_SIGN_CHECK_CODE_MO="zd_mo_checkCode";
    String WX_SIGN_CHECK_CODE_AL="zd_al_checkCode";
    String WX_SIGN_CHECK_CODE_LO="zd_lo_checkCode";
    /**
     * 微信子账号添加短信通知模板
     */
    String ZD_WECHAT_CHILD_ACCOUNT="zd_wechat_child_account";

    //========验证码类型start========//
    /**
     * 注册验证码
     */
    Integer REGISTER_CHECK_CODE=1;
    /**
     * redis key 值对应后缀
     */
    String REGISTER_SUFFIX="RE";

    /**
     * 密码修改验证码
     */
    Integer MODIFY_CHECK_CODE=2;
    /**
     * redis key 值对应后缀
     */
    String MODIFY_SUFFIX="MO";

    /**
     * 修改绑定验证码
     */
    Integer ALTER_CHECK_CODE=3;
    /**
     * redis key 值对应后缀
     */
    String ALTER_SUFFIX="AL";

    /**
     * 登录验证码
     */
    Integer LOGIN_CHECK_CODE=4;
    /**
     * redis key 值对应后缀
     */
    String LOGIN_SUFFIX="LO";

    //========验证码类型end========//


    /**
     * 微信登录状态校验key（获取openid的code）
     */
    String WX_LOGIN_KEY="wxLogin";

    //=========登录类型start==========//
    /**
     * 微信账号密码登录
     */
    Integer LOGIN_BY_PW=1;


    /**
     * 微信注册账号重复
     */
    Integer DUPLICATE_SIGN_PHONE=555;

    /**
     * 微信验证码登录
     */
    Integer LOGIN_BY_CHECK_CODE=2;
    //=========登录类型end==========//


    //=========密码修改类型start==========//
    /**
     * 微信账号根据旧密码修改密码
     */
    Integer UPDATE_PASSWORDE_BY_ORIGIN=1;

    /**
     * 根据微信验证码修改密码
     */
    Integer UPDATE_PASSWORDE_BY_CHECK_CODE=2;
    //=========密码修改类型end==========//

}
