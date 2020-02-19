package com.baturu.zd.service.server;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.TransLineDTO;
import com.baturu.zd.request.server.TranslineBaseQueryRequest;

/**
 * create by pengdi in 2019/4/27
 * 逐道线路快照基础查询服务
 */
public interface TranslineQueryService extends BaseQueryService<TranslineBaseQueryRequest, TransLineDTO> {

    /**
     * 根据运单号查询路线信息/面单信息
     * @param transportOrderNo
     * @return
     */
    ResultDTO<TransLineDTO> queryByOrderNo(String transportOrderNo);
}
