package com.baturu.zd.controller.web;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.web.FerryFreightDTO;
import com.baturu.zd.dto.web.excel.FerryFreightExcelDTO;
import com.baturu.zd.request.business.FerryFreightQueryRequest;
import com.baturu.zd.service.business.FerryFreightService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.util.DateUtil;
import com.baturu.zd.util.DefaultExcelStyleUtil;
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
 * create by pengdi in 2019/3/22
 * 摆渡运费（定价）controller
 */
@RestController
@RequestMapping("web/ferryFreight")
@Slf4j
public class FerryFreightController extends AbstractAppBaseController {

    @Autowired
    private FerryFreightService ferryFreightService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public ResultDTO<PageDTO> list(@ModelAttribute FerryFreightQueryRequest request){
        log.info("摆渡定价查询入参：{}",request);
        return ferryFreightService.selectForPage(request);
    }

    @RequestMapping(value = "/addFerryFreight",method = RequestMethod.POST)
    public ResultDTO<FerryFreightDTO> addFerryFreight(@RequestBody FerryFreightDTO ferryFreightDTO){
        AppUserLoginInfoDTO appUserLoginInfoDTO = getCurrentUserInfo();
        if(ferryFreightDTO == null){
            return ResultDTO.failed("新增摆渡定价::参数为空");
        }
        ferryFreightDTO.setCreateUserId(appUserLoginInfoDTO.getUserId());
        ferryFreightDTO.setCreateUserName(appUserLoginInfoDTO.getUsername());
        log.info("新增摆渡定价入参：{}",ferryFreightDTO);
        return ferryFreightService.saveOrUpdate(ferryFreightDTO);
    }

    @RequestMapping(value = "/updateFerryFreight",method = RequestMethod.POST)
    public ResultDTO<FerryFreightDTO> updateFerryFreight(@RequestBody FerryFreightDTO ferryFreightDTO){
        AppUserLoginInfoDTO appUserLoginInfoDTO = getCurrentUserInfo();
        if(ferryFreightDTO == null){
            return ResultDTO.failed("更新摆渡定价::参数为空");
        }
        ferryFreightDTO.setUpdateUserId(appUserLoginInfoDTO.getUserId());
        log.info("更新摆渡定价入参：{}",ferryFreightDTO);
        return ferryFreightService.saveOrUpdate(ferryFreightDTO);
    }

    @RequestMapping(value = "/export",method = RequestMethod.GET)
    public void exportFerryFreight(HttpServletResponse response, FerryFreightQueryRequest request) throws IOException {
        long startTime = System.currentTimeMillis();
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 文件名
        String dateStr = DateUtil.formatYYYYMMDDHHMMSS24(new Date());
        String fileName = "摆渡定价" + dateStr + ".xls";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        List<FerryFreightExcelDTO> excelDTOS = ferryFreightService.exportFerryFreightExcel(request);
        int size = excelDTOS.size();
        ExportParams exportParams = new ExportParams("摆渡定价","摆渡定价");
        exportParams.setStyle(DefaultExcelStyleUtil.class);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, FerryFreightExcelDTO.class, excelDTOS);
        workbook.write(response.getOutputStream());
        long endTime = System.currentTimeMillis();
        log.info("摆渡定价导出：耗时{}s，数量{}", Double.valueOf(((endTime - startTime))/1000),size);
    }


}
