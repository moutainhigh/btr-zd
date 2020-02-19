package com.baturu.zd.service.server;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.TranslineConstant;
import com.baturu.zd.dto.TransLineDTO;
import com.baturu.zd.entity.TransLineEntity;
import com.baturu.zd.mapper.TransLineMapper;
import com.baturu.zd.request.server.TranslineBaseQueryRequest;
import com.baturu.zd.service.common.TransLineService;
import com.baturu.zd.transform.TransLineTransform;
import com.baturu.zd.util.ObjectValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * create by pengdi in 2019/4/27
 */
@Service(interfaceClass = TranslineQueryService.class)
@Component("translineQueryService")
@Slf4j
public class TranslineQueryServiceImpl implements TranslineQueryService {
    @Autowired
    private TransLineMapper transLineMapper;
    @Autowired
    private TransLineService transLineService;

    @Override
    public ResultDTO<List<TransLineDTO>> queryByParam(TranslineBaseQueryRequest request) {
        if(ObjectValidateUtil.isAllFieldNull(request)){
            return ResultDTO.failed("逐道路线快照查询服务::参数为空");
        }
        QueryWrapper wrapper = this.buildWrapper(request);
        List<TransLineEntity> list = transLineMapper.selectList(wrapper);
        if(CollectionUtils.isEmpty(list)){
            return ResultDTO.successfy(Collections.emptyList());
        }
        return ResultDTO.successfy(Lists2.transform(list, TransLineTransform.ENTITY_TO_DTO));
    }

    @Override
    public ResultDTO<TransLineDTO> queryById(Integer id) {
        if(id == null || id <= 0){
            return ResultDTO.failed("逐道路线快照查询服务::id为空");
        }
        TransLineEntity entity = transLineMapper.selectById(id);
        return ResultDTO.successfy(TransLineTransform.ENTITY_TO_DTO.apply(entity));
    }

    private QueryWrapper buildWrapper(TranslineBaseQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if(StringUtils.isNotBlank(request.getTransportOrderNo())){
            wrapper.eq(TranslineConstant.TRANSPORT_ORDER_NO,request.getTransportOrderNo());
        }
        if(request.getActive() != null){
            wrapper.eq(TranslineConstant.ACTIVE,request.getActive());
        }else {
            wrapper.eq(TranslineConstant.ACTIVE,Boolean.TRUE);
        }
        return wrapper;
    }

    /**
     * 根据运单号查询路线信息/面单信息
     * @param transportOrderNo
     * @return
     */
    public ResultDTO<TransLineDTO> queryByOrderNo(String transportOrderNo){
        return transLineService.queryByOrderNo(transportOrderNo);
    }
}
