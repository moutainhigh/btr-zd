package com.baturu.btrzd.service.web;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.web.FerryFreightDTO;
import com.baturu.zd.request.business.FerryFreightQueryRequest;
import com.baturu.zd.service.business.FerryFreightService;
import com.baturu.zd.service.dto.common.PageDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class FerryFreightServiceTest extends BaseTest {

    @Autowired
    private FerryFreightService ferryFreightService;

    @Test
    public void selectForPage() {
        ResultDTO<PageDTO> resultDTO = ferryFreightService.selectForPage(FerryFreightQueryRequest.builder()
                .startTime(new Date())
                .endTime(new Date())
                .build());
        if(resultDTO.isSuccess()){
            resultDTO.getModel().getRecords().stream().forEach(o->System.out.println(o));
        }
    }

    @Test
    /**
     * 新增摆渡运费test
     */
    public void m1(){
        String s = "{    \"id\": 2," +
                "    \"servicePointId\": 1," +
                "    \"servicePointName\": null," +
                "    \"warehouseId\": 6," +
                "    \"warehouseName\": null," +
                "    \"hugeFreight\": 10," +
                "    \"bigFreight\": 9.35," +
                "    \"mediumFreight\": 5.5," +
                "    \"smallFreight\": 3.5," +
                "    \"tinyFreight\": 2," +
                "    \"createBy\": null," +
                "    \"createUserId\": 1," +
                "    \"updateUserId\": null," +
                "    \"active\": null," +
                "    \"createTime\": null," +
                "    \"updateTime\": null" +
                "}";
        FerryFreightDTO ferryFreightDTO = JSONObject.parseObject(s,new TypeReference<FerryFreightDTO>(){});
        ResultDTO<FerryFreightDTO> resultDTO = ferryFreightService.saveOrUpdate(ferryFreightDTO);
        if(resultDTO.isSuccess()){
            System.out.println(resultDTO.getModel());
        }
    }

}