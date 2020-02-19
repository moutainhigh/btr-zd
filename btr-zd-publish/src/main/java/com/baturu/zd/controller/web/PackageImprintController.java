package com.baturu.zd.controller.web;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.PackageImprintDTO;
import com.baturu.zd.request.server.PackageImprintBaseQueryRequest;
import com.baturu.zd.service.server.PackageImprintQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author liuduanyang
 * @since 2019/3/26
 */
@RestController
@Slf4j
@RequestMapping("web/packageImprint")
public class PackageImprintController extends AbstractAppBaseController {

    @Autowired
    private PackageImprintQueryService packageImprintQueryService;


    @RequestMapping(value = "query", method = RequestMethod.GET)
    public ResultDTO queryImprintByTransportOrderNo(String packageNo) {
        if (packageNo == null) {
            return ResultDTO.failed("包裹号必传");
        }
        PackageImprintBaseQueryRequest packageImprintBaseQueryRequest = new PackageImprintBaseQueryRequest();
        packageImprintBaseQueryRequest.setPackageNo(packageNo);
        ResultDTO<List<PackageImprintDTO>> listResultDTO = packageImprintQueryService.queryByParam(packageImprintBaseQueryRequest);
        if (listResultDTO.isUnSuccess()) {
            return listResultDTO;
        }
        List<PackageImprintDTO> packageImprintDTOS = listResultDTO.getModel();
        return ResultDTO.succeedWith(packageImprintDTOS);
    }
}
