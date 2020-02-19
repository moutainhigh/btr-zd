package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.BlameDTO;
import com.baturu.zd.dto.BlameExcelDTO;
import com.baturu.zd.dto.OrderExceptionDTO;
import com.baturu.zd.request.business.BlameQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.List;

/**
 * 异常定责service
 * @author liuduanyang
 * @since 2019/5/7
 */
public interface BlameService {

    /**
     * 新增异常定责记录
     * @param blameDTO 异常定责记录
     * @return
     */
    ResultDTO<OrderExceptionDTO> save(BlameDTO blameDTO);

    /**
     * 根据异常处理id分页定责记录
     * @param orderExceptionId
     * @return
     */
    ResultDTO<PageDTO> listByOrderExceptionId(Integer orderExceptionId, Integer current, Integer size);

    /**
     * 根据异常处理查询定责记录
     * @param orderExceptionId
     * @return
     */
    List<BlameDTO> getByOrderExceptionId(Integer orderExceptionId);

    /**
     * 根据条件分页查询
     * @param request
     * @return
     */
    ResultDTO<PageDTO> listByPage(BlameQueryRequest request);

    /**
     *  根据条件查询excel数据
     */
    List<BlameExcelDTO> exportExcel(BlameQueryRequest request);

    /**
     * 根据id查询定责记录
     */
    ResultDTO<BlameDTO> getById(Integer blameId);

    ResultDTO updateById(BlameDTO blameDTO);
}
