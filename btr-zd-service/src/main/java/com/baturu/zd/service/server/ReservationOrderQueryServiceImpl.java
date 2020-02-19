package com.baturu.zd.service.server;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.ReservationOrderConstant;
import com.baturu.zd.dto.wx.ReservationOrderDTO;
import com.baturu.zd.entity.wx.ReservationOrderEntity;
import com.baturu.zd.mapper.wx.ReservationOrderMapper;
import com.baturu.zd.request.server.ReservationOrderBaseQueryRequest;
import com.baturu.zd.service.server.ReservationOrderQueryService;
import com.baturu.zd.transform.wx.ReservationOrderTransform;
import com.baturu.zd.util.ObjectValidateUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * create by pengdi in 2019/3/15
 */
@Service(interfaceClass = ReservationOrderQueryService.class)
@Component("reservationOrderQueryService")
@Slf4j
public class ReservationOrderQueryServiceImpl implements ReservationOrderQueryService {
    @Autowired
    private ReservationOrderMapper reservationOrderMapper;

    @Override
    public ResultDTO<List<ReservationOrderDTO>> queryByParam(ReservationOrderBaseQueryRequest request) {
        if(ObjectValidateUtil.isAllFieldNull(request)){
            return ResultDTO.failed("参数为空");
        }
        QueryWrapper wrapper = this.initWrapper(request);
        List<ReservationOrderEntity> list = reservationOrderMapper.selectList(wrapper);
        List<ReservationOrderDTO> reservationOrderDTOS = Lists2.transform(list, ReservationOrderTransform.ENTITY_TO_DTO);
        return ResultDTO.succeedWith(reservationOrderDTOS);
    }

    @Override
    public ResultDTO<ReservationOrderDTO> queryById(Integer id) {
        if(id == null || id <= 0){
            return ResultDTO.failed("id为空");
        }
        ReservationOrderEntity reservationOrderEntity = reservationOrderMapper.selectById(id);
        return ResultDTO.successfy(ReservationOrderTransform.ENTITY_TO_DTO.apply(reservationOrderEntity));
    }
    private QueryWrapper initWrapper(ReservationOrderBaseQueryRequest reservationOrderQueryRequest){
        QueryWrapper wrapper=new QueryWrapper();
        if(StringUtils.isNotBlank(reservationOrderQueryRequest.getReservationNo())){
            wrapper.eq(ReservationOrderConstant.RESERVATION_ORDER_NO,reservationOrderQueryRequest.getReservationNo());
        }
        return wrapper;
    }
}
