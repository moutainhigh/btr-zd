package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.common.guava2.Sets2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.LevelAddressConstant;
import com.baturu.zd.dto.common.CityDTO;
import com.baturu.zd.dto.common.CountyDTO;
import com.baturu.zd.dto.common.ProvinceDTO;
import com.baturu.zd.dto.common.TownDTO;
import com.baturu.zd.entity.common.CityEntity;
import com.baturu.zd.entity.common.CountyEntity;
import com.baturu.zd.entity.common.ProvinceEntity;
import com.baturu.zd.entity.common.TownEntity;
import com.baturu.zd.mapper.common.CityMapper;
import com.baturu.zd.mapper.common.CountyMapper;
import com.baturu.zd.mapper.common.ProvinceMapper;
import com.baturu.zd.mapper.common.TownMapper;
import com.baturu.zd.transform.common.CityTransform;
import com.baturu.zd.transform.common.CountyTransform;
import com.baturu.zd.transform.common.ProvinceTransform;
import com.baturu.zd.transform.common.TownTransform;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * create by pengdi in 2019/3/12
 * 微信三级地址服务
 */
@Component("LevelAddressService")
@Slf4j
public class LevelAddressServiceImpl implements LevelAddressService {

    @Autowired
    ProvinceMapper provinceMapper;

    @Autowired
    CityMapper cityMapper;

    @Autowired
    CountyMapper countyMapper;

    @Autowired
    TownMapper townMapper;


