package com.baturu.zd.controller.app;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.TransportOrderConstant;
import com.baturu.zd.service.business.EditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运单修改记录控制器
 * @author liuduanyang
 * @since 2019/5/21
 */
@RestController
@Slf4j
@RequestMapping("edit/log")
public class EditLogController extends AbstractAppBaseController {

    @Autowired
    private EditLogService editLogService;

    /**
     *  分页查询修改日志
     */
    @GetMapping("/list")
    public ResultDTO list(String orderNo, Integer current, Integer size) throws Exception {
        if (orderNo == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "单号不能为空");
        }

        if (current == null || current == 0) {
            current = 1;
        }

        if (size == null || size == 0) {
            size = 10;
        }

        ResultDTO resultDTO = editLogService.getEditLogs(orderNo, TransportOrderConstant.TRANSPOTR_ORDER_LOG, current, size);
        return resultDTO;
    }
}
