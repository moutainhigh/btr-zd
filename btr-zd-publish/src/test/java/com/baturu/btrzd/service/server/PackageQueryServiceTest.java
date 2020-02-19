package com.baturu.btrzd.service.server;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.service.server.PackageQueryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by caizhuliang on 2019/4/28.
 */
@Slf4j
public class PackageQueryServiceTest extends BaseTest {

    @Reference
    private PackageQueryService packageQueryService;

    @Test
    public void testQueryPackagesByTransportOrderNo() {
        ResultDTO<List<PackageDTO>> resultDTO = packageQueryService.queryPackagesByTransportOrderNo("W90426000061", null);
        log.info("testQueryPackagesByTransportOrderNo resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

}
