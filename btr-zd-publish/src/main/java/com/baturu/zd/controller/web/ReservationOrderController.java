package com.baturu.zd.controller.web;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.common.ReservationOrderExcelDTO;
import com.baturu.zd.request.business.ReservationOrderQueryRequest;
import com.baturu.zd.service.business.ReservationOrderService;
import com.baturu.zd.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * 逐道预约单
 *
 * @author laijinjie
 * @since 2019/3/26
 */
@RestController
@Slf4j
@RequestMapping("web/reservation")
public class ReservationOrderController extends AbstractAppBaseController {

    @Autowired
    private ReservationOrderService reservationOrderService;


    @GetMapping(value = "/list")
    public ResultDTO listByPage(ReservationOrderQueryRequest reservationOrderQueryRequest) {
        log.info("预约单列表查询，参数{}", reservationOrderQueryRequest);
        ResultDTO resultDTO = reservationOrderService.queryReservationOrdersInWebPage(reservationOrderQueryRequest);
        return resultDTO;
    }

    @GetMapping(value = "/queryReservationOrderSummary")
    public ResultDTO queryReservationOrderSummary(ReservationOrderQueryRequest reservationOrderQueryRequest) {
        return reservationOrderService.queryReservationOrderSummary(reservationOrderQueryRequest);
    }


    @GetMapping(value = "/exportReservationOrderExcel")
    public void exportReservationOrderExcel(HttpServletResponse response, ReservationOrderQueryRequest reservationOrderQueryRequest) throws IOException {
        log.info("导出预约单excel,查询参数{}", reservationOrderQueryRequest);
        response.setHeader("content-Type", "application/vnd.ms-excel;charset=UTF-8");
        // 文件名
        String dateStr = DateUtil.formatYYYYMMDDHHMMSS24(new Date());
        String fileName = "预约单" + dateStr + ".xls";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        List<ReservationOrderExcelDTO> excelDTOS = reservationOrderService.exportReservationOrderExcel(reservationOrderQueryRequest);
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), ReservationOrderExcelDTO.class, excelDTOS);
        workbook.write(response.getOutputStream());
    }
}
