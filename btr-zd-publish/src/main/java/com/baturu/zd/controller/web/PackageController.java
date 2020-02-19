package com.baturu.zd.controller.web;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.web.PackageWebExcelDTO;
import com.baturu.zd.request.business.PackageQueryRequest;
import com.baturu.zd.service.business.PackageService;
import com.baturu.zd.service.business.TransportOrderService;
import com.baturu.zd.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * created by ketao by 2019/03/29
 **/
@RestController
@RequestMapping("web/package")
@Slf4j
public class PackageController extends AbstractAppBaseController {

    @Autowired
    private PackageService packageService;

    @Autowired
    private TransportOrderService transportOrderService;


    @RequestMapping(value = "query", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryInPage(PackageQueryRequest request, @RequestParam(required = false) List<Integer> states) {
        request.setStates((states == null || states.contains(-1)) ? null : states);
        return packageService.queryPackagesInPage(request);
    }



    @RequestMapping(value = "ordersTotal", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO ordersTotal(PackageQueryRequest request) {
        return transportOrderService.queryOrderSumByPackageRequest(request);
    }



    @RequestMapping(value = "queryByTransportOrderNo", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryByTransportOrderNoInPage(PackageQueryRequest request) {
        return packageService.queryByTransportOrderNoInPage(request);
    }

    /**
     * 包裹表导出excel
     *
     * @param response
     * @param request
     * @param states
     * @throws IOException
     */
    @GetMapping(value = "/queryPackgesExcel")
    public void queryPackgesExcel(HttpServletResponse response, PackageQueryRequest request, @RequestParam(required = false) List<Integer> states) throws IOException {
        log.info("导出包裹单excel,查询参数{}", request);
        response.setHeader("content-Type", "application/vnd.ms-excel;charset=UTF-8");
        // 文件名
        String dateStr = DateUtil.formatYYYYMMDDHHMMSS24(new Date());
        String fileName = "包裹表" + dateStr + ".xls";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        request.setStates((states == null || states.contains(-1)) ? null : states);
        List<PackageWebExcelDTO> packageWebExcelDTOS = packageService.queryPackagesExcel(request);
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), PackageWebExcelDTO.class, packageWebExcelDTOS);
        workbook.write(response.getOutputStream());
    }

}
