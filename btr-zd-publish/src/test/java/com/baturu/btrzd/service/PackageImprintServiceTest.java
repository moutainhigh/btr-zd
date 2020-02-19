package com.baturu.btrzd.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.PackageImprintDTO;
import com.baturu.zd.request.server.PackageImprintBaseQueryRequest;
import com.baturu.zd.service.business.PackageImprintService;
import com.baturu.zd.service.server.PackageImprintQueryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class PackageImprintServiceTest extends BaseTest{
    @Autowired
    PackageImprintQueryService queryService;

    @Autowired
    PackageImprintService service;

    @Test
    public void m0(){
        String s = "{\"id\":2,\"createUserId\":1,\"updateUserId\":null,\"createTime\":\"2019-03-22 15:41:34\",\"updateTime\":null," +
                "\"active\":true,\"transportOrderNo\":\"1\",\"packageNo\":\"1\",\"operateType\":2,\"operator\":\"pd\",\"remark\":\"1\"}";
        PackageImprintDTO packageImprintDTO = JSONObject.parseObject(s,new TypeReference<PackageImprintDTO>(){});
        ResultDTO<PackageImprintDTO> resultDTO = service.saveOrUpdate(packageImprintDTO);
        if(resultDTO.isSuccess()){
            System.out.println(resultDTO.getModel());
        }

    }

    @Test
    public void m1(){
        ResultDTO<List<PackageImprintDTO>> resultDTO = queryService.queryByParam(PackageImprintBaseQueryRequest.builder()
        .active(Boolean.TRUE)
        .build());
        if(resultDTO.isSuccess()){
            resultDTO.getModel().stream().forEach(o -> System.out.println(o));
        }
    }

    @Test
    public void m2(){
        ResultDTO<PackageImprintDTO> resultDTO = queryService.queryById(1);
        if(resultDTO.isSuccess()){
            System.out.println(resultDTO.getModel().toString());
        }
    }
}