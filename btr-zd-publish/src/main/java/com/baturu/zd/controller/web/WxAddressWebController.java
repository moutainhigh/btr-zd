package com.baturu.zd.controller.web;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.web.excel.WxAddressExcelDTO;
import com.baturu.zd.request.business.WxAddressQueryRequest;
import com.baturu.zd.service.business.WxAddressService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.util.DateUtil;
import com.baturu.zd.util.DefaultExcelStyleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * create by pengdi in 2019/3/29
 * web端地址簿controller
 */
@RestController
@RequestMapping("web/address")
@Slf4j
public class WxAddressWebController extends AbstractAppBaseController {

    @Autowired
    WxAddressService wxAddressService;


    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ResultDTO<PageDTO> list(WxAddressQueryRequest request) {
        return wxAddressService.selectWithSignForPage(request);
    }


    @RequestMapping(value = "export", method = RequestMethod.GET)
    public void exportAddressExcel(WxAddressQueryRequest request, HttpServletResponse response) throws IOException {
        long startTime = System.currentTimeMillis();
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 文件名
        String dateStr = DateUtil.formatYYYYMMDD(new Date());
        String fileName = "收寄件人信息" + dateStr + ".xls";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExportParams exportParams = new ExportParams("收寄件人信息", "收寄件人信息");
        List<WxAddressExcelDTO> excelDTOS = wxAddressService.exportWxAddressExcel(request);
        int size = excelDTOS.size();
        exportParams.setStyle(DefaultExcelStyleUtil.class);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, WxAddressExcelDTO.class, excelDTOS);
        workbook.write(response.getOutputStream());
        long endTime = System.currentTimeMillis();
        log.info("客户资料导出：耗时{}s，数量{}", Double.valueOf(((endTime - startTime)) / 1000), size);
    }

}
