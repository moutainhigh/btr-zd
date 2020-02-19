package com.baturu.zd.controller.app;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.ExceptionFollowRecordDTO;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.service.business.ExceptionFollowRecordService;
import com.baturu.zd.service.dto.common.PageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 异常跟踪记录控制器
 * @author liuduanyang
 * @since 2019/5/31
 */
@RestController
@Slf4j
@RequestMapping("/exception/followRecord")
public class ExceptionFollowRecordController extends AbstractAppBaseController {

    @Autowired
    private ExceptionFollowRecordService exceptionFollowRecordService;

    /**
     * 根据异常id查询异常跟踪记录
     * @param id
     * @return
     */
    @GetMapping("/list")
    public ResultDTO getByOrderExceptionId(Integer id, Integer current, Integer size) {
        if (id == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "异常id不能为空");
        }

        PageDTO pageDTO = exceptionFollowRecordService.getByOrderExceptionId(id, current, size);

        return ResultDTO.succeedWith(pageDTO);
    }

    /**
     * 创建异常跟踪记录
     * @param exceptionFollowRecordDTO
     * @return
     */
    @PostMapping
    public ResultDTO create(@RequestBody ExceptionFollowRecordDTO exceptionFollowRecordDTO) {
        if (exceptionFollowRecordDTO == null || StringUtils.isBlank(exceptionFollowRecordDTO.getRemark())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "备注不能为空");
        }

        AppUserLoginInfoDTO appUserLoginInfoDTO = getCurrentUserInfo();

        exceptionFollowRecordDTO.setCreateUserId(appUserLoginInfoDTO.getUserId());
        exceptionFollowRecordDTO.setUpdateUserId(appUserLoginInfoDTO.getUserId());
        exceptionFollowRecordDTO.setCreateUserName(appUserLoginInfoDTO.getName());

        return exceptionFollowRecordService.save(exceptionFollowRecordDTO);
    }
}
