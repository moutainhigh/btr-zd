package com.baturu.btrzd.service.common;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.app.AppMenuPermissionDTO;
import com.baturu.zd.service.business.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by caizhuliang on 2019/3/21.
 */
@Slf4j
public class PermissionServiceTest extends BaseTest {

    @Autowired
    private PermissionService permissionService;

    @Test
    public void testSelectPermissionByUserId() {
        ResultDTO<List<AppMenuPermissionDTO>> resultDTO = permissionService.queryPermissionByUserId(8);
        log.info("UserServiceTest ------------------>>>>>> resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

}
