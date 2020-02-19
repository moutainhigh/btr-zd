package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.request.business.EntruckingDetailsQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

/**
 * create by laijinjie in 2019/3/21
 * 装车单明细  对前端服务
 */
public interface EntruckingDetailsService {

    ResultDTO<PageDTO> queryEntruckingDetailsDTOForPage(EntruckingDetailsQueryRequest request);

}
