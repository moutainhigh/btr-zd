package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.PackageImprintDTO;
import com.baturu.zd.dto.api.ApiPackageDTO;

import java.util.Collection;
import java.util.List;

/**
 * 包裹轨迹信息 biz服务
 */
public interface PackageImprintService {
    /**
     * 新增/更新包裹轨迹信息 更新需保证id不为空
     * @param packageImprintDTO
     * @return
     */
    ResultDTO<PackageImprintDTO> saveOrUpdate(PackageImprintDTO packageImprintDTO);

    /**
     * 查询运单下所有的包裹轨迹
     * @param transportOrderNo
     * @return
     */
    ResultDTO<List<PackageImprintDTO>> queryByTransportOrderNo(String transportOrderNo);

    /**
     * 创建运单和包裹的轨迹
     * @param packageDTOs 需要创建包裹轨迹的包裹号
     * @param apiPackageDTO 运单轨迹信息
     * @param isCreateTransportOrderImprint 是否创建运单轨迹
     */
    ResultDTO createOrderAndPackageImprint(List<PackageDTO> packageDTOs, ApiPackageDTO apiPackageDTO, Boolean isCreateTransportOrderImprint);

    /**
     * 根据包裹号，操作类型，当前位置ID查询包裹轨迹
     * @param packageNos
     * @param operateType
     * @param locationId
     * @return
     */
    ResultDTO<List<PackageImprintDTO>> queryPackageImprintDTO(Collection<String> packageNos, Integer operateType, Integer locationId);

}
