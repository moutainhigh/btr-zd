package com.baturu.zd.service.server;

import com.alibaba.dubbo.config.annotation.Service;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.*;
import com.baturu.zd.enums.WxAddressTypeEnum;
import com.baturu.zd.service.business.LevelAddressService;
import com.baturu.zd.service.business.ServicePointService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * create by pengdi in 2019/3/12
 * 微信三级地址服务
 */
@Service(interfaceClass = LevelAddressQueryService.class)
@Component("levelAddressQueryService")
@Slf4j
public class LevelAddressQueryServiceImpl implements LevelAddressQueryService {

    @Autowired
    private LevelAddressService levelAddressService;
    @Autowired
    private ServicePointService servicePointService;
    /**
     * 网点类型 1：收单网点 2：配送网点
     */
    public final Integer COLLECTION  = 1;
    public final Integer DELIVERY = 2;

    /**
     * 地址类型 1：发货地址 2：正向收货地址 3:逆向收货地址
     */
    public final Integer SENDER  = 1;
    public final Integer FORWARD_RECIPIENT = 2;
    public final Integer REVERSE_RECIPIENT = 3;


    @Override
    public ResultDTO<List<ProvinceDTO>> getProvince() {
        return levelAddressService.selectProvincesById(Sets.newHashSet());
    }

    @Override
    public ResultDTO<List<CityDTO>> getCityByProvinceId(Set<Integer> provinceIds) {
        return levelAddressService.selectCities(provinceIds);
    }

    @Override
    public ResultDTO<List<CountyDTO>> getCountyByCityId(Set<Integer> cityIds) {
        return levelAddressService.selectCounties(cityIds);
    }

    @Override
    public ResultDTO<List<TownDTO>> getTownByCountyId(Set<Integer> countyIds) {
        return levelAddressService.selectTowns(countyIds);
    }

    @Override
    public ResultDTO<List<CityDTO>> queryCityIdsById(Set<Integer> ids) {
        return levelAddressService.selectCitiesById(ids);
    }

    @Override
    public ResultDTO<List<CountyDTO>> queryCountiesById(Set<Integer> ids) {
        return levelAddressService.selectCountiesById(ids);
    }

    @Override
    public ResultDTO<List<TownDTO>> queryTownsById(Set<Integer> ids) {
        return levelAddressService.selectTownsById(ids);
    }

    @Override
    public ResultDTO<List<ProvinceDTO>> getAllAddress(Integer type) {
        long startTime = System.currentTimeMillis();
        ResultDTO<List<ProvinceDTO>> resultDTO;
        if(type.equals(SENDER)){
            resultDTO = levelAddressService.selectAll();
        }else if(type.equals(FORWARD_RECIPIENT)){
            resultDTO = servicePointService.queryAllServiceArea(DELIVERY);
        }else if(type.equals(REVERSE_RECIPIENT)){
            resultDTO = servicePointService.queryAllServiceArea(COLLECTION);
        } else {
            resultDTO = ResultDTO.failed("四级地址信息::参数无效");
        }
        long endTime = System.currentTimeMillis();
        log.info("查询全部地址，类型：{}，耗时：{}ms",type,Double.valueOf((endTime - startTime)));
        return resultDTO;
    }

    @Override
    public ResultDTO<Map<String, Map<Integer, String>>> getLevelMap(Set<Integer> provinceIds, Set<Integer> cityIds, Set<Integer> countyIds, Set<Integer> townIds) {
        return levelAddressService.getLevelMap(provinceIds, cityIds, countyIds, townIds);
    }

    @Override
    public ResultDTO<List<LevelAddressVO>> queryReceivingAddr(Integer type, Integer parentId) {
        //查询配送网点覆盖范围
        return servicePointService.queryChildrenArea(DELIVERY,type,parentId);
    }

    @Override
    public ResultDTO<List<LevelAddressVO>> queryReverseAddr(Integer type, Integer parentId) {
        //查询开单网点覆盖范围
        return servicePointService.queryChildrenArea(COLLECTION,type,parentId);
    }


}
