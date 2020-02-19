package com.baturu.zd.controller.app;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.*;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.common.ServicePointDTO;
import com.baturu.zd.enums.ExceptionHandleResultEnum;
import com.baturu.zd.enums.ExceptionStateEnum;
import com.baturu.zd.request.business.OrderExceptionQueryRequest;
import com.baturu.zd.service.business.OrderExceptionService;
import com.baturu.zd.service.business.PackageService;
import com.baturu.zd.service.business.ServicePointService;
import com.baturu.zd.service.business.TransportOrderService;
import com.baturu.zd.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 运单异常控制器
 * @author liuduanyang
 * @since 2019/5/7
 */
@RestController
@Slf4j
@RequestMapping("app/orderException")
public class OrderExceptionController extends AbstractAppBaseController {

    @Autowired
    private OrderExceptionService orderExceptionService;

    @Autowired
    private TransportOrderService transportOrderService;

    @Autowired
    private PackageService packageService;

    @Autowired
    private ServicePointService servicePointService;

    /**
     * 根据运单号查询运单信息
     * @param transportOrderNo 运单号
     * @return
     */
    @GetMapping
    public ResultDTO<ExceptionInformationDTO> getByTransportOrderNo(String transportOrderNo) {
        if (StringUtils.isBlank(transportOrderNo)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "运单号不能为空");
        }
        ResultDTO<TransportOrderDTO> transportOrderResultDTO =
                transportOrderService.queryTransportOrdersByTransportOrderNo(transportOrderNo);
        if (transportOrderResultDTO.isUnSuccess()) {
            return ResultDTO.failed(transportOrderResultDTO.getErrorMsg());
        }
        TransportOrderDTO transportOrderDTO = transportOrderResultDTO.getModel();
        ExceptionInformationDTO exceptionInformationDTO = ExceptionInformationDTO.builder()
                .transportOrderNo(transportOrderDTO.getTransportOrderNo())
                .state(transportOrderDTO.getState())
                .build();
        // 查询运单下的包裹
        ResultDTO<List<PackageDTO>> listResultDTO = packageService.queryPackagesByTransportOrderNo(transportOrderNo, Collections.emptyList());
        if (listResultDTO.isUnSuccess()) {
            return ResultDTO.failed(listResultDTO.getErrorMsg());
        }
        List<PackageDTO> packageDTOS = listResultDTO.getModel();

