package com.baturu.zd.interceptor;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.controller.app.AppLoginController;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.service.common.RedisService;
import com.baturu.zd.util.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

/**
 * APP控制器除了登录控制器以外都需要校验token的AOP实现
 * @author CaiZhuliang
 * @since 2019-4-10
 */
@Aspect
@Component
@Slf4j
public class ValidateTokenAspect {

	@Autowired
	protected RedisService redisService;

	@Before("execution(* *..controller.app..*.*(..)) or execution(* *..controller.web..*.*(..))")
	public void doBeforeInServiceLayer(JoinPoint joinPoint) {
		log.debug("doBeforeInServiceLayer");
	}

	@After("execution(* *..controller.app..*.*(..)) or execution(* *..controller.web..*.*(..))")
	public void doAfterInServiceLayer(JoinPoint joinPoint) {
		log.debug("doAfterInServiceLayer");
	}

	@Around("execution(* *..controller.app..*.*(..)) or execution(* *..controller.web..*.*(..))")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		log.debug("doAround pjp = {}", pjp);
		Class<?> clazz = pjp.getTarget().getClass();
		log.debug("doAround clazz = {}", clazz);
		// 除了登录控制器不用校验token，其他都要
		if (AppLoginController.class.isAssignableFrom(clazz)) {
			return pjp.proceed();
		}
		ResultDTO validateTokenResult = validateToken();
		if (validateTokenResult.isUnSuccess()) {
			return validateTokenResult;
		}
		return pjp.proceed();
	}

	/**
	 * 验证token是否合法。合法返回true;否则返回false
	 */
	private ResultDTO validateToken() {
		log.debug("*******************************校验token********************************");
		// 获取request
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
		HttpServletRequest request = servletRequestAttributes.getRequest();
		String token = request.getHeader(AppConstant.TOKEN_HEADER);
		if (StringUtils.isBlank(token)) {
			log.info("validateToken error : 获取token失败。");
			return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_400, "非法请求");
		}
		String str = new String(Base64.getDecoder().decode(token));
		if (!str.contains(AppConstant.PARAM_SEPARATOR)) {
			log.info("validateToken error : token格式不正确。str = {}", str);
			return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_400, "非法请求");
		}
		String[] strs = str.split(AppConstant.PARAM_SEPARATOR);
		token = strs[0];
		String username = strs[1];
		String appUserLoginInfoStr = redisService.get(token);
		if (StringUtils.isBlank(appUserLoginInfoStr) || StringUtils.isBlank(username)) {
			log.info("validateToken error : 没有该token。appUserLoginInfoStr = {}, username = {}", str, username);
			return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_400, "非法请求");
		}
		AppUserLoginInfoDTO appUserLoginInfo = SerializeUtil.deserialize(appUserLoginInfoStr);
		if (null == appUserLoginInfo || !username.equals(appUserLoginInfo.getUsername())) {
			log.info("validateToken error : 反序列化失败或者获取信息不匹配。 appUserLoginInfo = {}", appUserLoginInfo);
			return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_400, "非法请求");
		}
		return ResultDTO.succeed(AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
	}

}
