package com.baturu.zd.controller;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.*;
import com.baturu.zd.enums.WxAddressTypeEnum;
import com.baturu.zd.service.business.LevelAddressService;
import com.baturu.zd.service.business.ServicePointService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * create by pengdi in 2019/3/13
 * 微信三级地址 逐道四级地址查询接口
 */
@RestController
@Slf4j
@RequestMapping("/region/find")
public class LevelAddressController extends BaseController {
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

    /**
     * 查询所有省份-发货
     * @return
     */
    @RequestMapping(value = "/allProvince",method = RequestMethod.GET)
    public ResultDTO<List<ProvinceDTO>> getProvince(){
        return levelAddressService.selectProvincesById(Sets.newHashSet());
    }

    /**
     * 查询省份下城市-发货
     * @param provinceId
     * @return
     */
    @RequestMapping(value = "/cityByProvince",method = RequestMethod.GET)
    public ResultDTO<List<CityDTO>> getCityByProvinceId(Integer provinceId){
        if(provinceId == null || provinceId <= 0){
            return ResultDTO.failed("参数无效！");
        }
        return levelAddressService.selectCities(Sets.newHashSet(provinceId));
    }

    /**
     * 查询城市下区县-发货
     * @param cityId
     * @return
     */
    @RequestMapping(value = "/countyByCity",method = RequestMethod.GET)
    public ResultDTO<List<CountyDTO>> getCountyByCityId(Integer cityId){
        if(cityId == null || cityId <= 0) {
            return ResultDTO.failed("参数无效！");
        }
        return levelAddressService.selectCounties(Sets.newHashSet(cityId));
    }

    /**
     * 查询区县下城镇-发货
     * @param countyId
     * @return
     */
    @RequestMapping(value = "/townByCounty",method = RequestMethod.GET)
    public ResultDTO<List<TownDTO>> getTownByCountyId(Integer countyId){
        if(countyId == null || countyId <= 0) {
            return ResultDTO.failed("参数无效！");
        }
        return levelAddressService.selectTowns(Sets.newHashSet(countyId));
    }

    /**
     * 查询所有四级地址
     * @param type 地址类型 1：发货地址 2：收货地址
     * @return
     */
    @RequestMapping(value = "/allAddress" , method = RequestMethod.GET)
    public ResultDTO<List<ProvinceDTO>> getAllAddress(@RequestParam Integer type){
        long startTime = System.currentTimeMillis();
        ResultDTO<List<ProvinceDTO>> resultDTO;
        if(type.equals(SENDER)){
            resultDTO = levelAddressService.selectAll();
        }else if(type.equals(FORWARD_RECIPIENT)){
            resultDTO = servicePointService.queryAllServiceArea(DELIVERY);
        }else {
            resultDTO = ResultDTO.failed("四级地址信息::参数无效");
        }
        long endTime = System.currentTimeMillis();
        log.info("查询全部{}地址耗时：{}ms",WxAddressTypeEnum.descMap.get(type),Double.valueOf((endTime - startTime)));
        return resultDTO;
    }

    /**
     * 根据类型查询子地址-正向收货
     * @param type 地址类型 1：省 2：市 3：区 4：镇
     * @param parentId 父地址id，查询省时不需要传
     * @return
     */
    @RequestMapping(value = "/queryReceivingAddr",method = RequestMethod.GET)
    public ResultDTO<List<LevelAddressVO>> queryReceivingAddr(Integer type,Integer parentId){
        //查询配送网点覆盖范围
        return servicePointService.queryChildrenArea(DELIVERY,type,parentId);
    }

    /**
     * 根据类型查询子地址-逆向开单收货
     * @param type 地址类型 1：省 2：市 3：区 4：镇
     * @param parentId 父地址id，查询省时不需要传
     * @return
     */
    @RequestMapping(value = "/queryReverseAddr",method = RequestMethod.GET)
    public ResultDTO<List<LevelAddressVO>> queryReverseAddr(Integer type,Integer parentId){
        //查询开单网点覆盖范围
        return servicePointService.queryChildrenArea(COLLECTION,type,parentId);
    }

}
