package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.ExceptionFollowRecordDTO;
import com.baturu.zd.dto.OrderExceptionDTO;
import com.baturu.zd.service.dto.common.PageDTO;

/**
 * 异常跟踪记录service
 * @author liuduanyang
 * @since 2019/5/31
 */
public interface ExceptionFollowRecordService {

    /**
     * 根据异常id查询异常跟踪记录
     * @param orderExceptionId
     * @return
     */
    PageDTO getByOrderExceptionId(Integer orderExceptionId, Integer current, Integer size);

    /**
     * 新增异常跟踪记录
     * @param exceptionFollowRecordDTO 异常跟踪记录
     * @return
     */
    ResultDTO<OrderExceptionDTO> save(ExceptionFollowRecordDTO exceptionFollowRecordDTO);
}
