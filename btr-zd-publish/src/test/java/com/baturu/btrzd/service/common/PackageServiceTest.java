package com.baturu.btrzd.service.common;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.service.business.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by caizhuliang on 2019/3/27.
 */
@Slf4j
public class PackageServiceTest extends BaseTest {

    @Autowired
    private PackageService packageService;

    @Test
    public void testSelectUserByUsername() {
        ResultDTO<List<PackageDTO>> resultDTO = packageService.queryPackagesByTransportOrderNo("W20190318", AppConstant.PACKAGE.STATE_CREATED_ORDER);
        log.info("PackageServiceTest ------------------>>>>>> resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

}
