package com.baturu.btrzd.mapper;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.zd.mapper.TransportOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by caizhuliang on 2019/3/29.
 */
@Slf4j
public class TransportOrderMapperTest extends BaseTest {

    @Autowired
    private TransportOrderMapper transportOrderMapper;

    @Test
    public void testGetNextId() {
        Integer id = transportOrderMapper.getNextId();
        log.info("TransportOrderMapperTest testGetNextId ------------->>>>>>  id = {}", id);
    }

    @Test
    public void testGetCurrentMaxTransportOrderNo() {
        String transportOrderNo = transportOrderMapper.getCurrentMaxTransportOrderNo();
        log.info("TransportOrderMapperTest testGetCurrentMaxTransportOrderNo ------------->>>>>>  transportOrderNo = {}", transportOrderNo);
    }

}
