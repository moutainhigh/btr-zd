package com.baturu.zd.controller.web;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.web.excel.FerryOrderDetailsExcelDTO;
import com.baturu.zd.dto.web.excel.FerryOrderExcelDTO;
import com.baturu.zd.request.business.FerryOrderDetailQueryRequest;
import com.baturu.zd.request.business.FerryOrderQueryRequest;
import com.baturu.zd.service.business.FerryOrderDetailsService;
import com.baturu.zd.service.business.FerryOrderService;
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
 * create by pengdi in 2019/3/26
 * 后台管理系统web 摆渡单 controller
 */
@RestController
@RequestMapping("web/ferry")
@Slf4j
public class FerryOrderWebController extends AbstractAppBaseController {
    @Autowired
    FerryOrderService ferryOrderService;
    @Autowired
    FerryOrderDetailsService ferryOrderDetailsService;

    @RequestMapping(value = "/order/list",method = RequestMethod.GET)
    public ResultDTO<PageDTO> orderList(FerryOrderQueryRequest request){
//        log.info("摆渡单查询入参：{}",request);
        return ferryOrderService.queryForPage(request);
    }

    @RequestMapping(value = "/order/export",method = RequestMethod.GET)
    public void exportFerryOrder(FerryOrderQueryRequest request, HttpServletResponse response) throws IOException {
        long startTime = System.currentTimeMillis();
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 文件名
        String dateStr = DateUtil.formatYYYYMMDDHHMMSS24(new Date());
        String fileName = "摆渡单" + dateStr + ".xls";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        List<FerryOrderExcelDTO> excelDTOS = ferryOrderService.exportFerryOrderExcel(request);
        int size = excelDTOS.size();
        ExportParams exportParams = new ExportParams("摆渡单","摆渡单");
        exportParams.setStyle(DefaultExcelStyleUtil.class);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, FerryOrderExcelDTO.class, excelDTOS);
        workbook.write(response.getOutputStream());
        long endTime = System.currentTimeMillis();
        log.info("摆渡单导出：耗时{}s，数量{}", Double.valueOf(((endTime - startTime))/1000),size);
    }

    @RequestMapping(value = "/detail/list",method = RequestMethod.GET)
    public ResultDTO<PageDTO> detailList(FerryOrderDetailQueryRequest request){
//        log.info("摆渡单明细查询入参：{}",request);
        return ferryOrderDetailsService.queryForPage(request);
    }

    @RequestMapping(value = "/detail/export",method = RequestMethod.GET)
    public void exportFerryOrdeDetails(FerryOrderDetailQueryRequest request, HttpServletResponse response) throws IOException {
        long startTime = System.currentTimeMillis();
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 文件名
        String dateStr = DateUtil.formatYYYYMMDDHHMMSS24(new Date());
        String fileName = "摆渡单明细" + dateStr + ".xls";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        List<FerryOrderDetailsExcelDTO> excelDTOS = ferryOrderDetailsService.exportFerryOrderDetails(request);
        int size = excelDTOS.size();
        ExportParams exportParams = new ExportParams("摆渡单明细","摆渡单明细");
        exportParams.setStyle(DefaultExcelStyleUtil.class);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, FerryOrderDetailsExcelDTO.class, excelDTOS);
        workbook.write(response.getOutputStream());
        long endTime = System.currentTimeMillis();
        log.info("摆渡单明细导出：耗时{}s，数量{}", Double.valueOf(((endTime - startTime))/1000),size);
    }

}
