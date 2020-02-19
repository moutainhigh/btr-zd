package com.baturu.zd.service.wx;

import com.alibaba.fastjson.JSONObject;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.WxSignConstant;
import com.baturu.zd.dto.wx.SignBindDTO;
import com.baturu.zd.dto.wx.WxSignDTO;
import com.baturu.zd.request.wx.WxSignQueryRequest;
import com.baturu.zd.service.business.SignBindService;
import com.baturu.zd.service.business.WxSignService;
import com.baturu.zd.service.common.AuthenticationService;
import com.baturu.zd.service.common.RedisService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * created by ketao by 2019/03/08
 **/
@Service("signBindService")
public class SignBindServiceImpl implements SignBindService {

    @Autowired
    private WxSignService wxSignService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 微信账号手机账号修改绑定旧账号确认
     * @param signBindDTO
     * @return
     */
    @Override
    public ResultDTO confimSignInfo(SignBindDTO signBindDTO){
        ResultDTO resultDTO = this.checkSignBind(signBindDTO);
        if(resultDTO.isUnSuccess()){
            return resultDTO;
        }
        WxSignDTO wxSgin = authenticationService.getWxSign();
        ResultDTO<List<WxSignDTO>> listResultDTO = wxSignService.selectByParam(WxSignQueryRequest.builder().openId(wxSgin.getOpenId())
                .signPhone(signBindDTO.getSignPhone()).build());
        if(listResultDTO.isUnSuccess()||CollectionUtils.isEmpty(listResultDTO.getModel())){
            return ResultDTO.failed("账号信息不存在");
        }
        return ResultDTO.succeedWith(Boolean.TRUE);
    }


    @Override
    public ResultDTO newSignBind(SignBindDTO signBindDTO){
        ResultDTO resultDTO = this.checkSignBind(signBindDTO);
        if(resultDTO.isUnSuccess()){
            return resultDTO;
        }
        WxSignDTO wxSign = authenticationService.getWxSign();
        ResultDTO<List<WxSignDTO>> listResultDTO = wxSignService.selectByParam(WxSignQueryRequest.builder().openId(wxSign.getOpenId()).build());
        if(listResultDTO.isUnSuccess()||CollectionUtils.isEmpty(listResultDTO.getModel())){
            return ResultDTO.failed("账号信息不存在");
        }
        WxSignDTO wxSignDTO = listResultDTO.getModel().get(0);
        wxSignDTO.setSignPhone(signBindDTO.getSignPhone());
        wxSignDTO.setUpdateUserId(wxSign.getId());
        ResultDTO updateResult = wxSignService.updateWxSign(wxSignDTO);
        if(updateResult.isSuccess()){
            ValueOperations<String,String> operation = redisTemplate.opsForValue();
            operation.set(wxSign.getOpenId(),JSONObject.toJSONString(wxSignDTO));
        }
        return updateResult;
    }

    private ResultDTO checkSignBind(SignBindDTO signBindDTO){
        if(signBindDTO==null){
            return ResultDTO.failed("账号绑定修改参数为空");
        }
        if(StringUtils.isBlank(signBindDTO.getSignPhone())){
            return ResultDTO.failed("注册手机号为空");
        }
        if(StringUtils.isBlank(signBindDTO.getCheckCode())){
            return ResultDTO.failed("验证码为空");
        }
        ResultDTO resultDTO = authenticationService.checkCode(signBindDTO.getSignPhone(), signBindDTO.getCheckCode(), WxSignConstant.ALTER_CHECK_CODE);
        return resultDTO;
    }


}
