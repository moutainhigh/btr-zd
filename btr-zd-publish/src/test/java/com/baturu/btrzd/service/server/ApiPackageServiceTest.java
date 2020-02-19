package com.baturu.btrzd.service.server;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.api.ApiPackageDTO;
import com.baturu.zd.enums.PackageOperateTypeEnum;
import com.baturu.zd.enums.PackageStateEnum;
import com.baturu.zd.enums.TransportOrderStateEnum;
import com.baturu.zd.service.server.ApiPackageService;
import com.baturu.zd.util.DateUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by caizhuliang on 2019/4/23.
 */
@Slf4j
public class ApiPackageServiceTest extends BaseTest {

    @Reference
    private ApiPackageService apiPackageService;

    @Test
    public void testInStore() {
        ApiPackageDTO apiPackageDTO = ApiPackageDTO.builder().transportOrderNo("W90429000002")
                .transportOrderState(TransportOrderStateEnum.TRANSPORTING.getType())
                .operateType(PackageOperateTypeEnum.RECEIVE.getType()).locationId(6).location("广州")
                .userName("单元测试").userId(2584).dateTime(DateUtil.getCurrentDate())
                .packageState(PackageStateEnum.RECEIVED.getType())
                .packageNos(Lists.newArrayList("W90429000002_01")).remark("单元测试入库操作").build();
        ResultDTO resultDTO = apiPackageService.inStore(apiPackageDTO);
        log.info("testInStore resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

}
