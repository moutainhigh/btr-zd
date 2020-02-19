package com.baturu.btrzd.service.server;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.ZdConstant;
import com.baturu.zd.dto.api.ApiTransportOrderDTO;
import com.baturu.zd.service.server.ApiTransportOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by caizhuliang on 2019/4/19.
 */
@Slf4j
public class ApiTransportOrderServiceTest extends BaseTest {

    @Reference
    private ApiTransportOrderService apiTransportOrderService;

    @Test
    public void testDispatchOrCheckTransportOrder() {
        ResultDTO resultDTO = apiTransportOrderService.dispatchOrCheckTransportOrder(
                ApiTransportOrderDTO.builder().handleNo("W90611000001_01").operateType(ZdConstant.ORDER_TYPE.EXPRESS)
                        .updateUserId("7").updateUserName("河源龙川app").location("河源龙川").build());
        log.info("testDispatchOrCheckTransportOrder resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

}
