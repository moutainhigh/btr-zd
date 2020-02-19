package com.baturu.zd.controller;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ketao
 * @since 2019-2-27
 */
@Slf4j
public abstract class BaseController {

    private static final String UNKNOWN = "unknown";
    private static final int IP_MAX_LENGTH = 15;

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;

    @ExceptionHandler
    public ResultDTO exceptionHandler(Throwable exception){
        log.error("异常处理：",exception);
        return ResultDTO.failedWith(exception.getMessage());
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
     * @return ip
     */
    protected String getIpAddress() {
        String ip = request.getHeader("X-Forwarded-For");
        log.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip={}", ip);
        if (StringUtils.isNotBlank(ip) && ip.length() > IP_MAX_LENGTH) {
            String[] ips = ip.split(AppConstant.COMMA_SEPARATOR);
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index];
                if (!"unknown".equalsIgnoreCase(strIp)) {
                    ip = strIp;
                    return ip;
                }
            }
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            log.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip={}", ip);
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip={}", ip);
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip={}", ip);
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip={}", ip);
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            log.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip={}", ip);
        }
        return ip;
    }

}
