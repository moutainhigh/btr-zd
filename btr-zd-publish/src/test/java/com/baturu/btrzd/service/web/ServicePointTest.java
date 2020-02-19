package com.baturu.btrzd.service.web;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.ServicePointDTO;
import com.baturu.zd.dto.common.ServicePointImportDTO;
import com.baturu.zd.service.business.ServicePointService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ServicePointTest extends BaseTest {

    @Autowired
    private ServicePointService servicePointService;

    @Test
    public void m(){
        ServicePointImportDTO servicePointDTO = ServicePointImportDTO.builder()
//                .originTeamName("杭州巴配达团队").address("神之国")
                .teamName("海南三亚（合伙）").contact("郭燕").warehouseId(6).regionId(4).build();
        ResultDTO resultDTO = servicePointService.importExpressServicePoint(servicePointDTO);
        return;
    }


}