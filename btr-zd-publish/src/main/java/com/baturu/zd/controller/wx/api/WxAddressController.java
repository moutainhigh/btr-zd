package com.baturu.zd.controller.wx.api;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.BaseConstant;
import com.baturu.zd.controller.BaseController;
import com.baturu.zd.dto.common.ServiceAreaDTO;
import com.baturu.zd.dto.wx.AmapKeyWordResultDTO;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.dto.wx.WxAddressVO;
import com.baturu.zd.dto.wx.WxSignDTO;
import com.baturu.zd.request.business.WxAddressQueryRequest;
import com.baturu.zd.request.wx.WxSignQueryRequest;
import com.baturu.zd.service.business.AddressAutoMatchService;
import com.baturu.zd.service.business.WxAddressService;
import com.baturu.zd.service.business.WxSignService;
import com.baturu.zd.service.common.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * create by pengdi in 2019/3/13
 * 微信地址簿(只用于微信客户端)
 */
@RestController
@RequestMapping("/address")
@Slf4j
public class WxAddressController extends BaseController {
    @Autowired
    private WxAddressService wxAddressService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private WxSignService wxSignService;
    @Autowired
    private AddressAutoMatchService addressAutoMatchService;


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultDTO queryWxAddressByParam(@ModelAttribute WxAddressQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed("参数不能为空！");
        }
        //手机号、名称条件存在查询所有用户的地址簿，没有则查询该用户下的
        if(StringUtils.isBlank(request.getPhone()) && StringUtils.isBlank(request.getName())){
            //获取当前登录账号信息
            WxSignDTO wxSign = authenticationService.getWxSign();
            //母账号查看  如果是获取默认地址则只查该账号的
            if(request.getIsDefault() != null && request.getIsDefault()){
                request.setCreateUserId(wxSign.getId());
            }else {
                ResultDTO<List<WxSignDTO>> resultDTO = wxSignService.selectByParam(WxSignQueryRequest.builder().ownerId(wxSign.getId()).build());
                if (resultDTO.isSuccess() && resultDTO.getModel().size() > 0) {
                    Set<Integer> childIds = resultDTO.getModel().stream().map(WxSignDTO::getId).collect(Collectors.toSet());
                    childIds.add(wxSign.getId());
                    request.setCreateUserIds(childIds);
                } else {
                    request.setCreateUserId(wxSign.getId());
                }
            }
        }
        request.setSource(BaseConstant.SOURCE_TYPE_WECHAT);

        return wxAddressService.selectWxAddressDTOForPage(request);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultDTO addWxAddress(@RequestBody WxAddressVO wxAddressVO) {
        if (wxAddressVO == null) {
            return ResultDTO.failed("参数不能为空！");
        }
        log.info("新增地址簿信息::参数{}", wxAddressVO);
        WxAddressDTO wxAddressDTO = WxAddressVO.toDTO(wxAddressVO);

        //获取当前登录账号信息
        WxSignDTO wxSign = authenticationService.getWxSign();
        wxAddressDTO.setCreateUserId(wxSign.getId());
        wxAddressDTO.setUpdateUserId(wxSign.getId());
        return wxAddressService.saveOrUpdate(wxAddressDTO);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultDTO updateWxAddress(@RequestBody WxAddressDTO wxAddressDTO) {
        log.info("修改地址簿信息::参数{}", wxAddressDTO);
        if (wxAddressDTO == null || wxAddressDTO.getId() == null) {
            return ResultDTO.failed("参数无效！");
        }
        //获取当前登录账号信息
        WxSignDTO wxSign = authenticationService.getWxSign();
        wxAddressDTO.setUpdateUserId(wxSign.getId());
        return wxAddressService.saveOrUpdate(wxAddressDTO);
    }

    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    public ResultDTO deleteWxAddress(@RequestBody WxAddressDTO wxAddressDTO) {
        log.info("删除地址簿信息::参数{}", wxAddressDTO);
        if (wxAddressDTO == null || wxAddressDTO.getId() == null) {
            return ResultDTO.failed("参数无效！");
        }
        return wxAddressService.deleteById(wxAddressDTO);
    }

    /**
     * 根据关键字获取地址列表
     * @param keyWord
     * @return
     */
    @RequestMapping(value = "matchByKeyWord",method = RequestMethod.GET)
    public ResultDTO<List<AmapKeyWordResultDTO>> matchByKeyWord(String keyWord){
        ResultDTO<List<AmapKeyWordResultDTO>> resultDTO = addressAutoMatchService.addListByKeyWord(keyWord);
        return resultDTO;
    }

    /**
     * 根据传入的经纬度获取对应街道，并匹配系统的服务区域获取服务区域记录
     * @param location
     * @param type 1:寄件地址，2：收件地址
     * @return
     */
    @RequestMapping(value = "matchByLocation",method = RequestMethod.GET)
    public ResultDTO<ServiceAreaDTO>  matchByLocation(String location,Integer type){
        ResultDTO<ServiceAreaDTO> resultDTO = addressAutoMatchService.matchAreaByLocation(location,type);
        return resultDTO;
    }
}
