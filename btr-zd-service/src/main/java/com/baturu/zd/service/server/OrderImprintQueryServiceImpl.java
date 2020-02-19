package com.baturu.zd.service.server;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.OrderImprintConstant;
import com.baturu.zd.dto.OrderImprintDTO;
import com.baturu.zd.dto.PackageImprintDTO;
import com.baturu.zd.entity.OrderImprintEntity;
import com.baturu.zd.mapper.OrderImprintMapper;
import com.baturu.zd.request.server.OrderImprintBaseQueryRequest;
import com.baturu.zd.transform.OrderImprintTransform;
import com.baturu.zd.util.ObjectValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * create by pengdi in 2019/3/21
 */
@Service(interfaceClass = OrderImprintQueryService.class)
@Component("orderImprintQueryService")
@Slf4j
public class OrderImprintQueryServiceImpl implements OrderImprintQueryService {
    @Autowired
    private OrderImprintMapper orderImprintMapper;

    @Override
    public ResultDTO<List<OrderImprintDTO>> queryByParam(OrderImprintBaseQueryRequest request) {
        if(ObjectValidateUtil.isAllFieldNull(request)){
            return ResultDTO.failed("运单轨迹信息::参数为空");
        }
        QueryWrapper wrapper = this.buildWrapper(request);
        List<OrderImprintEntity> list = orderImprintMapper.selectList(wrapper);
        if(CollectionUtils.isEmpty(list)){
            return ResultDTO.successfy(Collections.emptyList());
        }
        return ResultDTO.successfy(Lists2.transform(list, OrderImprintTransform.ENTITY_TO_DTO));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public ResultDTO<OrderImprintDTO> saveOrderImprint(OrderImprintDTO orderImprintDTO){
        ResultDTO resultDTO = this.checkValid(orderImprintDTO);
        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed(resultDTO.getErrorMsg());
        }
        OrderImprintEntity orderImprintEntity = OrderImprintTransform.DTO_TO_ENTITY.apply(orderImprintDTO);
        try {
            int num = orderImprintMapper.insert(orderImprintEntity);
            if (num == 0) {
                return ResultDTO.failed("运单操作记录保存失败");
            }
            orderImprintDTO.setId(orderImprintEntity.getId());
            return ResultDTO.succeedWith(orderImprintDTO);
        } catch (Exception e) {
            log.error("运单操作记录保存异常：",e);
            return ResultDTO.failed("运单操作记录保存异常："+e.getMessage());
        }
    }


    private ResultDTO checkValid(OrderImprintDTO orderImprintDTO){
        if (orderImprintDTO == null) {
            return ResultDTO.failed("包裹记录保存参数为空");
        }

        if (org.apache.commons.lang3.StringUtils.isBlank(orderImprintDTO.getTransportOrderNo())) {
            return ResultDTO.failed("运单记录保存：运单号为空");
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(orderImprintDTO.getOperator())) {
            return ResultDTO.failed("运单记录保存：操作人为空");
        }
        if (orderImprintDTO.getOperateType() == null) {
            return ResultDTO.failed("运单记录保存：操作类型为空");
        }
        if (StringUtils.isBlank(orderImprintDTO.getRemark())) {
            return ResultDTO.failed("运单记录保存：操作说明为空");
        }
        if (StringUtils.isBlank(orderImprintDTO.getLocation())) {
            return ResultDTO.failed("运单记录保存：当前位置为空");
        }
        return ResultDTO.succeed();
    }


    @Override
    public ResultDTO<OrderImprintDTO> queryById(Integer id) {
        if(id == null || id <= 0){
            return ResultDTO.failed("运单轨迹信息::id为空");
        }
        OrderImprintEntity entity = orderImprintMapper.selectById(id);
        return ResultDTO.successfy(OrderImprintTransform.ENTITY_TO_DTO.apply(entity));
    }

    private QueryWrapper buildWrapper(OrderImprintBaseQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if(CollectionUtils.isNotEmpty(request.getIds())){
            wrapper.in(OrderImprintConstant.ID,request.getIds());
        }
        if(StringUtils.isNotBlank(request.getReservationNo())){
            wrapper.eq(OrderImprintConstant.RESERVATION_NO,request.getReservationNo());
        }
        if(StringUtils.isNotBlank(request.getTransportOrderNo())){
            wrapper.eq(OrderImprintConstant.TRANSPORT_ORDER_NO,request.getTransportOrderNo());
        }
        if(request.getActive() != null){
            wrapper.eq(OrderImprintConstant.ACTIVE,request.getActive());
        } else {
            wrapper.eq(OrderImprintConstant.ACTIVE,Boolean.TRUE);
        }
        return wrapper;
    }
}
