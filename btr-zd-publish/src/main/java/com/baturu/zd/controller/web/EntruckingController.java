package com.baturu.zd.controller.web;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.request.business.EntruckingDetailsQueryRequest;
import com.baturu.zd.request.business.EntruckingQueryRequest;
import com.baturu.zd.service.business.EntruckingDetailsService;
import com.baturu.zd.service.business.EntruckingService;
import com.baturu.zd.service.common.AuthenticationService;
import com.baturu.zd.service.dto.common.PageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * created by laijinjie by 2019/03/21
 **/
@RestController
@RequestMapping("web/entrucking")
@Slf4j
public class EntruckingController extends AbstractAppBaseController {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private EntruckingService entruckingService;
    @Autowired
    private EntruckingDetailsService dntruckingDetailsService;


    @GetMapping(value = "/list")
    public ResultDTO queryEntruckingByParam(EntruckingQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed("参数不能为空！");
        }
        log.info("查询装车单信息::参数{} 分页参数:{}", request, request.getCurrent());
        ResultDTO<PageDTO> pageDTOResultDTO = entruckingService.queryEntruckingDTOForPage(request);
        return pageDTOResultDTO;
    }


    @GetMapping(value = "/listDetails")
    public ResultDTO queryEntruckingDetailsByParam(EntruckingDetailsQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed("参数不能为空！");
        }
        log.info("查询装车单明细信息::参数{} 分页参数:{}", request, request.getCurrent());
        ResultDTO<PageDTO> pageDTOResultDTO = dntruckingDetailsService.queryEntruckingDetailsDTOForPage(request);
        return pageDTOResultDTO;
    }
}
