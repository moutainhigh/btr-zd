package com.baturu.zd.service.server;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.LevelAddressConstant;
import com.baturu.zd.dto.common.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 逐道四级地址对外暴露服务
 * 包括收发货地址
 */
public interface LevelAddressQueryService {
    /**
     * 根据Id获取省份
     * @return
     */
    ResultDTO<List<ProvinceDTO>> getProvince();

    /**
     * 根据省份获取城市
     * @return
     */
    ResultDTO<List<CityDTO>> getCityByProvinceId(Set<Integer> provinceIds);

    /**
     * 根据城市获取区县
     * @return
     */
    ResultDTO<List<CountyDTO>> getCountyByCityId(Set<Integer> cityIds);

    /**
     * 根据区县获取城镇
     * @return
     */
    ResultDTO<List<TownDTO>> getTownByCountyId(Set<Integer> countyIds);

    /**
     * 根据id获取城市
     * @param ids
     * @return
     */
    ResultDTO<List<CityDTO>> queryCityIdsById(Set<Integer> ids);

    /**
     * 根据id获取区县
     * @return
     */
    ResultDTO<List<CountyDTO>> queryCountiesById(Set<Integer> ids);

    /**
     * 根据id获取城镇
     * @param ids
     * @return
     */
    ResultDTO<List<TownDTO>> queryTownsById(Set<Integer> ids);

    /**
     * 一次性获取所有地区（以children方式嵌套）
     * @param type 地址类型 1：发货地址 2：正向收货地址 3：逆向收货地址
     * @return
     */
    ResultDTO<List<ProvinceDTO>> getAllAddress(Integer type);


    /**
     * 根据四级地址id获取四级地址id，名称的map（省份Map的key：provinceMap；城市Map的key：cityMap；区Map的key：countyMap；镇Map的key：townMap）
     * @param provinceIds 省份id集合
     * @param cityIds 城市id集合
     * @param countyIds 区id集合
     * @param townIds 镇id集合
     * @see LevelAddressConstant
     * @return
     */
    ResultDTO<Map<String,Map<Integer,String>>> getLevelMap(Set<Integer> provinceIds, Set<Integer> cityIds, Set<Integer> countyIds, Set<Integer> townIds);

    /**
     * 根据类型查询子地址-正向收货
     * @param type 地址类型 1：省 2：市 3：区 4：镇
     * @param parentId 父地址id，查询省时不需要传
     * @return
     */
    ResultDTO<List<LevelAddressVO>> queryReceivingAddr(Integer type,Integer parentId);

    /**
     * 根据类型查询子地址-逆向开单收货
     * @param type 地址类型 1：省 2：市 3：区 4：镇
     * @param parentId 父地址id，查询省时不需要传
     * @return
     */
    ResultDTO<List<LevelAddressVO>> queryReverseAddr(Integer type, Integer parentId);
}
