package com.baturu.zd.aop;

import com.alibaba.fastjson.JSONObject;
import com.baturu.zd.constant.WxSignConstant;
import com.baturu.zd.service.common.AuthenticationService;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * created by ketao by 2019/03/04
 **/
@Aspect
@Component
@Slf4j
public class WeChatAuthorizeAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AuthenticationService authenticationService;



    //=====================================业务处理 start=====================================
    @Pointcut("execution(public * com.baturu.zd.controller.wx.api..*.*(..))")
    public void verify() {
    }

    @Before("verify()")
    public void doVerify() throws RuntimeException{
        //1.初始化参数
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String accessToken =null;
        String cookies = request.getHeader("Cookie");
        if(cookies==null){
            throw new RuntimeException("loginout:未携带Cookie信息，用户信息校验失败");
        }
        List<String> cookieList = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(cookies);
        for(int i=0;i<cookieList.size();i++) {
            String cookie = cookieList.get(i);
            if (cookie.contains(WxSignConstant.WX_LOGIN_KEY)) {
                accessToken = cookie.trim().substring((cookie.indexOf("=") + 1));
            }
        }
        ValueOperations<String, String> operations =redisTemplate.opsForValue();
        Boolean isPass = Boolean.FALSE;
        JSONObject user;
        if (accessToken != null) {
            String userValue = operations.get(accessToken);
            if(StringUtils.isNotBlank(userValue)){
                isPass=Boolean.TRUE;
            }
        }
        //4.如果没有登录就报错
        if (!isPass) {
            log.info("微信用户cookie：{}",accessToken);
            throw new RuntimeException("loginout:用户信息校验失败");
        }

    }

}
