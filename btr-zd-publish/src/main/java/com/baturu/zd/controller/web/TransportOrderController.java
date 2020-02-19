package com.baturu.zd.controller.web;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.TransportOrderConstant;
import com.baturu.zd.constant.WxAddressConstant;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.dto.WebTransportOrderDTO;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.web.TransportOrderWebDTO;
import com.baturu.zd.dto.wx.WxAddressSnapshotDTO;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.service.business.TransportOrderService;
import com.baturu.zd.util.DateUtil;
import com.baturu.zd.util.ObjectValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * created by ketao by 2019/03/15
 **/
@RestController
@RequestMapping("web/transportOrder")
@Slf4j
public class TransportOrderController extends AbstractAppBaseController {

    @Autowired
    private TransportOrderService transportOrderService;

    @PostMapping(value = "/create")
    public ResultDTO create(@RequestBody WebTransportOrderDTO webTransportOrderDTO) {
        ResultDTO validateDTO = this.validateWebTransportOrderDTO(webTransportOrderDTO);
        if (validateDTO.isUnSuccess()) {
            return validateDTO;
        }
        TransportOrderDTO transportOrderDTO = WebTransportOrderDTO.toTransportOrderDTO(webTransportOrderDTO);
        AppUserLoginInfoDTO appUserLoginInfo = getCurrentUserInfo();
        transportOrderDTO.setCreateUserId(appUserLoginInfo.getUserId());
        transportOrderDTO.setCreateUserName(appUserLoginInfo.getName());
        transportOrderDTO.setCreateTime(DateUtil.getCurrentDate());
        transportOrderDTO.setServicePointId(appUserLoginInfo.getServicePointId());
        WxAddressSnapshotDTO recipientAddr = transportOrderDTO.getRecipientAddr();
        WxAddressSnapshotDTO senderAddr = transportOrderDTO.getSenderAddr();
        recipientAddr.setType(WxAddressConstant.RECIPIENT_ADDRESS_TYPE);
        recipientAddr.setIsDefault(false);
        recipientAddr.setCreateUserId(appUserLoginInfo.getUserId());
        senderAddr.setType(WxAddressConstant.SENDER_ADDRESS_TYPE);
        senderAddr.setIsDefault(false);
        senderAddr.setCreateUserId(appUserLoginInfo.getUserId());
        transportOrderDTO.setActive(Boolean.TRUE);
        // 这里除了生成运单记录，也会生成对应的包裹记录
        ResultDTO<TransportOrderDTO> resultDTO = transportOrderService.insertTransportOrder(transportOrderDTO, webTransportOrderDTO.getReservationId(), TransportOrderConstant.WEB);
        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_502, resultDTO.getErrorMsg());
        }
        return ResultDTO.succeedWith(resultDTO.getModel(), AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

    @PostMapping(value = "/abolishById")
    public ResultDTO abolishById(@RequestBody WebTransportOrderDTO webTransportOrderDTO) {
        if (null == webTransportOrderDTO || null == webTransportOrderDTO.getId()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "操作失败，运单为空");
        }
        TransportOrderDTO transportOrderDTO = TransportOrderDTO.builder().id(webTransportOrderDTO.getId()).active(webTransportOrderDTO.getActive()).build();
        AppUserLoginInfoDTO appUserLoginInfo = getCurrentUserInfo();
        // 作废运单记录
        return transportOrderService.abolishById(transportOrderDTO, appUserLoginInfo.getUserId());
    }

    /**
     * WEB运单查询
     */
    @RequestMapping(value = "query", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryInPage(TransportOrderQueryRequest request,
                                 @RequestParam(required = false) List<Integer> states) {
        if (ObjectValidateUtil.isAllFieldNull(request) && (CollectionUtils.isEmpty(states) || states.contains(-1))) {
            return ResultDTO.failed("至少提交一个查询条件");
        }
        request.setStates(CollectionUtils.isEmpty(states) || states.contains(-1) ? null : states);
        AppUserLoginInfoDTO appUserLoginInfo = getCurrentUserInfo();
        //网点数据区分
        if (!Integer.valueOf(1).equals(appUserLoginInfo.getRoot())){
            request.setServicePointId(appUserLoginInfo.getServicePointId());
            request.setPointPartnerId(appUserLoginInfo.getPartnerId());
        }
        return transportOrderService.queryTransportOrdersForWebInPage(request);
    }

    @RequestMapping(value = "details", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO details(String transportOrderNo) {
        if (StringUtils.isBlank(transportOrderNo)) {
            return ResultDTO.failed("请输入运单号");
        }
        ResultDTO resultDTO = transportOrderService.queryTransportOrdersByTransportOrderNo(transportOrderNo);

        if (!resultDTO.isSuccess()) {
            return ResultDTO.failed(resultDTO.getErrorMsg());
        }
        TransportOrderDTO transportOrderDTO = (TransportOrderDTO) resultDTO.getModel();
        return ResultDTO.succeedWith(transportOrderDTO);
    }

    @RequestMapping(value = "queryTransLine", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryTransLine(String transportOrderNo) {
        if (StringUtils.isEmpty(transportOrderNo)) {
            return ResultDTO.failed("运单号为空");
        }
        AppUserLoginInfoDTO appUserLoginInfo = getCurrentUserInfo();
        return transportOrderService.queryTransLine(transportOrderNo, appUserLoginInfo.getName());
    }

    /**
     * 运单表导出excel
     *
     * @param response
     * @param request
     * @throws IOException
     */
    @GetMapping(value = "/queryTransportOrdersExcel")
    public void queryTransportOrdersExcel(HttpServletResponse response, TransportOrderQueryRequest request, @RequestParam(required = false) List<Integer> states) throws IOException {
        log.info("导出运单excel,查询参数{}", request);
        response.setHeader("content-Type", "application/vnd.ms-excel;charset=UTF-8");
        // 文件名
        String dateStr = DateUtil.formatYYYYMMDDHHMMSS24(new Date());
        String fileName = "运单" + dateStr + ".xls";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        request.setStates((states == null || states.contains(-1)) ? null : states);
        AppUserLoginInfoDTO appUserLoginInfo = getCurrentUserInfo();
        //网点数据区分
        if (!Integer.valueOf(1).equals(appUserLoginInfo.getRoot())){
            request.setServicePointId(appUserLoginInfo.getServicePointId());
            request.setPointPartnerId(appUserLoginInfo.getPartnerId());
        }
        List<TransportOrderWebDTO> transportOrderWebExcelDTOS = transportOrderService.queryTransportOrdersExcel(request);
        for (TransportOrderWebDTO transportOrderWebDTO : transportOrderWebExcelDTOS) {
            if (transportOrderWebDTO.getDispatchPayment() == null ||
                    transportOrderWebDTO.getDispatchPayment().compareTo(BigDecimal.ZERO) == 0) {
                transportOrderWebDTO.setDispatchPayment(new BigDecimal(0.00));
            }
            if (transportOrderWebDTO.getSupportValuePayment() == null ||
                    transportOrderWebDTO.getSupportValuePayment().compareTo(BigDecimal.ZERO) == 0) {
                transportOrderWebDTO.setSupportValuePayment(new BigDecimal(0.00));
            }
            if (transportOrderWebDTO.getSupportValue() == null
                    || transportOrderWebDTO.getSupportValue().compareTo(BigDecimal.ZERO) == 0) {
                transportOrderWebDTO.setSupportValue(new BigDecimal(0.00));
            }
            if (transportOrderWebDTO.getCollectPayment() == null
                    || transportOrderWebDTO.getCollectPayment().compareTo(BigDecimal.ZERO) == 0) {
                transportOrderWebDTO.setCollectPayment(new BigDecimal(0.00));
            }
        }

        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), TransportOrderWebDTO.class, transportOrderWebExcelDTOS);
        workbook.write(response.getOutputStream());
    }

    /**
     * 验证运单信息是否按照规则填写
     */
    private ResultDTO validateWebTransportOrderDTO(WebTransportOrderDTO webTransportOrderDTO) {
        if (null == webTransportOrderDTO) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写运单信息");
        }
        if (null == webTransportOrderDTO.getActive()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写运单是否有效");
        }
        if (null != webTransportOrderDTO.getId()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "新增的运单id不能有值");
        }
        WxAddressSnapshotDTO senderAddr = webTransportOrderDTO.getSenderAddr();
        if (null == senderAddr) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写寄件人信息");
        } else if (StringUtils.isEmpty(senderAddr.getName())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写寄件人姓名");
        } else if (StringUtils.isEmpty(senderAddr.getPhone())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写寄件人电话");
        } else if (StringUtils.isEmpty(senderAddr.getProvinceName()) || null == senderAddr.getProvinceId() || senderAddr.getProvinceId().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写寄件人省份");
        } else if (StringUtils.isEmpty(senderAddr.getCityName()) || null == senderAddr.getCityId() || senderAddr.getCityId().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写寄件人城市");
        } else if (StringUtils.isEmpty(senderAddr.getCountyName()) || null == senderAddr.getCountyId() || senderAddr.getCountyId().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写寄件人区域");
        } else if (StringUtils.isEmpty(senderAddr.getTownName()) || null == senderAddr.getTownId() || senderAddr.getTownId().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写寄件人街道");
        } else if (StringUtils.isEmpty(senderAddr.getAddress())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写寄件人详细地址");
        }
        WxAddressSnapshotDTO recipientAddr = webTransportOrderDTO.getRecipientAddr();
        if (null == recipientAddr) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填收件人信息");
        } else if (StringUtils.isEmpty(recipientAddr.getName())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写收件人姓名");
        } else if (StringUtils.isEmpty(recipientAddr.getPhone())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写收件人电话");
        } else if (StringUtils.isEmpty(recipientAddr.getProvinceName()) || null == recipientAddr.getProvinceId() || recipientAddr.getProvinceId().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写收件人省份");
        } else if (StringUtils.isEmpty(recipientAddr.getCityName()) || null == recipientAddr.getCityId() || recipientAddr.getCityId().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写收件人城市");
        } else if (StringUtils.isEmpty(recipientAddr.getCountyName()) || null == recipientAddr.getCountyId() || recipientAddr.getCountyId().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写收件人区域");
        } else if (StringUtils.isEmpty(recipientAddr.getTownName()) || null == recipientAddr.getTownId() || recipientAddr.getTownId().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写收件人街道");
        } else if (StringUtils.isEmpty(recipientAddr.getAddress())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写收件人详细地址");
        }
        if (null == webTransportOrderDTO.getQty()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写件数");
        }
        if (null == webTransportOrderDTO.getFreight()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写运费");
        }
        if (null == webTransportOrderDTO.getDeliveryType() || webTransportOrderDTO.getDeliveryType().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请选择配送方式");
        }
        Boolean nailBox = webTransportOrderDTO.getNailBox();
        if (null == nailBox) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写是否钉箱");
        } else if (nailBox) {
            Integer nailBoxNum = webTransportOrderDTO.getNailBoxNum();
            if (null == nailBoxNum || nailBoxNum <= 0) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写钉箱数");
            }
            BigDecimal nailBoxPayment = webTransportOrderDTO.getNailBoxPayment();
            if (nailBoxPayment == null || nailBoxPayment.compareTo(BigDecimal.ZERO) < 0) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写钉箱费");
            }
        } else {
            // 没有钉箱，钉箱信息不保存
            webTransportOrderDTO.setNailBoxPayment(null);
            webTransportOrderDTO.setNailBoxNum(null);
        }
        if (null == webTransportOrderDTO.getPayType() || webTransportOrderDTO.getPayType().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请选择付款方式");
        }
        if (null == webTransportOrderDTO.getArrivePayment() || webTransportOrderDTO.getArrivePayment().compareTo(BigDecimal.ZERO) < 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写到付费用");
        }
        if (null == webTransportOrderDTO.getTotalPayment() || webTransportOrderDTO.getTotalPayment().compareTo(BigDecimal.ZERO) < 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写应收费用");
        }
        if (null == webTransportOrderDTO.getNowPayment() || webTransportOrderDTO.getNowPayment().compareTo(BigDecimal.ZERO) < 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写现收费用");
        }
        if (null != webTransportOrderDTO.getCollectAmount()) {
            if (webTransportOrderDTO.getCollectAmount().compareTo(BigDecimal.ZERO) < 1) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写正确的代收金额");
            }
            if (StringUtils.isBlank(webTransportOrderDTO.getCollectAccount())) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写代收账号");
            }
            if (StringUtils.isBlank(webTransportOrderDTO.getBankName())) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写代收账号开户行");
            }
            if (StringUtils.isBlank(webTransportOrderDTO.getCollectAccountName())) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写代收账号人");
            }
        } else {
            // 没有代收，代收信息不保存
            webTransportOrderDTO.setCollectAmount(null);
            webTransportOrderDTO.setCollectAccount(null);
            webTransportOrderDTO.setBankName(null);
            webTransportOrderDTO.setCollectAccountName(null);
        }
        Integer reservationId = webTransportOrderDTO.getReservationId();
        if (null != reservationId && reservationId > 0) {
            Integer recipientAddrId = webTransportOrderDTO.getRecipientAddrId();
            Integer senderAddrId = webTransportOrderDTO.getSenderAddrId();
            if (null == recipientAddrId || recipientAddrId.equals(0) || senderAddrId == null || senderAddrId.equals(0)) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "通过预约单新增运单的，收寄件人地址薄id不能为空");
            }
        }
        return ResultDTO.succeed();
    }
}
