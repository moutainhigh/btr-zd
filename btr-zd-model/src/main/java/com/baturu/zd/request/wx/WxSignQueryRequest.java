package com.baturu.zd.request.wx;

import com.baturu.zd.request.business.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * created by ketao by 2019/02/27
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WxSignQueryRequest extends BaseRequest implements Serializable {

    private Set<Integer> ids;

    /**
     * 微信openId
     */
    private String openId;

    /**
     * 手机号/微信账号
     */
    private String signPhone;

    /**
     * 密码
     */
    private String password;


    /**
     * 母账号id
     */
    private Integer ownerId;

}
