package com.baturu.zd.controller.app;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.gateway.constants.Channel;
import com.baturu.gateway.constants.enums.PayOrderTypeEnum;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.paybuss.pay2.constants.CarrierOrderTypeEnum;
import com.baturu.paybuss.pay2.constants.CarrierPayTypeEnum;
import com.baturu.paybuss.pay2.dto.PayCarrierOrderDTO;
import com.baturu.paybuss.pay2.dto.PayCarrierOrderQueryParam;
import com.baturu.paybuss.pay2.dto.PayDTO;
import com.baturu.paybuss.pay2.dto.PayResultDTO;
import com.baturu.paybuss.pay2.service.Pay2Service;
import com.baturu.paybuss.pay2.service.PayCarrierOrderQueryService;
import com.baturu.trade.cart.constants.Platform;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.app.GatheringDTO;
import com.baturu.zd.dto.common.BatchGatheringDTO;
import com.baturu.zd.dto.common.BatchGatheringDetailDTO;
import com.baturu.zd.dto.common.ServicePointDTO;
import com.baturu.zd.enums.BatchGatheringStatusEnum;
import com.baturu.zd.service.business.BatchGatheringDetailService;
import com.baturu.zd.service.business.BatchGatheringService;
import com.baturu.zd.service.business.ServicePointService;
import com.baturu.zd.vo.app.CreateQrcodeResultVO;
import com.baturu.zd.vo.app.QueryPayResultVO;
import com.baturu.zd.vo.web.CreateQrcodeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 逐道收单APP运单控制器
 * @author CaiZhuliang
 * @since 2019-3-27
 */
@RestController
@Slf4j
@RequestMapping("app/gathering")
public class GatheringController extends AbstractAppBaseController {

    /**
     * 支付订单类型 1:交易订单 2：物流订单
     */
    private static final Integer PAY_ORDER_TYPE = 2;
    private static final String REMARK = "%s【%s】通过逐道APP收款";
    private static final String QUERYPAYRESULT_FAILURE = "运单号/摆渡单号/流水号【%s】查询不到对应的支付记录.";

    @Reference(check = false)
    private Pay2Service pay2Service;
    @Reference(check = false)
    private PayCarrierOrderQueryService payCarrierOrderQueryService;
    @Autowired
    private ServicePointService servicePointService;
    @Autowired
    private BatchGatheringService batchGatheringService;
    @Autowired
    private BatchGatheringDetailService batchGatheringDetailService;

