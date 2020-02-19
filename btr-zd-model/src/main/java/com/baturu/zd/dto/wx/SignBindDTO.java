package com.baturu.zd.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * created by ketao by 2019/03/08
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignBindDTO extends BaseWxDTO implements Serializable {


    /**
     * 微信注册账号
     */
    private String signPhone;

    /**
     * 验证码
     */
    private String checkCode;

    private Integer updateUserId;
}
