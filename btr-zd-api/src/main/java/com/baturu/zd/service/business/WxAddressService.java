package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.web.excel.WxAddressExcelDTO;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.request.business.WxAddressQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.List;


public interface WxAddressService {

    /**
     * 分页查询地址簿
     * @param request
     * @return
     */
    ResultDTO<PageDTO> selectWxAddressDTOForPage(WxAddressQueryRequest request);

    /**
     * 分页查询收寄件人信息（地址簿+注册信息）
     * @param request
     * @return
     */
    ResultDTO<PageDTO> selectWithSignForPage(WxAddressQueryRequest request);

    /**
     * 填充三级地址名称
     * @param wxAddressDTOS
     */
    List<WxAddressDTO> fillLevelAddressName(List<WxAddressDTO> wxAddressDTOS);

    /**
     * 新增地址簿
     * @param wxAddressDTO
     * @return
     */
    ResultDTO<WxAddressDTO> saveOrUpdate(WxAddressDTO wxAddressDTO);

    /**
     * 删除地址簿
     * @param wxAddressDTO
     * @return
     */
    ResultDTO deleteById(WxAddressDTO wxAddressDTO);

    /**
     * 导出收寄件人信息（地址簿+注册信息）
     * @param request
     * @return
     */
    List<WxAddressExcelDTO> exportWxAddressExcel(WxAddressQueryRequest request);
}
