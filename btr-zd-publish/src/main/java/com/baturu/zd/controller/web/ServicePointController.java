package com.baturu.zd.controller.web;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.common.ProvinceDTO;
import com.baturu.zd.dto.common.ServiceAreaDTO;
import com.baturu.zd.dto.common.ServicePointDTO;
import com.baturu.zd.dto.common.ServicePointImportDTO;
import com.baturu.zd.request.business.ServicePointQueryRequest;
import com.baturu.zd.service.business.ServicePointService;
import com.google.common.collect.Sets;
import javafx.geometry.Pos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * created by laijinjie by 2019/03/22
 **/
@RestController
@RequestMapping("web/servicePoint")
@Slf4j
public class ServicePointController extends AbstractAppBaseController {

    @Autowired
    private ServicePointService servicePointService;


    @RequestMapping(value = "/queryAll", method = RequestMethod.GET)
    public ResultDTO queryAllServicePoint(@ModelAttribute ServicePointQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed("参数不能为空！");
        }
        AppUserLoginInfoDTO appUserLoginInfo = getCurrentUserInfo();
        if (!Integer.valueOf(1).equals(appUserLoginInfo.getRoot())){
            request.setIds(Sets.newHashSet(appUserLoginInfo.getServicePointId()));
        }
        log.info("查询所有业务网点信息::参数{}", request);
        ResultDTO<List<Map<String, Object>>> listResultDTO = servicePointService.queryAllServicePoint(request);
        return listResultDTO;
    }


    @GetMapping(value = "/queryServicePointByUser")
    public ResultDTO queryServicePointByUser() {
        AppUserLoginInfoDTO appUserLoginInfo = getCurrentUserInfo();
        return servicePointService.queryServicePointById(appUserLoginInfo.getServicePointId());
    }


    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryInPage(ServicePointQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed("参数不能为空！");
        }
        return servicePointService.queryServicePointsInPage(request);
    }



    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryById(Integer id) {
        if (id == null) {
            return ResultDTO.failed("网点id不能为空！");
        }
        return servicePointService.queryServicePointById(id);
    }


    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO saveServicePoint(@RequestBody ServicePointDTO servicePointDTO) {
        AppUserLoginInfoDTO userLoginInfoDTO = getCurrentUserInfo();
        if (servicePointDTO.getId() == null) {
            servicePointDTO.setCreateUserId(userLoginInfoDTO.getUserId());
        } else {
            servicePointDTO.setUpdateUserId(userLoginInfoDTO.getUserId());
            servicePointDTO.setUpdateTime(new Date());
        }
        return servicePointService.saveServicePoint(servicePointDTO);
    }



    @RequestMapping(value = "/queryOtherServiceArea", method = RequestMethod.GET)
    public ResultDTO queryOtherServiceAreaWithoutServicePointId(Integer servicePointId, Integer type) {
        log.info("queryOtherServiceAreaWithoutServicePointId requestParam = [servicePointId:{}, type:{}]", servicePointId, type);
        return servicePointService.queryOtherServiceAreaWithoutServicePointId(servicePointId, type);
    }

    /**
     * 配送网点导入
     */
    @RequestMapping(value = "importExpressPoint", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO importExpressPoint(@RequestBody ServicePointImportDTO servicePointImportDTO) {
        ResultDTO resultDTO = servicePointService.importExpressServicePoint(servicePointImportDTO);
        if(resultDTO.isUnSuccess()){
            log.info("配送网点导入参数:{}",servicePointImportDTO);
        }
        return resultDTO;
    }

    /**
     * 合伙人服务范围查询
     */
    @RequestMapping(value = "queryPartnerAreaByTeamName",method = RequestMethod.GET)
    public ResultDTO<List<ProvinceDTO>> queryPartnerAreaByTeamName(String teamName){
        ResultDTO<List<ProvinceDTO>> resultDTO = servicePointService.queryPartnerAreaByTeamName(teamName);
        if(resultDTO.isUnSuccess()){
            log.info("根据合伙人团队名称查询服务范围失败：{}，{}",teamName,resultDTO.getErrorMsg());
        }
        return resultDTO;
    }

    /**
     * 判断输入的合伙人是否存在
     */
    @RequestMapping(value = "existByTeamName",method = RequestMethod.GET)
    public ResultDTO<Boolean> existByTeamName(String teamName){
        return  servicePointService.existByTeamName(teamName);
    }

    @GetMapping(value = "names")
    public ResultDTO<List<ServicePointDTO>> getAllServicePointName() {

        return servicePointService.getDeliveryServicePointNames();
    }
}
