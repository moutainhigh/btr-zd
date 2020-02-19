package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.config.datasource.DataSourceType;
import com.baturu.zd.config.datasource.Datasource;
import com.baturu.zd.dto.OrderImprintDTO;
import com.baturu.zd.entity.OrderImprintEntity;
import com.baturu.zd.mapper.OrderImprintMapper;
import com.baturu.zd.transform.OrderImprintTransform;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * create by pengdi in 2019/3/21
 * 运单轨迹信息biz服务
 */
@Service("orderImprintService")
@Slf4j
public class OrderImprintServiceImpl implements OrderImprintService {

    @Autowired
    private OrderImprintMapper orderImprintMapper;

    @Transactional(rollbackFor = Throwable.class)
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    @Override
    public ResultDTO<OrderImprintDTO> saveOrUpdate(OrderImprintDTO orderImprintDTO) {
        ResultDTO validateResult = this.validateRequest(orderImprintDTO);
        if(validateResult.isUnSuccess()){
            return validateResult;
        }
        OrderImprintEntity entity = OrderImprintTransform.DTO_TO_ENTITY.apply(orderImprintDTO);
        entity.setUpdateTime(new Date());
        Integer result;
        if(entity.getId() == null){
            entity.setActive(Boolean.TRUE);
            entity.setCreateTime(new Date());
            result = orderImprintMapper.insert(entity);
        }else {
            result = orderImprintMapper.updateById(entity);
        }

        if(result <= 0){
            return ResultDTO.failed("运单轨迹信息::保存/更新失败");
        }
        return ResultDTO.successfy(OrderImprintTransform.ENTITY_TO_DTO.apply(entity));
    }

    private ResultDTO validateRequest(OrderImprintDTO orderImprintDTO) {
        if(orderImprintDTO == null){
            return ResultDTO.failed("运单轨迹信息::参数为空");
        }
        if(orderImprintDTO.getId() == null){
            if(StringUtils.isBlank(orderImprintDTO.getTransportOrderNo())){
                return ResultDTO.failed("运单轨迹信息::运单号为空");
            }
            if(StringUtils.isBlank(orderImprintDTO.getOperator())){
                return ResultDTO.failed("运单轨迹信息::操作人为空");
            }
            if(StringUtils.isBlank(orderImprintDTO.getRemark())){
                return ResultDTO.failed("运单轨迹信息::操作说明为空");
            }
        }
        return ResultDTO.succeed();
    }
}
