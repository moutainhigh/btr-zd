package com.baturu.zd.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * created by ketao by 2019/03/08
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseWxDTO implements Serializable {

    /**
     * 微信账号openId
     */
    protected String openId;

}
