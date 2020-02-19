package com.baturu.zd.service.server;

import com.alibaba.dubbo.config.annotation.Service;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.OrderExceptionDTO;
import com.baturu.zd.service.business.OrderExceptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 运单异常对外暴露接口service
 * @author liuduanyang
 * @since 2019/5/7
 */
@Service(interfaceClass = OrderExceptionQueryService.class)
@Component("orderExceptionQueryService")
@Slf4j
public class OrderExceptionQueryServiceImpl implements OrderExceptionQueryService {

    @Autowired
    private OrderExceptionService orderExceptionService;

    @Override
    public ResultDTO<OrderExceptionDTO> save(OrderExceptionDTO orderExceptionDTO) throws Exception {
        return orderExceptionService.save(orderExceptionDTO);
    }
}
