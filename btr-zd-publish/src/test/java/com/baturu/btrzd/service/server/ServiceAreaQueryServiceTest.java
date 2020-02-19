package com.baturu.btrzd.service.server;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.request.server.ServiceAreaBaseQueryRequest;
import com.baturu.zd.service.server.ServiceAreaQueryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * ServiceArea单元测试类
 * @author LDy
 *
 */
@Slf4j
public class ServiceAreaQueryServiceTest extends BaseTest {

    @Reference
    private ServiceAreaQueryService serviceAreaQueryService;

    @Test
    public void testQueryDefaultArea() {
        ServiceAreaBaseQueryRequest request = ServiceAreaBaseQueryRequest.builder()
                .provinceId(19)
                .cityId(440200)
                .countyId(1711)
                .townId(440203001)
                .build();
        ResultDTO resultDTO = serviceAreaQueryService.queryDefaultArea(request);
        log.info("查询结果为{}", resultDTO);
    }
}
