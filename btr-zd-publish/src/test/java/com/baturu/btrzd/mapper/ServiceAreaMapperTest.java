package com.baturu.btrzd.mapper;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.zd.entity.common.ServiceAreaEntity;
import com.baturu.zd.mapper.common.ServiceAreaMapper;
import com.baturu.zd.request.business.ServiceAreaQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by caizhuliang on 2019/5/16.
 */
@Slf4j
public class ServiceAreaMapperTest extends BaseTest {

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Test
    public void testQueryDefaultServiceAreasByFour() {
        List<ServiceAreaEntity> list = serviceAreaMapper.queryDefaultServiceAreasByFour(
                ServiceAreaQueryRequest.builder().provinceId(19).cityId(440100).countyId(1669).townId(440184104).build());
        log.info("testQueryDefaultServiceAreasByFour list = {}", list);
    }

}
