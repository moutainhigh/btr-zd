package com.baturu.zd.controller.app;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.request.server.ServiceAreaBaseQueryRequest;
import com.baturu.zd.service.server.ServiceAreaQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuduanyang
 * @since 2019/6/26
 */
@RestController
@Slf4j
@RequestMapping("/app/serviceArea")
public class AppServiceAreaController extends AbstractAppBaseController {

    @Autowired
    private ServiceAreaQueryService serviceAreaQueryService;

    /**
     * 根据四级地址获取服务区域内的默认地址
     * @param request
     * @return
     */
    @GetMapping(value = "default")
    public ResultDTO getAreaDefaultAddress(ServiceAreaBaseQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "四级地址不能为空!");
        }

        ResultDTO resultDTO = serviceAreaQueryService.queryDefaultArea(request);

        return resultDTO;
    }
}
