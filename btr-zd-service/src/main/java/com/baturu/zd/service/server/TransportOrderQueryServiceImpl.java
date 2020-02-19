package com.baturu.zd.service.server;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.TransportOrderConstant;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.entity.TransportOrderEntity;
import com.baturu.zd.mapper.TransportOrderMapper;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.request.server.TransportOrderBaseQueryRequest;
import com.baturu.zd.service.business.TransportOrderService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.TransportOrderTransform;
import com.baturu.zd.util.ObjectValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * create by pengdi in 2019/3/15
 */
@Service(interfaceClass = TransportOrderQueryService.class)
@Component("transportOrderQueryService")
@Slf4j
public class TransportOrderQueryServiceImpl implements TransportOrderQueryService {
    @Autowired
    TransportOrderMapper transportOrderMapper;

    @Autowired
    TransportOrderService transportOrderService;

    @Override
    public ResultDTO<List<TransportOrderDTO>> queryByParam(TransportOrderBaseQueryRequest request) {
        if(ObjectValidateUtil.isAllFieldNull(request)){
            return ResultDTO.failed("参数为空");
        }
        QueryWrapper wrapper = this.initWrapper(request);
        List<TransportOrderEntity> list = transportOrderMapper.selectList(wrapper);
        List<TransportOrderDTO> reservationOrderDTOS = Lists2.transform(list, TransportOrderTransform.ENTITY_TO_DTO);
        return ResultDTO.successfy(reservationOrderDTOS);
    }

    private QueryWrapper initWrapper(TransportOrderBaseQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if(StringUtils.isNotBlank(request.getTransportOrderNo())){
            wrapper.eq(TransportOrderConstant.TRANSPORT_ORDER_NO,request.getTransportOrderNo());
        }
        if(request.getActive() != null){
            wrapper.eq(TransportOrderConstant.ACTIVE,request.getActive());
        }else {
            wrapper.eq(TransportOrderConstant.ACTIVE,Boolean.TRUE);
        }
        return wrapper;
    }

    @Override
    public ResultDTO<TransportOrderDTO> queryById(Integer id) {
        if(id == null || id <= 0){
            return ResultDTO.failed("id为空");
        }
        TransportOrderEntity transportOrderEntity = transportOrderMapper.selectById(id);
        return ResultDTO.successfy(TransportOrderTransform.ENTITY_TO_DTO.apply(transportOrderEntity));
    }

    @Override
    public ResultDTO<PageDTO> queryTransportOrdersInPage(TransportOrderQueryRequest transportOrderQueryRequest) {
        ResultDTO<PageDTO<TransportOrderDTO>> resultDTO = transportOrderService.queryTransportOrdersInPage(transportOrderQueryRequest);
        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed(resultDTO.getErrorMsg());
        }
        return ResultDTO.successfy(resultDTO.getModel());
    }

    @Override
    public ResultDTO<TransportOrderDTO> queryTransportOrdersByTransportOrderNo(String transportOrderNo) {
        return transportOrderService.queryTransportOrdersByTransportOrderNo(transportOrderNo);
    }

    @Override
    public ResultDTO<TransportOrderDTO> insertTransportOrder(TransportOrderDTO transportOrderDTO, Integer reservationId, String resource) {
        return transportOrderService.insertTransportOrder(transportOrderDTO, reservationId, resource);
    }

    @Override
    public ResultDTO<List<String>> queryTransportOrderByPartnerId(String partnerId, Integer packageState) {
        return transportOrderService.queryTransportOrderByPartnerId(partnerId, packageState);
    }
}
