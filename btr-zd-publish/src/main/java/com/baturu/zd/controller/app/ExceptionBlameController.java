package com.baturu.zd.controller.app;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.BlameDTO;
import com.baturu.zd.dto.BlameExcelDTO;
import com.baturu.zd.dto.OrderExceptionDTO;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.enums.BlameStateEnum;
import com.baturu.zd.enums.ExceptionHandleResultEnum;
import com.baturu.zd.enums.ExceptionStateEnum;
import com.baturu.zd.request.business.BlameQueryRequest;
import com.baturu.zd.service.business.BlameService;
import com.baturu.zd.service.business.OrderExceptionService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 异常定责记录控制器
 * @author liuduanyang
 * @since 2019/6/3
 */
@RestController
@Slf4j
@RequestMapping("/exception/blame")
public class ExceptionBlameController extends AbstractAppBaseController {

    @Autowired
    private BlameService blameService;

    @Autowired
    private OrderExceptionService orderExceptionService;

    @PostMapping
    public ResultDTO create(@RequestBody BlameDTO blameDTO) {
        if (blameDTO == null || StringUtils.isBlank(blameDTO.getBlameName())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "责任部门不能为空");
        }
        Integer orderExceptionId = blameDTO.getOrderExceptionId();
        if (orderExceptionId == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "异常id不能为空");
        }

        AppUserLoginInfoDTO userInfo = getCurrentUserInfo();
        
        // 更新异常记录装填为处理中，处理结果为定责
        OrderExceptionDTO orderExceptionDTO = orderExceptionService.getById(orderExceptionId);
        orderExceptionDTO.setState(ExceptionStateEnum.HANDLING.getType());
        orderExceptionDTO.setHandleResult(ExceptionHandleResultEnum.BLAME.getType());

        ResultDTO resultDTO = orderExceptionService.updateById(orderExceptionDTO);
        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "更新异常信息失败");
        }


        blameDTO.setCreateUserId(userInfo.getUserId());
        blameDTO.setUpdateUserId(userInfo.getUserId());
        blameDTO.setCreateUserName(userInfo.getName());

        return blameService.save(blameDTO);
    }

    @GetMapping("/page")
    public ResultDTO getByOrderExceptionId(Integer orderExceptionId, Integer current, Integer size) {
        if (orderExceptionId == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "异常处理id不能为空");
        }

        ResultDTO<PageDTO> resultDTO = blameService.listByOrderExceptionId(orderExceptionId, current, size);

        return resultDTO;
    }

    @GetMapping("/list")
    public ResultDTO listByPage(BlameQueryRequest request) {
        if (request == null) {
            request = BlameQueryRequest.builder()
                    .startTime(DateUtil.countdown(30))
                    .endTime(DateUtil.getCurrentDate())
                    .build();
        }

        // 默认第一页，每页10条
        if (request.getCurrent() == null || request.getCurrent() == 0) {
            request.setCurrent(1);
        }
        if (request.getSize() == null || request.getSize() == 0) {
            request.setSize(10);
        }

        return blameService.listByPage(request);
    }

    /**
     * 定责记录导出excel
     * @param response
     * @param request
     * @throws IOException
     */
    @GetMapping(value = "/excel")
    public void exportBlameExcel(HttpServletResponse response, BlameQueryRequest request) throws IOException {
        log.info("导出定责记录excel,查询参数{}", request);
        response.setHeader("content-Type", "application/vnd.ms-excel;charset=UTF-8");

        // 文件名
        String date = DateUtil.formatYYYYMMDDHHMMSS24(DateUtil.getCurrentDate());
        String fileName = "异常定责" + date + ".xls";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

        request.setCurrent(null);
        request.setSize(null);

        List<BlameExcelDTO> blameExcelDTOS = blameService.exportExcel(request);
        if (CollectionUtils.isEmpty(blameExcelDTOS)) {
            response.getWriter().write("导出定责信息excel失败");
        }
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), BlameExcelDTO.class, blameExcelDTOS);
        workbook.write(response.getOutputStream());
    }

    /**
     * 同意定责
     * @param blameId
     * @param content
     * @return
     */
    @GetMapping("/aggree")
    public ResultDTO aggree(Integer blameId, String content) {
        if (blameId == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "定责id不能为空");
        }

        ResultDTO<BlameDTO> resultDTO = blameService.getById(blameId);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        BlameDTO blameDTO = resultDTO.getModel();

        // 更新定责记录状态并填充审核备注
        blameDTO.setState(BlameStateEnum.FINISH_AUDIT.getType());
        blameDTO.setReviewRemark(content);

        ResultDTO updateBlameResult = blameService.updateById(blameDTO);
        if (updateBlameResult.isUnSuccess()) {
            return updateBlameResult;
        }

        // 更新异常记录为已处理
        Integer orderExceptionId = blameDTO.getOrderExceptionId();
        OrderExceptionDTO orderExceptionDTO = orderExceptionService.getById(orderExceptionId);

        orderExceptionDTO.setState(ExceptionStateEnum.FINISHED_HANDLE.getType());

        ResultDTO updateOrderExceptionResult = orderExceptionService.updateById(orderExceptionDTO);
        if (updateOrderExceptionResult.isUnSuccess()) {
            return updateOrderExceptionResult;
        }

        return ResultDTO.succeed();
    }

    /**
     * 定责驳回
     * @param blameId
     * @param content
     * @return
     */
    @GetMapping("/reject")
    public ResultDTO reject(Integer blameId, String content) {
        if (blameId == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "定责id不能为空");
        }
        if (content == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "驳回意见不能为空");
        }

        ResultDTO<BlameDTO> resultDTO = blameService.getById(blameId);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        BlameDTO blameDTO = resultDTO.getModel();

        // 更新定责记录状态为已驳回
        blameDTO.setState(BlameStateEnum.REJECT.getType());
        blameDTO.setReviewRemark(content);

        ResultDTO updateResultDTO = blameService.updateById(blameDTO);
        if (updateResultDTO.isUnSuccess()) {
            return updateResultDTO;
        }

        return ResultDTO.succeed();
    }
}
