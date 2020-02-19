package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.wx.WxSignDTO;
import com.baturu.zd.request.wx.WxSignQueryRequest;

import java.util.List;

/**
 * created by ketao by 2019/02/27
 **/
public interface WxSignService {

    /**
     * 注册信息查询
      * @param wxSignQueryRequest
     * @return
     */
    ResultDTO<List<WxSignDTO>> selectByParam(WxSignQueryRequest wxSignQueryRequest);

    /**
     * 根据id查询
     */
    ResultDTO<WxSignDTO> selectById(Integer id);


    /**
     * 注册信息分页查询
     * @param wxSignQueryRequest
     * @return
     */
    ResultDTO<List<WxSignDTO>> selectInPage(WxSignQueryRequest wxSignQueryRequest);

    /**
     * 注册信息更新
     * @param wxSignDTO
     * @return
     */
    ResultDTO updateWxSign(WxSignDTO wxSignDTO);

    /**
     * 逐道微信验证码获取
     * @param phone
     * @param type 验证码类型 1：注册；2：密码修改；3：账号绑定修改；4：登录
     * @return
     */
    ResultDTO<String> getCheckCode(String phone,Integer type);


    /**
     * 逐道微信公众号用户注册
     * @param wxSignDTO
     * @return
     */
    ResultDTO sign(WxSignDTO wxSignDTO);

    /**
     * 子账号添加
     * @param wxSignDTO
     * @return
     */
    ResultDTO<WxSignDTO> saveChildAccount(WxSignDTO wxSignDTO);

    /**
     * 逐道微信公众号用户登录
     * @param wxSignDTO
     * @return
     */
    ResultDTO wxLogin(WxSignDTO wxSignDTO);

    /**
     * 密码修改 (密码修改类型不为空，微信key不为空，取值参考WxSignConstant，根据旧密码修改，新旧密码不为空；根据验证码修改，注册手机号，验证码不为空)
     * @param wxSignDTO
     * @see com.baturu.zd.constant.WxSignConstant
     * @return
     */
    ResultDTO updatePassword(WxSignDTO wxSignDTO);

    ResultDTO deleteChildAccount(Integer id);
}
