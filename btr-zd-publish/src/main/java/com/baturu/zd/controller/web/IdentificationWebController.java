package com.baturu.zd.controller.web;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.web.excel.IdentificationExcelDTO;
import com.baturu.zd.dto.wx.IdentificationDTO;
import com.baturu.zd.request.business.IdentificationQueryRequest;
import com.baturu.zd.service.business.IdentificationService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.util.DateUtil;
import com.baturu.zd.util.DefaultExcelStyleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * create by pengdi in 2019/3/26
 * 实名认证/客户资料 web controller
 */
@RestController
@Slf4j
@RequestMapping("web/identification")
public class IdentificationWebController extends AbstractAppBaseController {
    @Autowired
    IdentificationService identificationService;

    @RequestMapping(value = "list",method = RequestMethod.GET)
    public ResultDTO<PageDTO> list(IdentificationQueryRequest request){
        log.info("客户资料查询入参:{}",request);
        return identificationService.querySignWithIdentificationsInPage(request);
    }

    @RequestMapping(value = "updateType",method = RequestMethod.POST)
    public ResultDTO<IdentificationDTO> updateType(@RequestBody IdentificationDTO identificationDTO){
        if(identificationDTO == null){
            return ResultDTO.failed("修改客户资料::参数为空");
        }
        identificationDTO.setUpdateUserId(getCurrentUserInfo().getUserId());
        return identificationService.updateCustomerType(identificationDTO);
    }

    @RequestMapping(value = "export",method = RequestMethod.GET)
    public void exportIdentification(IdentificationQueryRequest request, HttpServletResponse response) throws IOException {
        long startTime = System.currentTimeMillis();
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 文件名
        String dateStr = DateUtil.formatYYYYMMDDHHMMSS24(new Date());
        String fileName = "客户资料" + dateStr + ".xls";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ExportParams exportParams = new ExportParams("客户资料","客户资料");
        List<IdentificationExcelDTO> excelDTOS = identificationService.exportIdentificationExcel(request);
        int size = excelDTOS.size();
        exportParams.setStyle(DefaultExcelStyleUtil.class);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, IdentificationExcelDTO.class, excelDTOS);
        workbook.write(response.getOutputStream());
        long endTime = System.currentTimeMillis();
        log.info("客户资料导出：耗时{}s，数量{}", Double.valueOf(((endTime - startTime))/1000),size);
    }

}
