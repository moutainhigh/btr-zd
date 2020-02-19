package com.baturu.btrzd.service.common;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.service.common.RedisService;
import com.baturu.zd.util.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by caizhuliang on 2019/3/21.
 */
@Slf4j
public class RedisServiceTest extends BaseTest {

    @Autowired
    protected RedisService redisService;

    @Test
    public void testSet() {
        String randomCode = "btr-zd-sd-app-ad2bea09-875a-4f87-9a99-22aa0248e003";
        AppUserLoginInfoDTO appUserLoginInfoDTO = AppUserLoginInfoDTO.builder().name("张三").username("ZhangSan").build();
        redisService.set(randomCode, SerializeUtil.serialize(appUserLoginInfoDTO), 100000);
    }

    @Test
    public void testGet() {
        String str = redisService.get("btr-zd-sd-app-ad2bea09-875a-4f87-9a99-22aa0248e003");
        AppUserLoginInfoDTO infoDTO = SerializeUtil.deserialize(str);
        log.info("infoDTO = {}", infoDTO);
    }

}
