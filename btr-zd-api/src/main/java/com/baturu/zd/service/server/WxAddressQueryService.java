package com.baturu.zd.service.server;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.request.business.WxAddressQueryRequest;
import com.baturu.zd.request.server.WxAddressBaseQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

public interface WxAddressQueryService extends BaseQueryService<WxAddressBaseQueryRequest,WxAddressDTO> {

    /**
     * 分页查询地址簿
     * @param request
     * @return
     */
    ResultDTO<PageDTO> selectWxAddressDTOForPage(WxAddressQueryRequest request);

    /**
     * 新增地址簿
     * @param wxAddressDTO
     * @return
     */
    ResultDTO<WxAddressDTO> saveOrUpdate(WxAddressDTO wxAddressDTO);
}
