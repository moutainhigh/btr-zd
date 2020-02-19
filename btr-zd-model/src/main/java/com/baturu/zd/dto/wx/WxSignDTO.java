package com.baturu.zd.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * created by ketao by 2019/02/27
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxSignDTO implements Serializable {

    private Integer id;

    /**
     * 微信账号openId
     */
    private String openId;

    /**
     * 微信名称
     */
    private String nickname;

    /**
     * 密码
     */
    private String password;

    /**
     * 原密码（用于密码修改）
     */
    private String originPassword;

    /**
     * 注册手机号
     */
    private String signPhone;


    /**
     * 是否有效
     */
    private Boolean active;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *创建人id
     */
    private Integer createUserId;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人id
     */
    private Integer updateUserId;

    /**
     * 母账号id
     */
    private Integer ownerId;

    /**
     * 验证码
     */
    private String checkCode;

    /**
     * 登录方式（1：账号密码登录；2：验证码快捷登录）
     */
    private Integer loginType;

    /**
     * 验证码类型  1：注册；2：密码修改；3：账号绑定修改；4：登录
     * @see com.baturu.zd.constant.WxSignConstant
     */
    private Integer checkCodeType;

    /**
     * 密码修改类型
     * @see com.baturu.zd.constant.WxSignConstant 密码修改类型:1:微信账号根据旧密码修改密码;2:根据微信验证码修改密码"
     */
    private Integer passwordType;

}
