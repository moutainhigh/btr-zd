package com.baturu.zd.service.server;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.PackageImprintConstant;
import com.baturu.zd.dto.PackageImprintDTO;
import com.baturu.zd.entity.PackageImprintEntity;
import com.baturu.zd.enums.PackageOperateTypeEnum;
import com.baturu.zd.mapper.PackageImprintMapper;
import com.baturu.zd.request.server.PackageImprintBaseQueryRequest;
import com.baturu.zd.transform.PackageImprintTransform;
import com.baturu.zd.util.ObjectValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * create by pengdi in 2019/3/22
 */
@Service(interfaceClass = PackageImprintQueryService.class)
@Component("packageImprintQueryService")
@Slf4j
public class PackageImprintQueryServiceImpl implements PackageImprintQueryService {
    @Autowired
    private PackageImprintMapper packageImprintMapper;

    @Override
    public ResultDTO<List<PackageImprintDTO>> queryByParam(PackageImprintBaseQueryRequest request) {
        if(ObjectValidateUtil.isAllFieldNull(request)){
            return ResultDTO.failed("包裹轨迹信息::参数全为空");
        }
        QueryWrapper wrapper = this.buildWrapper(request);
        List<PackageImprintEntity> list = packageImprintMapper.selectList(wrapper);
        if(CollectionUtils.isEmpty(list)){
            return ResultDTO.successfy(Collections.emptyList());
        }
        return ResultDTO.successfy(Lists2.transform(list,PackageImprintTransform.ENTITY_TO_DTO));
    }


    @Override
    public ResultDTO<PackageImprintDTO> queryById(Integer id) {
        if(id == null || id <= 0){
            return ResultDTO.failed("包裹轨迹信息::id为空");
        }
        PackageImprintEntity entity = packageImprintMapper.selectById(id);
        return ResultDTO.successfy(PackageImprintTransform.ENTITY_TO_DTO.apply(entity));
    }

    private QueryWrapper buildWrapper(PackageImprintBaseQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if (CollectionUtils.isNotEmpty(request.getIds())) {
            wrapper.in(PackageImprintConstant.ID,request.getIds());
        }
        if (StringUtils.isNotBlank(request.getPackageNo())) {
            wrapper.eq(PackageImprintConstant.PACKAGE_NO,request.getPackageNo());
        }
        if (StringUtils.isNotBlank(request.getTransportOrderNo())) {
            wrapper.eq(PackageImprintConstant.TRANSPORT_ORDER_NO,request.getTransportOrderNo());
        }
        if (request.getOperateType() != null) {
            wrapper.eq(PackageImprintConstant.OPERATE_TYPE,request.getOperateType());
        }
        if (StringUtils.isNotBlank(request.getLocation())) {
            wrapper.eq(PackageImprintConstant.LOCATION,request.getLocation());
        }
        if (request.getLocationId() != null && request.getLocationId() > 0) {
            wrapper.eq(PackageImprintConstant.LOCATION_ID,request.getLocationId());
        }
        if (request.getActive() != null) {
            wrapper.eq(PackageImprintConstant.ACTIVE,request.getActive());
        } else {
            wrapper.eq(PackageImprintConstant.ACTIVE,Boolean.TRUE);
        }
        wrapper.orderByDesc(PackageImprintConstant.CREATE_TIME);
        return wrapper;
    }
}
