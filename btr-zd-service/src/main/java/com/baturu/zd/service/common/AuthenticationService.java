package com.baturu.zd.service.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.WxSignConstant;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.common.UserDTO;
import com.baturu.zd.dto.wx.WxSignDTO;
import com.baturu.zd.dto.wx.WxUserInfoDTO;
import com.baturu.zd.util.CheckCodeUtil;
import com.baturu.zd.util.SerializeUtil;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * created by ketao by 2019/03/07
 **/
@Service
@Slf4j
public class AuthenticationService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${zd.weChat.appId}")
    private String appId;

    @Value("${zd.weChat.appSecret}")
    private String appSecret;

    @Value("${zd.weChat.userInfoUrl}")
    private String wxUserInfoUrl;

    @Value("${zd.weChat.openIdUrl}")
    private String openIdUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    protected RedisService redisService;

    /**
     * 获取微信用户信息
     *
     * @param code
     * @return
     */
    public ResultDTO getWxUserInfo(String code) throws UnsupportedEncodingException {
        String openIdStr = restTemplate.getForObject(this.initOpenIdUrl(code), String.class);
        JSONObject openIdJson = JSONObject.parseObject(openIdStr);
        ResultDTO openIdResult = this.resultMeaningful(openIdJson);
        if (openIdResult.isUnSuccess()) {
            return openIdResult;
        }
        String openId = openIdJson.getString("openid");
        String accessToken = openIdJson.getString("access_token");
        String userInfoStr = restTemplate.getForObject(this.initWxUserInfoUrl(accessToken, openId), String.class);
        JSONObject userInfoJson = JSONObject.parseObject(new String(userInfoStr.getBytes("ISO-8859-1"), "UTF-8"));
        ResultDTO userInfoResult = this.resultMeaningful(userInfoJson);
        if (userInfoResult.isUnSuccess()) {
            return userInfoResult;
        }
        WxUserInfoDTO wxUserInfoDTO = this.initUserInfo(userInfoJson);
        //获取redis中的登录信息，未登录返回null
        WxSignDTO wxSignDTO = this.getWxSignByKey(openId);
        log.info("登录状态监控：{}，{}", openId, wxSignDTO);
        //设置登录状态
        if (wxSignDTO != null) {
            wxUserInfoDTO.setIsLogin(Boolean.TRUE);
        } else {
            wxUserInfoDTO.setIsLogin(Boolean.FALSE);
        }
        return ResultDTO.succeedWith(wxUserInfoDTO);
    }

    private WxUserInfoDTO initUserInfo(JSONObject json) {
        return WxUserInfoDTO.builder().openId(json.getString("openid"))
                .nickname(json.getString("nickname"))
                .sex(json.getString("sex"))
                .province(json.getString("province"))
                .city(json.getString("city"))
                .country(json.getString("country"))
                .headImgUrl(json.getString("headimgurl"))
                .privilege(json.getString("privilege"))
                .build();
    }

    /**
     * 初始化获取access_token和openId的url
     * code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     *
     * @param code
     * @return
     */
    private String initOpenIdUrl(String code) {
        String url = this.openIdUrl + "&appid=" + this.appId + "&secret=" + this.appSecret + "&code=" + code;
        return url;
    }

    /**
     * 初始化获取微信用户信息url
     *
     * @param accessToken
     * @param openId
     * @return
     */
    private String initWxUserInfoUrl(String accessToken, String openId) {
        String url = this.wxUserInfoUrl + "&access_token=" + accessToken + "&openid=" + openId;
        return url;
    }

    private ResultDTO resultMeaningful(JSONObject json) {
        if (json != null && StringUtils.isBlank(json.getString("errmsg"))) {
            return ResultDTO.succeed();
        }
        return ResultDTO.failed(json.getString("errmsg"));
    }

    /**
     * 根据cookie获取当前登录的微信账号
     *
     * @return
     */
    public WxSignDTO getWxSign() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String key = null;
        String cookies = request.getHeader("Cookie");
        if (cookies == null) {
            throw new RuntimeException("loginout:未携带Cookie信息，用户信息校验失败");
        }
        List<String> cookieList = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(cookies);
        for (int i = 0; i < cookieList.size(); i++) {
            String cookie = cookieList.get(i);
            if (cookie.contains(WxSignConstant.WX_LOGIN_KEY)) {
                key = cookie.trim().substring((cookie.indexOf("=") + 1));
            }
        }
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        if (key == null) {
            return WxSignDTO.builder().build();
        }
        String wxSignJson = operations.get(key);
        if (wxSignJson == null) {
            return WxSignDTO.builder().build();
        }
        return JSON.parseObject(wxSignJson, WxSignDTO.class);
    }

    /**
     * 根据微信key获取登录信息
     *
     * @param key
     * @return
     */
    public WxSignDTO getWxSignByKey(String key) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        if (key == null) {
            return WxSignDTO.builder().build();
        }
        String wxSignJson = operations.get(key);
        if (StringUtils.isBlank(wxSignJson)) {
            return null;
        }
        return JSON.parseObject(wxSignJson, WxSignDTO.class);
    }


    /**
     * 核对验证码
     *
     * @param phone 传入手机号
     * @param code  传入验证码
     * @param type  验证码类型
     * @return
     * @see WxSignConstant
     */
    public ResultDTO checkCode(String phone, String code, Integer type) {
        Map<String, String> map = CheckCodeUtil.matchCheckCodeType(type);
        if (map == null) {
            log.warn("异常验证码类型:{}:{}", phone, type);
            return ResultDTO.failed("异常验证码类型");
        }
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String checkCode = operations.get((phone + "_" + map.get(CheckCodeUtil.CHECK_CODE_SUFFIX)));
        if (StringUtils.isBlank(checkCode)) {
            return ResultDTO.failed("验证码已失效，请重新获取");
        }
        if (!code.equals(checkCode)) {
            return ResultDTO.failed("验证码错误！");
        }
        return ResultDTO.succeed();
    }

    /**
     * 获取非微信登录用户
     *
     * @return
     * @throws Exception
     */
    public UserDTO getCurrentUser() throws Exception {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader(AppConstant.TOKEN_HEADER);
            String str = new String(Base64.getDecoder().decode(token));
            String[] strs = str.split(AppConstant.PARAM_SEPARATOR);
            token = strs[0];
            String appUserLoginInfoStr = redisService.get(token);
            AppUserLoginInfoDTO appUserLoginInfo = SerializeUtil.deserialize(appUserLoginInfoStr);
            return UserDTO.builder().id(appUserLoginInfo.getUserId()).username(appUserLoginInfo.getUsername()).name(appUserLoginInfo.getName()).build();
        } catch (Exception e) {
            log.warn("当前用户获取异常", e);
            throw new Exception("当前用户获取异常");
        }

    }
}
