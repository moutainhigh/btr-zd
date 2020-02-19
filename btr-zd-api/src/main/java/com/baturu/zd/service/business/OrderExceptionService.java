package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.OrderExceptionDTO;
import com.baturu.zd.dto.OrderExceptionExcelDTO;
import com.baturu.zd.request.business.OrderExceptionQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.List;

/**
 * 运单异常service
 * @author liuduanyang
 * @since 2019/5/7
 */
public interface OrderExceptionService {

    /**
     * 根据包裹号查询异常信息
     * @param packageNo 包裹号
     * @return 异常信息list
     */
    List<OrderExceptionDTO> getByPackageNo(String packageNo);

    /**
     * 新增异常信息
     * @param orderExceptionDTO 异常信息
     * @return
     */
    ResultDTO<OrderExceptionDTO> save(OrderExceptionDTO orderExceptionDTO);

    /**
     * 根据条件分页查询
     * @param request
     * @return
     */
    ResultDTO<PageDTO> listByPage(OrderExceptionQueryRequest request);

    /**
     * 根据条件查询list
     */
    List<OrderExceptionExcelDTO> exportExcel(OrderExceptionQueryRequest request);

    /**
     * 根据异常id获取异常处理
     */
    OrderExceptionDTO getById(Integer id);

    /**
     * 根据id更新异常处理
     */
    ResultDTO updateById(OrderExceptionDTO orderExceptionDTO);

    /**
     * 根据运单号分页查询
     * @param transportOrderNo
     * @return
     */
    ResultDTO listByTransportOrderNo(String transportOrderNo, Integer current, Integer size);
}
