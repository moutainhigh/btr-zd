package com.baturu.btrzd.service.common;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.UserDTO;
import com.baturu.zd.service.business.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by caizhuliang on 2019/3/20.
 */
@Slf4j
public class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Test
    public void testSelectUserByUsername() {
        ResultDTO<UserDTO> resultDTO = userService.queryUserByUsername("caizhuliang");
        log.info("UserServiceTest ------------------>>>>>> resultDTO = {}", resultDTO);
        Assert.assertNotNull(resultDTO);
    }

}
