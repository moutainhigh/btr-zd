package com.baturu.btrzd.service.common;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.CityDTO;
import com.baturu.zd.dto.common.CountyDTO;
import com.baturu.zd.dto.common.ProvinceDTO;
import com.baturu.zd.service.business.LevelAddressService;
import com.google.common.collect.Sets;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class WxLevelAddressTest extends BaseTest {
    @Autowired
    private LevelAddressService levelAddressService;

    @Test
    public void m0(){
        ResultDTO<List<ProvinceDTO>> resultDTO = levelAddressService.selectProvincesById(Sets.newHashSet());
        if(resultDTO.isSuccess()){
            System.out.println(resultDTO.getModel());
        }
    }

    @Test
    public void m1(){
        Integer provinceId = 1;
        ResultDTO<List<CityDTO>> resultDTO = levelAddressService.selectCities(Sets.newHashSet(provinceId));
        if(resultDTO.isSuccess()){
            resultDTO.getModel().stream().forEach(o->System.out.println(o));
        }
    }

    @Test
    public void m2(){
        Integer cityId = 820031;
        ResultDTO<List<CountyDTO>> resultDTO = levelAddressService.selectCounties(Sets.newHashSet(cityId));
        if(resultDTO.isSuccess()){
            resultDTO.getModel().stream().forEach(o->System.out.println(o));
        }
    }

    @Test
    public void m3(){
        ResultDTO<List<ProvinceDTO>> provinceResult = levelAddressService.selectProvincesById(Sets.newHashSet());
        List<ProvinceDTO> provinceDTOS = null;
        if(provinceResult.isSuccess()){
            provinceDTOS = provinceResult.getModel();
        }
        for(ProvinceDTO provinceDTO : provinceDTOS){
            System.out.println(provinceDTO);
            System.out.println("============================================================");
            ResultDTO<List<CityDTO>> cityResult = levelAddressService.selectCities(Sets.newHashSet(provinceDTO.getId()));
            List<CityDTO> cityDTOS = Lists.newArrayList();
            if(cityResult.isSuccess()){
                cityDTOS = cityResult.getModel();
            }else {
                System.out.println("省份" + provinceDTO.getId() + " 没有城市");
            }
            for(CityDTO cityDTO : cityDTOS){
                System.out.println("      ============================================================");
                System.out.println("      "+ cityDTO);
                ResultDTO<List<CountyDTO>> countyResult = levelAddressService.selectCounties(Sets.newHashSet(cityDTO.getId()));
                List<CountyDTO> countyDTOS = Lists.newArrayList();
                if(countyResult.isSuccess()){
                    countyDTOS = countyResult.getModel();
                }else {
                    System.out.println("城市" + cityDTO.getId() + " 没有区县");
                }
                for(CountyDTO countyDTO : countyDTOS){
                    System.out.println("      " + "      " + countyDTO);
                }
                System.out.println("      ============================================================");
            }
            System.out.println("============================================================");
        }
    }

    @Test
    public void m4() {
        ResultDTO<List<ProvinceDTO>> provinceResult = levelAddressService.selectAll();
        if(provinceResult.isSuccess()){
            provinceResult.getModel().stream().forEach(o->System.out.println(o));
        }
    }

}