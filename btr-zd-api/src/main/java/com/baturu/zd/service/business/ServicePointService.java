package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.*;
import com.baturu.zd.request.business.ServicePointQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.List;
import java.util.Map;

/**
 * create by laijinjie in 2019/3/22
 * 业务网点 对前端服务
 */
public interface ServicePointService {

    /**
     * 查询所有网点的id和名称
     *
     * @param request
     * @return
     */
    ResultDTO<List<Map<String, Object>>> queryAllServicePoint(ServicePointQueryRequest request);

    /**
     * 分页查询网点信息
     * @param request
     * @return
     */
    ResultDTO<PageDTO> queryServicePointsInPage(ServicePointQueryRequest request);

    /**
     * 根据ID查询业务网点记录
     *
     * @param servicePointId 网点ID
     * @return
     */
    ResultDTO<ServicePointDTO> queryServicePointById(Integer servicePointId);

    /***
     * 查询除了某个开单网点的覆盖范围以外的开单网点覆盖范围
     * @param servicePointId 网点id
     * @param type 网点类型 1:收单网点 2:配送网点
     * @return ResultDTO<List<ServiceAreaDTO>>
     */
    ResultDTO<List<ServiceAreaDTO>> queryOtherServiceAreaWithoutServicePointId(Integer servicePointId, Integer type);

    /**
     * 网点保存
     * @param servicePointDTO
     * @return
     */
    ResultDTO<ServicePointDTO> saveServicePoint(ServicePointDTO servicePointDTO);

    /**
     * 根据类型查询所有网点覆盖范围
     * @param type 网点类型 1：收单网点覆盖范围 2：配送网点覆盖范围
     * @return
     */
    ResultDTO<List<ProvinceDTO>> queryAllServiceArea(Integer type);

    /**
     * 根据父级地址、网点类型、地址类型查询网点覆盖范围
     * @param servicePointType 网点类型 1：收单网点覆盖范围 2：配送网点覆盖范围
     * @param parentId 父级地址id
     * @param addressType 地址类型  1：省 2：市 3：区 4：镇
     * @return
     */
    ResultDTO<List<LevelAddressVO>> queryChildrenArea(Integer servicePointType, Integer addressType, Integer parentId);

    /**
     * 判断输入的合伙人是否存在
     * @param teamName
     * @return
     */
    ResultDTO<Boolean> existByTeamName(String teamName);

    /**
     * 根据合伙人名称获取合伙人服务范围
     * @param teamName
     * @return
     */
    ResultDTO<List<ProvinceDTO>> queryPartnerAreaByTeamName(String teamName);

    /**
     * 保存导入的配送网点
     * @param servicePointDTO
     * @return
     */
    ResultDTO importExpressServicePoint(ServicePointImportDTO servicePointDTO);

    /**
     * 获取所有配送网点名称
     */
    ResultDTO<List<ServicePointDTO>> getDeliveryServicePointNames();
}
