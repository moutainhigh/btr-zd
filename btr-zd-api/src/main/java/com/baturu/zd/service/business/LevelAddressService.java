package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.LevelAddressConstant;
import com.baturu.zd.dto.common.CityDTO;
import com.baturu.zd.dto.common.CountyDTO;
import com.baturu.zd.dto.common.ProvinceDTO;
import com.baturu.zd.dto.common.TownDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 三级发货地址服务
 */
public interface LevelAddressService {
    /**
     * 根据Id获取省份
     * @return
     */
    ResultDTO<List<ProvinceDTO>> selectProvincesById (Set<Integer> ids);

    /**
     * 根据省份获取城市
     * @return
     */
    ResultDTO<List<CityDTO>> selectCities(Set<Integer> provinceIds);

    /**
     * 根据id获取城市
     * @return
     */
    ResultDTO<List<CityDTO>> selectCitiesById(Set<Integer> ids);

    /**
     * 根据城市获取区县
     * @return
     */
    ResultDTO<List<CountyDTO>> selectCounties(Set<Integer> cityIds);

    /**
     * 根据id获取区县
     * @return
     */
    ResultDTO<List<CountyDTO>> selectCountiesById (Set<Integer> ids);

    /**
     * 根据区县获取城镇
     * @param countyIds
     * @return
     */
    ResultDTO<List<TownDTO>> selectTowns (Set<Integer> countyIds);

    /**
     * 根据id获取城镇
     * @param ids
     * @return
     */
    ResultDTO<List<TownDTO>> selectTownsById (Set<Integer> ids);

    /**
     * 一次性获取所有地区（以children方式嵌套）
     * @return
     */
    ResultDTO<List<ProvinceDTO>> selectAll();


    /**
     * 根据三级地址id获取三级地址id，名称的map（省份Map的key：provinceMap；城市Map的key：cityMap；区Map的key：countyMap；）
     * @param provinceIds 省份id集合
     * @param cityIds 城市id集合
     * @param countyIds 区id集合
     * @see LevelAddressConstant
     * @return
     */
    ResultDTO<Map<String,Map<Integer,String>>> getLevelMap(Set<Integer> provinceIds, Set<Integer> cityIds, Set<Integer> countyIds, Set<Integer> townIds);
}
