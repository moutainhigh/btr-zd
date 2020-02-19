package com.baturu.zd.controller.app;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.TransportOrderConstant;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.TransLineDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.dto.TransportOrderLogDTO;
import com.baturu.zd.dto.app.AppTransportOrderDTO;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.app.ValidateTransportOrderDTO;
import com.baturu.zd.enums.GatheringStatusEnum;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.service.business.PackageService;
import com.baturu.zd.service.business.TransportOrderService;
import com.baturu.zd.service.common.TransLineService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.util.DateUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 逐道收单APP运单控制器
 *
 * @author CaiZhuliang
 * @since 2019-3-21
 */
@RestController("appTransportOrderController")
@Slf4j
@RequestMapping("app/transportOrder")
public class TransportOrderController extends AbstractAppBaseController {

    @Autowired
    private TransportOrderService transportOrderService;
    @Autowired
    private PackageService packageService;
    @Autowired
    private TransLineService transLineService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO list(@RequestBody TransportOrderQueryRequest queryRequest) {
        boolean flag = null == queryRequest || (null == queryRequest.getState() && null == queryRequest.getGatheringStatus());
        if (flag) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "运单状态和收款状态不能同时为空");
        }
        queryRequest.setServicePointId(getCurrentUserInfo().getServicePointId());
        log.debug("list queryRequest = {}", queryRequest);
        // 查询正向单
        queryRequest.setType(TransportOrderConstant.POSITIVE_ORDER_TYPE);
        ResultDTO<PageDTO<TransportOrderDTO>> resultDTO = transportOrderService.queryTransportOrdersInPage(queryRequest);
        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_502, resultDTO.getErrorMsg());
        }
        return ResultDTO.succeedWith(resultDTO.getModel(), AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

    @RequestMapping(value = "/details/{transportOrderNo}", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO details(@PathVariable String transportOrderNo) {
        if (StringUtils.isBlank(transportOrderNo)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请输入运单号");
        }
        ResultDTO<TransportOrderDTO> resultDTO = transportOrderService.queryTransportOrdersByTransportOrderNo(transportOrderNo);
        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_502, resultDTO.getErrorMsg());
        }
        TransportOrderDTO transportOrderDTO = resultDTO.getModel();
        if (!transportOrderDTO.getType().equals(TransportOrderConstant.POSITIVE_ORDER_TYPE)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查询不到该正向运单");
        }

        AppTransportOrderDTO appTransportOrderDTO = AppTransportOrderDTO.copy(transportOrderDTO);

        // 填充包裹号列表
        ResultDTO<List<PackageDTO>> packageDTOResult = packageService.queryPackagesByTransportOrderNo(transportOrderNo, Lists.newArrayList());
        if (packageDTOResult.isUnSuccess()) {
            log.info("details packageDTOResult = {}", packageDTOResult);
        }
        appTransportOrderDTO.setPackages(packageDTOResult.getModel());
        // 4个路线中的名称、编号、仓库名等
        ResultDTO<TransLineDTO> transLineDTOResult = transLineService.queryByOrderNo(transportOrderNo);
        if (transLineDTOResult.isUnSuccess()) {
            return transLineDTOResult;
        }
        TransLineDTO transLineDTO = transLineDTOResult.getModel();
        // copy路线信息到运单对象
        Date createTime = appTransportOrderDTO.getCreateTime();
        BeanUtils.copyProperties(transLineDTO, appTransportOrderDTO);
        appTransportOrderDTO.setCreateTime(createTime);
        return ResultDTO.succeedWith(appTransportOrderDTO, AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO create(@RequestBody AppTransportOrderDTO appTransportOrderDTO) {
        // 验证运单信息是否按照规则填写
        ResultDTO validateResultDTO = validateAppTransportOrderDTO(appTransportOrderDTO);
        if (validateResultDTO.isUnSuccess()) {
            return validateResultDTO;
        }
        AppUserLoginInfoDTO appUserLoginInfo = getCurrentUserInfo();
        appTransportOrderDTO.setCreateUserId(appUserLoginInfo.getUserId());
        appTransportOrderDTO.setCreateTime(DateUtil.getCurrentDate());
        appTransportOrderDTO.setServicePointId(appUserLoginInfo.getServicePointId());
        // 这里除了生成运单记录，也会生成对应的包裹记录，轨迹等等
        TransportOrderDTO transportOrderDTO = AppTransportOrderDTO.toTransportOrderDTO(appTransportOrderDTO);
        transportOrderDTO.setGatheringStatus(GatheringStatusEnum.UNPAY.getType());
        transportOrderDTO.setActive(true);
        transportOrderDTO.setWarehouseId(appUserLoginInfo.getWarehouseId());
        transportOrderDTO.setType(TransportOrderConstant.POSITIVE_ORDER_TYPE);
        ResultDTO<TransportOrderDTO> resultDTO = transportOrderService.insertTransportOrder(transportOrderDTO, appTransportOrderDTO.getReservationId(), TransportOrderConstant.APP);
        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_502, resultDTO.getErrorMsg());
        }
        return ResultDTO.succeedWith(resultDTO.getModel().getTransportOrderNo(), AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

    /**
     * 验证运单信息是否按照规则填写
     */
    private ResultDTO validateAppTransportOrderDTO(AppTransportOrderDTO appTransportOrderDTO) {
        if (null == appTransportOrderDTO) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写运单信息");
        }
        if (null == appTransportOrderDTO.getSenderAddrId() || appTransportOrderDTO.getSenderAddrId().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写寄件人信息");
        }
        if (null == appTransportOrderDTO.getRecipientAddrId() || appTransportOrderDTO.getRecipientAddrId().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填收件人信息");
        }
        if (null == appTransportOrderDTO.getQty() || appTransportOrderDTO.getQty() <= 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写件数");
        } else if (appTransportOrderDTO.getQty().compareTo(AppConstant.MAX_PACKAGE_NUM) > 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "件数最多99");
        }
        if (null == appTransportOrderDTO.getPayType() || appTransportOrderDTO.getPayType().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写付款方式");
        }
        if (null == appTransportOrderDTO.getGatheringStatus()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写运单收款状态");
        }
        if (BigDecimal.ZERO.compareTo(appTransportOrderDTO.getFreight()) >= 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写运费");
        }
        if (null == appTransportOrderDTO.getNailBox()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写是否钉箱");
        } else if (appTransportOrderDTO.getNailBox()) {
            if (null == appTransportOrderDTO.getNailBoxPayment() || BigDecimal.ZERO.compareTo(appTransportOrderDTO.getNailBoxPayment()) >= 0) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写钉箱费");
            }
            if (null == appTransportOrderDTO.getNailBoxNum() || appTransportOrderDTO.getNailBoxNum() <= 0
                    || appTransportOrderDTO.getNailBoxNum().compareTo(appTransportOrderDTO.getQty()) > 0) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写正确的钉箱数");
            }
        } else {
            // 没有钉箱，钉箱信息不保存
            appTransportOrderDTO.setNailBoxPayment(null);
            appTransportOrderDTO.setNailBoxNum(null);
        }
        if (null != appTransportOrderDTO.getCollectAmount() && BigDecimal.ZERO.compareTo(appTransportOrderDTO.getCollectAmount()) < 0) {
            if (StringUtils.isBlank(appTransportOrderDTO.getCollectAccount())) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写代收账号");
            }
            if (StringUtils.isBlank(appTransportOrderDTO.getBankName())) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写代收账号开户行");
            }
            if (StringUtils.isBlank(appTransportOrderDTO.getCollectAccountName())) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写代收账号人");
            }
        } else {
            // 没有代收，代收信息不保存
            appTransportOrderDTO.setCollectPayment(null);
            appTransportOrderDTO.setCollectAmount(null);
            appTransportOrderDTO.setCollectAccount(null);
            appTransportOrderDTO.setBankName(null);
            appTransportOrderDTO.setCollectAccountName(null);
        }
        // 获取网点信息是否代收
        AppUserLoginInfoDTO currentUserInfo = getCurrentUserInfo();
        Boolean needCollect = currentUserInfo.getNeedCollect();

        if (null == appTransportOrderDTO.getTotalPayment() || BigDecimal.ZERO.compareTo(appTransportOrderDTO.getTotalPayment()) >= 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "请填写应收费用");
        } else if (null == appTransportOrderDTO.getArrivePayment()) {
            appTransportOrderDTO.setArrivePayment(BigDecimal.ZERO);
        } else if (null == appTransportOrderDTO.getNowPayment()) {
            appTransportOrderDTO.setNowPayment(BigDecimal.ZERO);
        } else if (needCollect && appTransportOrderDTO.getTotalPayment()
                .compareTo(appTransportOrderDTO.getArrivePayment().add(appTransportOrderDTO.getNowPayment())) != 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "应收费用应该等于到付+现付");
        }
        if (null == appTransportOrderDTO.getState()) {
            appTransportOrderDTO.setState(AppConstant.TRANSPORT_ORDER.ORDER_STATE_CREATE_ORDER);
        }
        return ResultDTO.succeed();
    }

    @RequestMapping(value = "/packageQuery/{transportOrderNo}", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO packageQuery(@PathVariable String transportOrderNo) {
        ResultDTO<List<PackageDTO>> resultDTO = packageService.queryPackagesByTransportOrderNo(transportOrderNo, Lists.newArrayList());
        if (resultDTO.isUnSuccess()) {
            log.info("TransportOrderController packageQuery error. resultDTO = {}", resultDTO);
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_503, resultDTO.getErrorMsg());
        }
        return ResultDTO.succeedWith(resultDTO.getModel(), AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

    @RequestMapping(value = "/package/{warehousecode}", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryByWarehouseCode(@PathVariable String warehousecode) {
        if (StringUtils.isBlank(warehousecode)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "仓库编码必传");
        }
        // 根据仓库编码查询路线
        ResultDTO<List<TransLineDTO>> resultDTO = transLineService.queryByWarehouseCode(warehousecode);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        // 通过路线获的运单号
        List<TransLineDTO> transLineDTOS = resultDTO.getModel();
        List<PackageDTO> packageDTOS = Lists.newArrayList();
        for (TransLineDTO transLineDTO : transLineDTOS) {
            String transportOrderNo = transLineDTO.getTransportOrderNo();
            ResultDTO<List<PackageDTO>> listResultDTO = packageService.queryPackagesByTransportOrderNo(transportOrderNo, AppConstant.PACKAGE.STATE_CREATED_ORDER);
            if (listResultDTO.isUnSuccess()) {
                log.info("查询不到运单号对应的包裹信息", transportOrderNo);
                continue;
            }
            packageDTOS.addAll(listResultDTO.getModel());
        }
        return ResultDTO.succeedWith(packageDTOS, AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

    @RequestMapping(value = "/validatingOrder", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO validatingOrder(@RequestBody Map<String, ValidateTransportOrderDTO> validateMap) {
        log.info("validateMap = {}", validateMap);
        return ResultDTO.succeedWith(AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

    /**
     * 根据包裹号查询
     * @param packageNo
     */
    @GetMapping(value = "/{packageNo}")
    public ResultDTO queryByPackageNo(@PathVariable String packageNo) {
        if (StringUtils.isBlank(packageNo)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "");
        }
        ResultDTO<PackageDTO> resultDTO = packageService.queryPackageByPackageNo(packageNo);
        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "");
        }
        if (resultDTO == null || resultDTO.getModel() == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "");
        }
        return ResultDTO.succeedWith(resultDTO.getModel());
    }


    /**
     * 运单编辑
     */
    @PutMapping
    public ResultDTO update(@RequestBody TransportOrderLogDTO transportOrderLogDTO) throws Exception {
        if (transportOrderLogDTO == null || transportOrderLogDTO.getIdentification() == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "更新的运单数据为空");
        }

        AppUserLoginInfoDTO appUserLoginInfoDTO = getCurrentUserInfo();
        transportOrderLogDTO.setCreateUserId(appUserLoginInfoDTO.getUserId());
        transportOrderLogDTO.setUpdateUserId(appUserLoginInfoDTO.getUserId());
        transportOrderLogDTO.setCreateUserName(appUserLoginInfoDTO.getName());
        transportOrderLogDTO.setUpdateUserName(appUserLoginInfoDTO.getName());
        transportOrderLogDTO.setType(TransportOrderConstant.TRANSPOTR_ORDER_LOG);

        ResultDTO update = transportOrderService.update(transportOrderLogDTO);

        return update;
    }

}