    @Override
    public ResultDTO<List<ProvinceDTO>> selectProvincesById(Set<Integer> ids) {
        QueryWrapper wrapper = new QueryWrapper();
        if (CollectionUtils.isNotEmpty(ids)) {
            wrapper.in(LevelAddressConstant.ID, ids);
        }
        wrapper.eq(LevelAddressConstant.ACTIVE, Boolean.TRUE);
        List<ProvinceEntity> list = provinceMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return ResultDTO.failed("查询省份信息::获取省份失败！");
        }
        return ResultDTO.successfy(Lists2.transform(list, ProvinceTransform.ENTITY_TO_DTO));
    }

    @Override
    public ResultDTO<List<CityDTO>> selectCities(Set<Integer> provinceIds) {
        if (provinceIds == null || provinceIds.size() == 0) {
            return ResultDTO.failed("查询城市信息::省份id参数无效！");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.in(LevelAddressConstant.PROVINCE_ID, provinceIds);
        wrapper.eq(LevelAddressConstant.ACTIVE, Boolean.TRUE);

        List<CityEntity> list = cityMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return ResultDTO.failed("查询城市信息::获取城市失败！");
        }
        return ResultDTO.successfy(Lists2.transform(list, CityTransform.ENTITY_TO_DTO));
    }

    @Override
    public ResultDTO<List<CityDTO>> selectCitiesById(Set<Integer> ids) {
        if (ids == null || ids.size() == 0) {
            return ResultDTO.failed("id参数为空！");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.in(LevelAddressConstant.ID, ids);
        wrapper.eq(LevelAddressConstant.ACTIVE, Boolean.TRUE);

        List<CityEntity> list = cityMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return ResultDTO.failed("查询城市信息::获取城市失败！");
        }
        return ResultDTO.successfy(Lists2.transform(list, CityTransform.ENTITY_TO_DTO));
    }


    @Override
    public ResultDTO<List<CountyDTO>> selectCounties(Set<Integer> cityIds) {
        if (cityIds == null || cityIds.size() <= 0) {
            return ResultDTO.failed("查询区县信息::城市id参数无效！");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.in(LevelAddressConstant.CITY_ID, cityIds);
        wrapper.eq(LevelAddressConstant.ACTIVE, Boolean.TRUE);

        List<CountyEntity> list = countyMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return ResultDTO.failed("查询区县信息::获取区县失败！");
        }
        return ResultDTO.successfy(Lists2.transform(list, CountyTransform.ENTITY_TO_DTO));
    }

    @Override
    public ResultDTO<List<CountyDTO>> selectCountiesById(Set<Integer> ids) {
        if (ids == null || ids.size() == 0) {
            return ResultDTO.failed("id参数为空！");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.in(LevelAddressConstant.ID, ids);
        wrapper.eq(LevelAddressConstant.ACTIVE, Boolean.TRUE);

        List<CountyEntity> list = countyMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return ResultDTO.failed("查询区县信息::获取区县失败！");
        }
        return ResultDTO.successfy(Lists2.transform(list, CountyTransform.ENTITY_TO_DTO));
    }

    @Override
    public ResultDTO<List<TownDTO>> selectTowns(Set<Integer> countyIds) {
        if (countyIds == null || countyIds.size() <= 0) {
            return ResultDTO.failed("查询城镇信息::区县id参数无效！");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.in(LevelAddressConstant.COUNTY_ID, countyIds);
        wrapper.eq(LevelAddressConstant.ACTIVE, Boolean.TRUE);

        List<TownEntity> list = townMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return ResultDTO.failed("查询城镇信息::获取城镇失败！");
        }
        return ResultDTO.successfy(Lists2.transform(list, TownTransform.ENTITY_TO_DTO));
    }

    @Override
    public ResultDTO<List<TownDTO>> selectTownsById(Set<Integer> ids) {
        if (ids == null || ids.size() == 0) {
            return ResultDTO.failed("id参数为空！");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.in(LevelAddressConstant.ID, ids);
        wrapper.eq(LevelAddressConstant.ACTIVE, Boolean.TRUE);

        List<TownEntity> list = townMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return ResultDTO.failed("查询城镇信息::获取城镇失败！");
        }
        return ResultDTO.successfy(Lists2.transform(list, TownTransform.ENTITY_TO_DTO));
    }


    @Override
    public ResultDTO<List<ProvinceDTO>> selectAll() {
        //查询省份
        ResultDTO<List<ProvinceDTO>> provinceResult = this.selectProvincesById(Sets.newHashSet());
        if (provinceResult.isUnSuccess()) {
            return ResultDTO.failed("查询四级地址信息::获取省份失败！");
        }
        List<ProvinceDTO> provinceDTOS = provinceResult.getModel();
        //查询城市
        Set<Integer> provinceIds = provinceDTOS.stream().map(ProvinceDTO::getId).collect(Collectors.toSet());
        ResultDTO<List<CityDTO>> cityResult = this.selectCities(provinceIds);
        if (cityResult.isUnSuccess()) {
            return ResultDTO.failed("查询四级地址信息::获取城市失败！");
        }
        List<CityDTO> cityDTOS = cityResult.getModel();
        //省id => 城市集合
        Map<Integer, List<CityDTO>> provinceId2CityMap = cityDTOS.stream().collect(Collectors.groupingBy(CityDTO::getProvinceId));
        //查询区县
        Set<Integer> cityIds = cityDTOS.stream().map(CityDTO::getId).collect(Collectors.toSet());
        ResultDTO<List<CountyDTO>> countyResult = this.selectCounties(cityIds);
        if (countyResult.isUnSuccess()) {
            return ResultDTO.failed("查询四级地址信息::获取区县失败！");
        }
        List<CountyDTO> countyDTOS = countyResult.getModel();
        //城市id => 区县集合
        Map<Integer, List<CountyDTO>> cityId2CountyMap = countyDTOS.stream().collect(Collectors.groupingBy(CountyDTO::getCityId));
        //城镇数量多 采用分批查询
        List<Integer> countyIds = countyDTOS.stream().map(CountyDTO::getId).distinct().collect(Collectors.toList());
        List<List<Integer>> countyIdsPartition = Lists.partition(countyIds, 800);
        List<TownDTO> townDTOS = Lists.newArrayList();
        for (List<Integer> ids : countyIdsPartition) {
            ResultDTO<List<TownDTO>> townResult = this.selectTowns(Sets.newHashSet(ids));
            if (townResult.isUnSuccess()) {
                log.info("区县:{}下城镇数据不完整", ids);
                continue;
            }
            townDTOS.addAll(townResult.getModel());
        }
        //区县id => 城镇集合
        Map<Integer, List<TownDTO>> countyId2TownMap = townDTOS.stream().collect(Collectors.groupingBy(TownDTO::getCountyId));

        //填充children
        /**
         * 数据组装格式
         * 省：{
         *     市：{
         *          区：{
         *              镇：{
         *              }
         *          }
         *     }
         * }
         */
        countyDTOS.stream().forEach(o -> o.setChildren(countyId2TownMap.getOrDefault(o.getId(), Lists.newArrayList())));
        cityDTOS.stream().forEach(o -> o.setChildren(cityId2CountyMap.getOrDefault(o.getId(), Lists.newArrayList())));
        provinceDTOS.stream().forEach(o -> o.setChildren(provinceId2CityMap.getOrDefault(o.getId(), Lists.newArrayList())));
        return ResultDTO.successfy(provinceDTOS);
    }

    @Override
    public ResultDTO<Map<String, Map<Integer, String>>> getLevelMap(Set<Integer> provinceIds, Set<Integer> cityIds, Set<Integer> countyIds, Set<Integer> townIds) {
        Map<Integer, String> provinceMap = Maps.newHashMap();
        Map<Integer, String> cityMap = Maps.newHashMap();
        Map<Integer, String> countyMap = Maps.newHashMap();
        Map<Integer, String> townMap = Maps.newHashMap();
        ResultDTO<List<ProvinceDTO>> provinceResult = this.selectProvincesById(provinceIds);
        ResultDTO<List<CityDTO>> cityResult = this.selectCitiesById(cityIds);
        ResultDTO<List<CountyDTO>> countyResult = this.selectCountiesById(countyIds);
        ResultDTO<List<TownDTO>> townResult = this.selectTownsById(townIds);
        if (provinceResult.isSuccess()) {
            provinceMap = provinceResult.getModel().stream().collect(Collectors.toMap(ProvinceDTO::getId, ProvinceDTO::getName, (p1, p2) -> p1));
        }
        if (cityResult.isSuccess()) {
            cityMap = cityResult.getModel().stream().collect(Collectors.toMap(CityDTO::getId, CityDTO::getName, (c1, c2) -> c1));
        }
        if (countyResult.isSuccess()) {
            countyMap = countyResult.getModel().stream().collect(Collectors.toMap(CountyDTO::getId, CountyDTO::getName, (c1, c2) -> c1));
        }
        if (townResult.isSuccess()) {
            townMap = townResult.getModel().stream().collect(Collectors.toMap(TownDTO::getId, TownDTO::getName, (t1, t2) -> t1));
        }
        Map<String, Map<Integer, String>> map = Maps.newHashMap();
        map.put(LevelAddressConstant.PROVINCE_MAP_KEY, provinceMap);
        map.put(LevelAddressConstant.CITY_MAP_KEY, cityMap);
        map.put(LevelAddressConstant.COUNTY_MAP_KEY, countyMap);
        map.put(LevelAddressConstant.TOWN_MAP_KEY,townMap);
        return ResultDTO.succeedWith(map);
    }


}
