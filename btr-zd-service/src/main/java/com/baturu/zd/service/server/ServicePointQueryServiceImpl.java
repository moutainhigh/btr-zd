package com.baturu.zd.service.server;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.ServicePointConstant;
import com.baturu.zd.dto.common.ServicePointDTO;
import com.baturu.zd.entity.common.ServiceAreaEntity;
import com.baturu.zd.entity.common.ServicePointEntity;
import com.baturu.zd.mapper.common.ServiceAreaMapper;
import com.baturu.zd.mapper.common.ServicePointMapper;
import com.baturu.zd.request.server.ServicePointBaseQueryRequest;
import com.baturu.zd.transform.ServiceAreaTransform;
import com.baturu.zd.transform.ServicePointTransform;
import com.baturu.zd.transform.wx.WxAddressTransform;
import com.baturu.zd.util.ObjectValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * create by pengdi in 2019/3/25
 * 逐道网点 单表查询服务
 */
@Service(interfaceClass = ServicePointQueryService.class)
@Component("servicePointQueryService")
@Slf4j
public class ServicePointQueryServiceImpl implements ServicePointQueryService {
    @Autowired
    ServicePointMapper servicePointMapper;

    @Autowired
    ServiceAreaMapper serviceAreaMapper;

    @Override
    public ResultDTO<List<ServicePointDTO>> queryByParam(ServicePointBaseQueryRequest request) {
        if(ObjectValidateUtil.isAllFieldNull(request)){
            return ResultDTO.failed("逐道网点信息::参数为空");
        }
        QueryWrapper wrapper = this.buildWrapper(request);
        List<ServicePointEntity> list = servicePointMapper.selectList(wrapper);
        if(CollectionUtils.isEmpty(list)){
            return ResultDTO.successfy(Collections.emptyList());
        }
        return ResultDTO.successfy(Lists2.transform(list, ServicePointTransform.ENTITY_TO_DTO));
    }

    @Override
    public ResultDTO<ServicePointDTO> queryById(Integer id) {
        if(id == null || id <= 0){
            return ResultDTO.failed("逐道网点信息::id为空");
        }
        ServicePointEntity entity = servicePointMapper.selectById(id);
        return ResultDTO.successfy(ServicePointTransform.ENTITY_TO_DTO.apply(entity));
    }

    private QueryWrapper buildWrapper(ServicePointBaseQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if(CollectionUtils.isNotEmpty(request.getIds())){
            wrapper.in(ServicePointConstant.ID,request.getIds());
        }
        if(request.getNum() != null && request.getNum() > 0){
            wrapper.eq(ServicePointConstant.NUM,request.getNum());
        }
        if(StringUtils.isNotBlank(request.getName())){
            wrapper.eq(ServicePointConstant.NAME,request.getName());
        }
        if(request.getType() != null && request.getType() > 0){
            wrapper.eq(ServicePointConstant.TYPE,request.getType());
        }
        if (request.getPartnerId() != null && request.getPartnerId() > 0) {
            wrapper.eq(ServicePointConstant.PARTNERID, request.getPartnerId());
        }

        if(request.getActive() != null){
            wrapper.eq(ServicePointConstant.ACTIVE,request.getActive());
        }else {
            wrapper.eq(ServicePointConstant.ACTIVE,Boolean.TRUE);
        }
        return wrapper;
    }

    @Override
    public ResultDTO<ServicePointDTO> getByPartnerId(Long partnerId) {
        ServicePointBaseQueryRequest servicePointBaseQueryRequest = ServicePointBaseQueryRequest.builder().partnerId(partnerId).build();
        ResultDTO<List<ServicePointDTO>> listResultDTO = queryByParam(servicePointBaseQueryRequest);
        if (listResultDTO.isUnSuccess()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查找不合伙人的收单网点失败!");
        }
        List<ServicePointDTO> servicePointDTOS = listResultDTO.getModel();
        if (servicePointDTOS == null || servicePointDTOS.size() <= 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查找不到合伙人的收单网点!");
        }
        ServicePointDTO servicePointDTO = servicePointDTOS.get(0);
        Set<Integer> serviceAreaIds = serviceAreaMapper.queryServiceAreaIdsByPointId(servicePointDTO.getId());
        List<ServiceAreaEntity> serviceAreaEntities = new ArrayList<>(serviceAreaIds.size());
        for (Integer serviceAreaId: serviceAreaIds) {
            serviceAreaEntities.add(serviceAreaMapper.selectById(serviceAreaId));
        }
        servicePointDTO.setServiceAreas(Lists2.transform(serviceAreaEntities, ServiceAreaTransform.ENTITY_TO_DTO));
        return ResultDTO.succeedWith(servicePointDTO);
    }

    @Override
    public ResultDTO<ServicePointDTO> getByPartnerIdAndType(Long partnerId, Integer type) {
        ServicePointBaseQueryRequest servicePointBaseQueryRequest = ServicePointBaseQueryRequest.builder().partnerId(partnerId).type(type).build();
        ResultDTO<List<ServicePointDTO>> listResultDTO = queryByParam(servicePointBaseQueryRequest);
        if (listResultDTO.isUnSuccess()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查找不合伙人的配送网点失败!");
        }
        List<ServicePointDTO> servicePointDTOS = listResultDTO.getModel();
        if (servicePointDTOS == null || servicePointDTOS.size() <= 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查找不到合伙人的配送网点!");
        }
        ServicePointDTO servicePointDTO = servicePointDTOS.get(0);
        return ResultDTO.succeedWith(servicePointDTO);
    }
}