        // 查询所有包裹的运单异常
        List<PackageExceptionDTO> packageExceptionDTOS = new ArrayList<>(packageDTOS.size());
        for (PackageDTO packageDTO : packageDTOS) {
            PackageExceptionDTO packageExceptionDTO = PackageExceptionDTO.builder()
                    .packageNo(packageDTO.getPackageNo())
                    .orderExceptionDTOList(orderExceptionService.getByPackageNo(packageDTO.getPackageNo()))
                    .build();
            packageExceptionDTOS.add(packageExceptionDTO);
        }
        exceptionInformationDTO.setPackageExceptionDTOList(packageExceptionDTOS);
        return ResultDTO.succeedWith(exceptionInformationDTO);
    }

    /**
     * 创建异常信息
     * @param orderExceptionDTO
     * @return
     */
    @PostMapping
    public ResultDTO create(@RequestBody OrderExceptionDTO orderExceptionDTO) throws Exception {
        if (orderExceptionDTO == null || orderExceptionDTO.getType() == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "异常类型不能为空");
        }
        if (CollectionUtils.isEmpty(orderExceptionDTO.getImages()) || orderExceptionDTO.getImages().size() <= 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "图片不能为空");
        }
        if (StringUtils.isBlank(orderExceptionDTO.getPackageNo())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "包裹号不能为空");
        }

        ResultDTO<PackageDTO> packageResultDTO = packageService.queryPackageByPackageNo(orderExceptionDTO.getPackageNo());
        if (packageResultDTO.isUnSuccess()) {
            return packageResultDTO;
        }

        AppUserLoginInfoDTO currentUserInfo = getCurrentUserInfo();
        ResultDTO<ServicePointDTO> resultDTO = servicePointService.queryServicePointById(currentUserInfo.getServicePointId());
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }

        ServicePointDTO servicePointDTO = resultDTO.getModel();
        PackageDTO packageDTO = packageResultDTO.getModel();

        // 填充异常信息
        orderExceptionDTO.setTransportOrderNo(packageDTO.getTransportOrderNo());
        orderExceptionDTO.setPackageId(packageDTO.getId());
        orderExceptionDTO.setPackageNo(packageDTO.getPackageNo());
        orderExceptionDTO.setTransportOrderId(packageDTO.getTransportOrderId());
        orderExceptionDTO.setCreateUserId(currentUserInfo.getUserId());
        orderExceptionDTO.setUpdateUserId(currentUserInfo.getUserId());
        orderExceptionDTO.setCreateUserName(currentUserInfo.getName());
        orderExceptionDTO.setDepartmentId(servicePointDTO.getId());
        orderExceptionDTO.setDepartmentName(servicePointDTO.getName());

        // 讲images图片数组设置到icon1，icon2，icon3，icon4属性中
        handleImage(orderExceptionDTO);

        ResultDTO<OrderExceptionDTO> orderExceptionResult = orderExceptionService.save(orderExceptionDTO);
        if (orderExceptionResult.isUnSuccess()) {
            return orderExceptionResult;
        }

        return ResultDTO.succeed();
    }

    /**
     * 根据条件分页查询异常信息
     * @param request
     * @return
     */
    @GetMapping("/list")
    public ResultDTO listByPage(OrderExceptionQueryRequest request) {

        if (request == null) {
            request = OrderExceptionQueryRequest.builder()
                    .startTime(DateUtil.countdown(30))
                    .endTime(DateUtil.getCurrentDate())
                    .build();
        }

        // 默认第一页，每页10条
        if (request.getCurrent() == null) {
            request.setCurrent(1);
        }
        if (request.getSize() == null) {
            request.setSize(10);
        }

        return orderExceptionService.listByPage(request);
    }

    /**
     * 异常信息导出excel
     * @param response
     * @param request
     * @throws IOException
     */
    @GetMapping(value = "/excel")
    public void exportOrderExceptionExcel(HttpServletResponse response, OrderExceptionQueryRequest request) throws IOException {
        log.info("导出异常excel,查询参数{}", request);
        response.setHeader("content-Type", "application/vnd.ms-excel;charset=UTF-8");

        // 文件名
        String date = DateUtil.formatYYYYMMDDHHMMSS24(DateUtil.getCurrentDate());
        String fileName = "异常处理" + date + ".xls";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

        List<OrderExceptionExcelDTO> orderExceptionExcelDTOS = orderExceptionService.exportExcel(request);
        if (CollectionUtils.isEmpty(orderExceptionExcelDTOS)) {
            response.getWriter().write("导出异常信息excel失败");
        }
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), OrderExceptionExcelDTO.class, orderExceptionExcelDTOS);
        workbook.write(response.getOutputStream());
    }

    /**
     * 异常处理关闭
     * @param id
     * @return
     */
    @PutMapping("/shutdown/{id}")
    public ResultDTO shutdown(@PathVariable("id") Integer id) {
        if (id == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "异常id不能为空");
        }

        OrderExceptionDTO orderExceptionDTO = orderExceptionService.getById(id);
        if (orderExceptionDTO == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查询不到异常记录");
        }

        orderExceptionDTO.setHandleResult(ExceptionHandleResultEnum.CLOSED.getType());
        orderExceptionDTO.setState(ExceptionStateEnum.FINISHED_HANDLE.getType());

        return orderExceptionService.updateById(orderExceptionDTO);
    }

    @GetMapping("/page")
    public ResultDTO listByTransportOrderNo(String transportOrderNo, Integer current, Integer size) {
        if (transportOrderNo == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "运单号不能为空");
        }
        if (current == null) {
            current = 1;
        }
        if (size == null) {
            size = 10;
        }
        return orderExceptionService.listByTransportOrderNo(transportOrderNo, current, size);
    }

    /**
     * images属性处理
     * @param orderExceptionDTO
     */
    private void handleImage(OrderExceptionDTO orderExceptionDTO) throws Exception {
        List<String> images = orderExceptionDTO.getImages();
        Class<OrderExceptionDTO> clazz = OrderExceptionDTO.class;
        for (int i = 0; i < images.size(); i++) {
            Method method = clazz.getDeclaredMethod("setIcon" + (i + 1), String.class);
            method.setAccessible(true);
            method.invoke(orderExceptionDTO, images.get(i));
        }
    }
}
