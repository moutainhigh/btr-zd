package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.wx.SignBindDTO;

/**
 * created by ketao by 2019/03/19
 **/
public interface SignBindService {

    /**
     * 微信账号手机账号修改绑定旧账号确认
     * @param signBindDTO
     * @return
     */
    ResultDTO confimSignInfo(SignBindDTO signBindDTO);

    /**
     * 新账号绑定
     * @param signBindDTO
     * @return
     */
    ResultDTO newSignBind(SignBindDTO signBindDTO);
}
