package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.web.excel.IdentificationExcelDTO;
import com.baturu.zd.dto.wx.IdentificationDTO;
import com.baturu.zd.request.business.IdentificationQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.List;

/**
 * created by ketao by 2019/03/06
 **/
public interface IdentificationService {

    /**
     * 保存实名认证信息
     * @param identificationDTO
     * @return
     */
    ResultDTO<IdentificationDTO> saveIdentification(IdentificationDTO identificationDTO);

    /**
     * 实名认证信息查询
     * @param identificationQueryRequest
     * @return
     */
    ResultDTO<List<IdentificationDTO>> queryIdentifications(IdentificationQueryRequest identificationQueryRequest);

    /**
     * 实名认证信息分页查询
     * @param identificationQueryRequest
     * @return
     */
    ResultDTO<PageDTO> queryIdentificationsInPage(IdentificationQueryRequest identificationQueryRequest);

    /**
     * 实名认证信息分页查询 注册信息+实名认证信息
     * @param request
     * @return
     */
    ResultDTO<PageDTO> querySignWithIdentificationsInPage(IdentificationQueryRequest request);

    /**
     * 修改实名认证用户的类型  黑名单、月结用户等
     * @param identificationDTO
     * @return
     */
    ResultDTO<IdentificationDTO> updateCustomerType(IdentificationDTO identificationDTO);

    /**
     * Excel导出客户资料 注册+实名认证数据
     */
    List<IdentificationExcelDTO> exportIdentificationExcel(IdentificationQueryRequest request);
}
