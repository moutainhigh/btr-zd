package com.baturu.zd.controller.app;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.app.AppEntruckingDTO;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.common.EntruckingDTO;
import com.baturu.zd.service.business.EntruckingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 逐道收单APP装车发运控制器
 * @author CaiZhuliang
 * @since 2019-3-28
 */
@RestController("appEntruckingController")
@Slf4j
@RequestMapping("app/entrucking")
public class EntruckingController extends AbstractAppBaseController {

    @Autowired
    private EntruckingService entruckingService;

    @RequestMapping(value = "/create",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO create(@RequestBody AppEntruckingDTO appEntruckingDTO) {
        AppUserLoginInfoDTO appUserLoginInfo = getCurrentUserInfo();
        appEntruckingDTO.setCreateUserId(appUserLoginInfo.getUserId());
        appEntruckingDTO.setCreateUserName(appUserLoginInfo.getName());
        appEntruckingDTO.setServicePointId(appUserLoginInfo.getServicePointId());
        ResultDTO<EntruckingDTO> entruckingResultDTO = entruckingService.saveEntruckingInApp(appEntruckingDTO);
        if (entruckingResultDTO.isUnSuccess()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_503, entruckingResultDTO.getErrorMsg());
        }
        return ResultDTO.succeed(AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

}
