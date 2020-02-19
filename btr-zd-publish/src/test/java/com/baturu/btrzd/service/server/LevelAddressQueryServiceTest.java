package com.baturu.btrzd.service.server;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.zd.service.server.LevelAddressQueryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class LevelAddressQueryServiceTest extends BaseTest {

    @Autowired
    private LevelAddressQueryService levelAddressQueryService;

    @Test
    public void m0(){
        System.out.println(levelAddressQueryService.getAllAddress(3));
    }
}