package com.baturu.zd.service.server;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.ServiceAreaConstant;
import com.baturu.zd.dto.common.ServiceAreaDTO;
import com.baturu.zd.entity.common.ServiceAreaEntity;
import com.baturu.zd.mapper.common.ServiceAreaMapper;
import com.baturu.zd.request.server.ServiceAreaBaseQueryRequest;
import com.baturu.zd.transform.ServiceAreaTransform;
import com.baturu.zd.util.ObjectValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * created by ketao by 2019/06/25
 **/

@Service(interfaceClass = ServiceAreaQueryService.class)
@Component("serviceAreaQueryService")
@Slf4j
public class ServiceAreaQueryServiceImpl implements ServiceAreaQueryService {

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Override
    public ResultDTO<List<ServiceAreaDTO>> queryByParam(ServiceAreaBaseQueryRequest request) {
        if (ObjectValidateUtil.isAllFieldNull(request)) {
            return ResultDTO.failed("逐道覆盖范围::参数为空");
        }
        QueryWrapper wrapper = this.buildWrapper(request);
        List<ServiceAreaEntity> list = serviceAreaMapper.selectList(wrapper);
        return ResultDTO.succeedWith(Lists2.transform(list, ServiceAreaTransform.ENTITY_TO_DTO));
    }

    private QueryWrapper buildWrapper(ServiceAreaBaseQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if (request.getProvinceId() != null && request.getProvinceId() > 0) {
            wrapper.eq(ServiceAreaConstant.PROVINCE_ID, request.getProvinceId());
        }
        if (request.getCityId() != null && request.getCityId() > 0) {
            wrapper.eq(ServiceAreaConstant.CITY_ID, request.getCityId());
        }
        if (request.getCountyId() != null && request.getCountyId() > 0) {
            wrapper.eq(ServiceAreaConstant.COUNTY_ID, request.getCountyId());
        }
        if (request.getTownId() != null && request.getTownId() > 0) {
            wrapper.eq(ServiceAreaConstant.TOWN_ID, request.getTownId());
        }
        return wrapper;
    }

    @Override
    public ResultDTO<ServiceAreaDTO> queryById(Integer id) {
        if (id == null || id == 0) {
            return ResultDTO.failed("id为空");
        }
        ServiceAreaEntity serviceAreaEntity = serviceAreaMapper.selectById(id);
        if (serviceAreaEntity == null) {
            return ResultDTO.failed("id:" + id + "对应查询结果为空");
        }
        return ResultDTO.succeedWith(ServiceAreaTransform.ENTITY_TO_DTO.apply(serviceAreaEntity));
    }

    @Override
    public ResultDTO queryDefaultArea(ServiceAreaBaseQueryRequest request) {
        if (request == null ||
                request.getProvinceId() == null ||
                request.getCityId() == null ||
                request.getCountyId() == null ||
                request.getTownId() == null) {
            log.info("默认地址查询：对应四级地址参数缺失：{}", request);
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "四级地址不能为空");
        }

        // 查询四级地址对应的ServiceArea
        ServiceAreaDTO serviceAreaDTO = serviceAreaMapper.queryDefaultArea(request);
        log.info("四级地址={}，网点服务范围={}", request, serviceAreaDTO);

        // 四级地址没有对应的网点服务范围
        if (serviceAreaDTO == null) {
            serviceAreaDTO = ServiceAreaDTO.builder()
                    .provinceId(request.getProvinceId())
                    .cityId(request.getCityId())
                    .countyId(request.getCountyId())
                    .townId(request.getTownId())
                    .isServiceArea(0)
                    .build();
            return ResultDTO.failedWith(serviceAreaDTO, "找不到四级地址对应的服务范围");
        }

        // serviceArea是默认地址
        if (ServiceAreaConstant.IS_DEFAULT.equals(serviceAreaDTO.getIsDefault())) {
            serviceAreaDTO.setIsServiceArea(ServiceAreaConstant.IS_SERVICE_AREA);
            return ResultDTO.succeedWith(serviceAreaDTO);
        }

        // serviceArea不是默认地址，则需要找到服务网点下的默认地址
        Set<Integer> serviceAreaIds = serviceAreaMapper.queryServiceAreaIdsByPointId(serviceAreaDTO.getServicePointId());
        for (Integer serviceAreaId : serviceAreaIds) {
            ServiceAreaEntity serviceAreaEntity = serviceAreaMapper.selectById(serviceAreaId);
            if (ServiceAreaConstant.IS_DEFAULT.equals(serviceAreaEntity.getIsDefault())) {
                return ResultDTO.succeedWith(ServiceAreaDTO.builder()
                                            .provinceId(serviceAreaEntity.getProvinceId())
                                            .cityId(serviceAreaEntity.getCityId())
                                            .countyId(serviceAreaEntity.getCountyId())
                                            .townId(serviceAreaEntity.getTownId())
                                            .isServiceArea(ServiceAreaConstant.IS_SERVICE_AREA)
                                            .build());
            }
        }

        serviceAreaDTO.setIsServiceArea(ServiceAreaConstant.IS_SERVICE_AREA);
        return ResultDTO.succeedWith(serviceAreaDTO);
    }
}
