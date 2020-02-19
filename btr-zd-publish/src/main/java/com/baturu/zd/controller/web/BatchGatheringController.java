package com.baturu.zd.controller.web;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.common.BatchGatheringDTO;
import com.baturu.zd.dto.common.BatchGatheringDetailDTO;
import com.baturu.zd.request.business.BatchGatheringDetailQueryRequest;
import com.baturu.zd.request.business.BatchGatheringQueryRequest;
import com.baturu.zd.service.business.BatchGatheringDetailService;
import com.baturu.zd.service.business.BatchGatheringService;
import com.baturu.zd.service.dto.common.PageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 逐道WEB批量号查询控制器
 * @author CaiZhuliang
 * @since 2019-10-18
 */
@RestController
@Slf4j
@RequestMapping("web/batchGathering")
public class BatchGatheringController extends AbstractAppBaseController {

    @Autowired
    private BatchGatheringService batchGatheringService;
    @Autowired
    private BatchGatheringDetailService batchGatheringDetailService;

    /**
     * 分页查询批量收款记录，要根据当前登录人做数据隔离
     */
    @RequestMapping(value = "query", method = RequestMethod.POST)
    public ResultDTO<PageDTO<BatchGatheringDTO>> queryInPage(@RequestBody BatchGatheringQueryRequest queryRequest) {
        if (queryRequest == null) {
            queryRequest = new BatchGatheringQueryRequest();
        }
        queryRequest.setCreateUserId(getCurrentUserInfo().getUserId());
        return batchGatheringService.queryInPage(queryRequest);
    }

    /**
     * 分页查询批量收款记录的明细
     */
    @RequestMapping(value = "queryDetailInPage", method = RequestMethod.POST)
    public ResultDTO<PageDTO<BatchGatheringDetailDTO>> queryDetailInPage(@RequestBody BatchGatheringDetailQueryRequest queryRequest) {
        if (queryRequest == null || StringUtils.isBlank(queryRequest.getBatchGatheringNo())) {
            return ResultDTO.failed("流水号不能为空");
        }
        return batchGatheringDetailService.queryInPage(queryRequest);
    }

    /**
     * 根据运单查询明细
     */
    @RequestMapping(value = "delete/{batchGatheringNo}", method = RequestMethod.PUT)
    public ResultDTO<BatchGatheringDTO> delete(@PathVariable String batchGatheringNo) {
        if (StringUtils.isBlank(batchGatheringNo)) {
            return ResultDTO.failed("流水号不能为空");
        }
        return batchGatheringService.deleteByBatchGatheringNo(batchGatheringNo);
    }

}
