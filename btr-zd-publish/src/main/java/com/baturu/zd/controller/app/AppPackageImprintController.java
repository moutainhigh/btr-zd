package com.baturu.zd.controller.app;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.PackageImprintDTO;
import com.baturu.zd.request.server.PackageImprintBaseQueryRequest;
import com.baturu.zd.service.server.PackageImprintQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("app/orderImprint")
public class AppPackageImprintController extends AbstractAppBaseController {

    @Autowired
    private PackageImprintQueryService packageImprintQueryService;

    @RequestMapping(value = "/{packageNo}", method = RequestMethod.GET)
    public ResultDTO queryImprintByTransportOrderNo(@PathVariable String packageNo) {
        if (packageNo == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "包裹号必传");
        }

        PackageImprintBaseQueryRequest packageImprintBaseQueryRequest = new PackageImprintBaseQueryRequest();
        packageImprintBaseQueryRequest.setPackageNo(packageNo);
        ResultDTO<List<PackageImprintDTO>> listResultDTO = packageImprintQueryService.queryByParam(packageImprintBaseQueryRequest);
        if (listResultDTO.isUnSuccess()) {
            return listResultDTO;
        }
        List<PackageImprintDTO> packageImprintDTOS = listResultDTO.getModel();
        if (packageImprintDTOS == null || packageImprintDTOS.size() <=0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查询不到包裹轨迹数据");
        }

        return ResultDTO.succeedWith(packageImprintDTOS);
    }
}
