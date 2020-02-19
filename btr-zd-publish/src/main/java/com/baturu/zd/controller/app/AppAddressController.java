package com.baturu.zd.controller.app;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.request.business.WxAddressQueryRequest;
import com.baturu.zd.service.business.WxAddressService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuduanyang
 * @since 2019/3/29
 */
@RestController
@Slf4j
@RequestMapping("app/address")
public class AppAddressController extends AbstractAppBaseController {

    @Autowired
    private WxAddressService wxAddressService;

    /**
     * 创建地址簿
     *
     * @param wxAddressDTO
     * @return ResultDTO
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResultDTO addWxAddress(@RequestBody WxAddressDTO wxAddressDTO) {
        if (wxAddressDTO == null) {
            return ResultDTO.failed("参数不能为空！");
        }
        // 从校验token返回的ResultDO中返回用户信息
        AppUserLoginInfoDTO appUserLoginInfoDTO = getCurrentUserInfo();

        wxAddressDTO.setCreateUserId(appUserLoginInfoDTO.getUserId());
        wxAddressDTO.setUpdateUserId(appUserLoginInfoDTO.getUserId());

        // 填充省市区名字
        ResultDTO<WxAddressDTO> addressResult = wxAddressService.saveOrUpdate(wxAddressDTO);
        if (addressResult.isUnSuccess()) {
            return addressResult;
        }
        addressResult.getModel().setCityName(wxAddressDTO.getCityName());
        addressResult.getModel().setCountyName(wxAddressDTO.getCountyName());
        addressResult.getModel().setProvinceName(wxAddressDTO.getProvinceName());
        addressResult.getModel().setTownName(wxAddressDTO.getTownName());

        return addressResult;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultDTO listByPage(WxAddressQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed("参数不能为空！");
        }
        request.setType(request.getType() == null || request.getType() != 2 ? 1 : 2);
        AppUserLoginInfoDTO appUserLoginInfoDTO = getCurrentUserInfo();
        if (StringUtils.isBlank(request.getPhone())) {
            request.setCreateUserId(appUserLoginInfoDTO.getUserId());
        }
        log.info("查询地址簿信息::参数{} ", request);
        return wxAddressService.selectWxAddressDTOForPage(request);
    }
}
