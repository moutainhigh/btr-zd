package com.baturu.zd.controller.app;

import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.controller.BaseController;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.service.common.RedisService;
import com.baturu.zd.util.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;

/**
 * @author CaiZhuliang
 * @since 2019-3-20
 */
@Slf4j
public abstract class AbstractAppBaseController extends BaseController {

    @Value("${zd.app.liveTime}")
    protected long liveTime;

    @Autowired
    protected RedisService redisService;

    /**
     * 获取当前用户登录信息
     */
    protected AppUserLoginInfoDTO getCurrentUserInfo() {
        String token = request.getHeader(AppConstant.TOKEN_HEADER);
        String str = new String(Base64.getDecoder().decode(token));
        token = str.split(AppConstant.PARAM_SEPARATOR)[0];
        log.info("getCurrentUserInfo token = {}", token);
        String appUserLoginInfoStr = redisService.get(token);
        log.debug("getCurrentUserInfo appUserLoginInfoStr = {}", appUserLoginInfoStr);
        return SerializeUtil.deserialize(appUserLoginInfoStr);
    }

}
