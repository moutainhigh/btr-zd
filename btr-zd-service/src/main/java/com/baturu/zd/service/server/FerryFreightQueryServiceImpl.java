package com.baturu.zd.service.server;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.FerryFreightConstant;
import com.baturu.zd.dto.web.FerryFreightDTO;
import com.baturu.zd.entity.FerryFreightEntity;
import com.baturu.zd.mapper.FerryFreightMapper;
import com.baturu.zd.request.server.FerryFreightBaseQueryRequest;
import com.baturu.zd.transform.FerryFreightTransform;
import com.baturu.zd.util.ObjectValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * create by pengdi in 2019/3/14
 * 逐道摆渡运费单表查询服务
 */
@Service(interfaceClass = FerryFreightQueryService.class)
@Component("ferryFreightQueryService")
@Slf4j
public class FerryFreightQueryServiceImpl implements FerryFreightQueryService {
    @Autowired
    private FerryFreightMapper ferryFreightMapper;


    @Override
    public ResultDTO queryByParam(FerryFreightBaseQueryRequest request) {
        if(ObjectValidateUtil.isAllFieldNull(request)){
            return ResultDTO.failed("查询地址簿信息::参数不能为空");
        }
        QueryWrapper wrapper = this.buildWrapper(request);
        List<FerryFreightEntity> ferryFreightEntities = ferryFreightMapper.selectList(wrapper);
        if(CollectionUtils.isEmpty(ferryFreightEntities)){
            return ResultDTO.successfy(Collections.emptyList());
        }
        return ResultDTO.successfy(Lists2.transform(ferryFreightEntities, FerryFreightTransform.ENTITY_TO_DTO));
    }

    @Override
    public ResultDTO<FerryFreightDTO> queryById(Integer id) {
        if(id == null || id <= 0){
            return ResultDTO.failed("查询地址簿信息::id无效");
        }
        FerryFreightEntity ferryFreightEntity = ferryFreightMapper.selectById(id);
        return ResultDTO.successfy(FerryFreightTransform.ENTITY_TO_DTO.apply(ferryFreightEntity));
    }

    private QueryWrapper buildWrapper(FerryFreightBaseQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if(CollectionUtils.isNotEmpty(request.getIds())){
            wrapper.in(FerryFreightConstant.ID,request.getIds());
        }
        if(CollectionUtils.isNotEmpty(request.getServicePointIds())){
            wrapper.in(FerryFreightConstant.SERVICE_POINT_ID,request.getServicePointIds());
        }
        if(CollectionUtils.isNotEmpty(request.getWarehouseIds())){
            wrapper.in(FerryFreightConstant.WAREHOUSE_ID,request.getWarehouseIds());
        }
        if(request.getStartTime() != null && request.getEndTime() != null && request.getStartTime().before(request.getEndTime())){
            wrapper.ge(FerryFreightConstant.CREATE_TIME,request.getStartTime());
            wrapper.le(FerryFreightConstant.CREATE_TIME,request.getEndTime());
        }
        if(request.getActive() != null){
            wrapper.eq(FerryFreightConstant.ACTIVE,request.getActive());
        } else {
            wrapper.eq(FerryFreightConstant.ACTIVE,Boolean.TRUE);
        }
        return wrapper;
    }

}
