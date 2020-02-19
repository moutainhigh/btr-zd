package com.baturu.btrzd.service.common;

import com.alibaba.fastjson.JSON;
import com.baturu.btrzd.service.BaseTest;
import com.baturu.zd.service.business.ServicePointService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ServicePointServiceTest extends BaseTest {
    @Autowired
    private ServicePointService servicePointService;

    @Test
    public void m0(){
        System.out.println(JSON.toJSONString(servicePointService.queryAllServiceArea(2)));
    }

    @Test
    public void m1(){
        System.out.println(JSON.toJSONString(servicePointService.queryChildrenArea(2,4,1682)));
    }
}