    @RequestMapping(value = "/createQrcode", method = RequestMethod.POST)
    public ResultDTO<CreateQrcodeResultVO> createQrcode(@RequestBody GatheringDTO gatheringDTO) {
        log.info("GatheringController##createQrcode : gatheringDTO = {}", gatheringDTO);
        ResultDTO validateRequestParamResultDTO = validateRequestParam(gatheringDTO);
        if (validateRequestParamResultDTO.isUnSuccess()) {
            log.info("GatheringController##createQrcode error : 调用预支付接口失败。 validateRequestParamResultDTO = {}", validateRequestParamResultDTO);
            return validateRequestParamResultDTO;
        }
        log.info("GatheringController##createQrcode - 【{}】发起预支付请求", gatheringDTO.getPayOrderNo());
        AppUserLoginInfoDTO appUserLoginInfo = getCurrentUserInfo();
        if (StringUtils.isBlank(gatheringDTO.getSubject())) {
            gatheringDTO.setSubject(String.format(REMARK, appUserLoginInfo.getName(), appUserLoginInfo.getUsername()));
        }
        ResultDTO<PayResultDTO> payResultDTO = pay2Service.pay(getPayDTO(gatheringDTO, appUserLoginInfo));
        if (payResultDTO.isUnSuccess()) {
            log.error("GatheringController##createQrcode error : 调用预支付接口失败，请联系资金流同事查看原因。payResultDTO = {}", payResultDTO);
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_503, payResultDTO.getErrorMsg());
        }
        CreateQrcodeResultVO createQrcodeResultVO = CreateQrcodeResultVO.builder().build();
        BeanUtils.copyProperties(payResultDTO.getModel(), createQrcodeResultVO);
        return ResultDTO.succeedWith(createQrcodeResultVO, AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

    @RequestMapping(value = "/queryPayResult/{payOrderNo}", method = RequestMethod.GET)
    public ResultDTO queryPayResult(@PathVariable String payOrderNo) {
        if (StringUtils.isBlank(payOrderNo)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "运单号/摆渡单号/流水号不能为空");
        }
        ResultDTO<PayCarrierOrderDTO> payResultDTO = payCarrierOrderQueryService.findPayCarrierOrderDTOByParam(PayCarrierOrderQueryParam.builder()
                .carrierType(CarrierPayTypeEnum.CASH_IMMEDIATELY.getValue())
                .carrierNo(payOrderNo)
                .build());
        if (payResultDTO.isUnSuccess()) {
            log.error("GatheringController##queryPayResult error : 调用查询运单支付记录接口失败，请联系资金流同事查看原因。payResultDTO = {}", payResultDTO);
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_503, payResultDTO.getErrorMsg());
        }
        PayCarrierOrderDTO payCarrierOrderDTO = payResultDTO.getModel();
        if (null == payCarrierOrderDTO) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_502, String.format(QUERYPAYRESULT_FAILURE, payOrderNo));
        }
        QueryPayResultVO queryPayResultVO = QueryPayResultVO.builder().build();
        BeanUtils.copyProperties(payCarrierOrderDTO, queryPayResultVO);
        return ResultDTO.succeedWith(queryPayResultVO, AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

    /**
     * 批量收款
     */
    @RequestMapping(value = "/createQrcodeByBatch", method = RequestMethod.POST)
    public ResultDTO createQrcodeByBatch(@RequestBody CreateQrcodeVO createQrcodeVO) {
        if (createQrcodeVO == null || CollectionUtils.isEmpty(createQrcodeVO.getTransportOrderNos())) {
            return ResultDTO.failed("请勾选需要付款的运单");
        }
        if (createQrcodeVO.getPlatform() == null || createQrcodeVO.getPlatform() == 0) {
            createQrcodeVO.setPlatform(Platform.WEB);
        }
        // 创建批量收款流水号
        AppUserLoginInfoDTO appUserLoginInfo = getCurrentUserInfo();
        ResultDTO<BatchGatheringDTO> resultDTO = createBatchGatheringDTO(createQrcodeVO, appUserLoginInfo);
        if (resultDTO.isUnSuccess()) {
            log.info("批量收款异常。错误原因 : {}", resultDTO.getErrorMsg());
            return ResultDTO.failed(resultDTO.getErrorMsg());
        }
        BatchGatheringDTO batchGatheringDTO = resultDTO.getModel();
        // 查明细只是为了拿到具体的运单号，给到paybuss那边的备注做记录而已
        ResultDTO<List<BatchGatheringDetailDTO>> detailResult = batchGatheringDetailService
                .queryByBatchGatheringNo(batchGatheringDTO.getBatchGatheringNo());
        if (detailResult.isUnSuccess()) {
            log.error("批量收款异常。错误原因 : {}", detailResult.getErrorMsg());
            return ResultDTO.failed(detailResult.getErrorMsg());
        }
        Set<String> transportOrderNos = detailResult.getModel().stream()
                .map(BatchGatheringDetailDTO::getTransportOrderNo).collect(Collectors.toSet());
        // 调用财务支付接口
        GatheringDTO gatheringDTO = GatheringDTO.builder()
                .customerIp(getIpAddress())
                .orderType(CarrierOrderTypeEnum.NORMAL.getValue())
                .payOrderId(batchGatheringDTO.getId())
                .payOrderNo(batchGatheringDTO.getBatchGatheringNo())
                .payChannel(Channel.ALLIN)
                .platform(createQrcodeVO.getPlatform())
                .trxAmount(batchGatheringDTO.getTrxAmount())
                .payOrderType(PayOrderTypeEnum.LOGISTICS.getValue())
                .subject(String.format("批量收款运单 : %s", transportOrderNos))
                .build();
        ResultDTO<PayResultDTO> payResultDTO = pay2Service.pay(getPayDTO(gatheringDTO, appUserLoginInfo));
        if (payResultDTO.isUnSuccess()) {
            log.error("GatheringController##createQrcodeByBatch error : 调用预支付接口失败，请联系资金流同事查看原因。payResultDTO = {}", payResultDTO);
            return ResultDTO.failed(payResultDTO.getErrorMsg());
        }
        CreateQrcodeResultVO createQrcodeResultVO = CreateQrcodeResultVO.builder().build();
        BeanUtils.copyProperties(payResultDTO.getModel(), createQrcodeResultVO);
        return ResultDTO.succeedWith(createQrcodeResultVO);
    }

    private ResultDTO<BatchGatheringDTO> createBatchGatheringDTO(CreateQrcodeVO createQrcodeVO, AppUserLoginInfoDTO appUserLoginInfo) {
        BatchGatheringDTO batchGatheringDTO = BatchGatheringDTO.builder()
                .status(BatchGatheringStatusEnum.UNPAY.getType())
                .build();
        batchGatheringDTO.setCreateUserId(appUserLoginInfo.getUserId());
        batchGatheringDTO.setCreateTime(new Date());
        return batchGatheringService.create(batchGatheringDTO, createQrcodeVO.getTransportOrderNos());
    }

    /**
     * 根据gatheringDTO返回pay2Service.pay的入参
     */
    private PayDTO getPayDTO(GatheringDTO gatheringDTO, AppUserLoginInfoDTO appUserLoginInfo) {
        ResultDTO<ServicePointDTO> resultDTO = servicePointService.queryServicePointById(appUserLoginInfo.getServicePointId());
        String packSuffix = CarrierOrderTypeEnum.NORMAL.getValue().equals(gatheringDTO.getOrderType()) ?
                AppConstant.TRANSPORT_ORDER_IDENTIFY : AppConstant.FERRY_ORDER_IDENTIFY;
        log.info("GatheringController##getPayDTO : packSuffix = {}", packSuffix);
        return PayDTO.builder()
                .customerIp(gatheringDTO.getCustomerIp())
                .carrierOrderType(gatheringDTO.getOrderType())
                .payOrderId(gatheringDTO.getPayOrderId())
                .payOrderNo(gatheringDTO.getPayOrderNo())
                .payChannel(gatheringDTO.getPayChannel())
                .platform(gatheringDTO.getPlatform())
                .trxAmount(gatheringDTO.getTrxAmount())
                .payOrderType(gatheringDTO.getPayOrderType())
                .packSuffix(packSuffix)
                .payerName(appUserLoginInfo.getUsername())
                .subject(gatheringDTO.getSubject())
                .payOrderBusinessType(CarrierPayTypeEnum.CASH_IMMEDIATELY.getValue())
                .regionId(resultDTO.getModel().getRegionId())
                .build();
    }

    /**
     * 校验请求预支付接口入参是否正确
     * @param gatheringDTO 请求预支付接口入参
     */
    private ResultDTO validateRequestParam(GatheringDTO gatheringDTO) {
        if (null == gatheringDTO) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "入参不能为空");
        }
        if (StringUtils.isBlank(gatheringDTO.getPayOrderNo())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "运单号/摆渡单号不能为空");
        }
        if (null == gatheringDTO.getPayOrderId() || gatheringDTO.getPayOrderId().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "运单id/摆渡单id不能为空");
        }
        if (null == gatheringDTO.getOrderType()) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "运单类型不能为空");
        }
        if (null == gatheringDTO.getPayChannel() || gatheringDTO.getPayChannel().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "支付渠道不能为空");
        }
        if (StringUtils.isBlank(gatheringDTO.getCustomerIp())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "付款ip不能为空");
        }
        if (null == gatheringDTO.getTrxAmount() || gatheringDTO.getTrxAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "交易金额不能为空");
        }
        if (null == gatheringDTO.getPlatform() || gatheringDTO.getPlatform().equals(0)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "支付具体下单终端类型不能为空");
        }
        if (null == gatheringDTO.getPayOrderType() || gatheringDTO.getPayOrderType().equals(0)) {
            gatheringDTO.setPayOrderType(PAY_ORDER_TYPE);
        }
        return ResultDTO.succeedWith(null, AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

}
