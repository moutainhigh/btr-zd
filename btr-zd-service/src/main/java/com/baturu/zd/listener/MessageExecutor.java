package com.baturu.zd.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.baturu.message.Message;
import com.baturu.message.consumer.TopicMessageExecutor;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.paybuss.pay2.constants.CarrierOrderTypeEnum;
import com.baturu.paybuss.pay2.constants.CarrierPayTypeEnum;
import com.baturu.paybuss.pay2.constants.PayOrderStatusEnum;
import com.baturu.paybuss.pay2.dto.PayCarrierOrderDTO;
import com.baturu.paybuss.pay2.dto.PayCarrierOrderNotifyDTO;
import com.baturu.paybuss.pay2.dto.PayCarrierOrderQueryParam;
import com.baturu.paybuss.pay2.service.PayCarrierOrderQueryService;
import com.baturu.tms.api.constant.transport.PayStateEnum;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.FerryOrderConstant;
import com.baturu.zd.dto.FerryOrderDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.dto.common.BatchGatheringDetailDTO;
import com.baturu.zd.dto.common.UserDTO;
import com.baturu.zd.enums.GatheringStatusEnum;
import com.baturu.zd.enums.TransportOrderStateEnum;
import com.baturu.zd.service.business.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订阅运单支付消息情况。
 * @author CaiZhuliang
 * @since 2019-3-26
 */
@Slf4j
@Component
public class MessageExecutor implements TopicMessageExecutor {

    @Autowired
    private TransportOrderService transportOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private FerryOrderService ferryOrderService;
    @Reference(check = false)
    private PayCarrierOrderQueryService payCarrierOrderQueryService;
    @Autowired
    private SyncTmsTransportOrderServiceImpl syncTmsTransportOrderService;
    @Autowired
    private BatchGatheringService batchGatheringService;

    @Override
    public String getTopic() {
        return AppConstant.LOGISTICS_APP_PAY_SUCCESS;
    }

    /**
     * 异步处理消息的线程池，最大并发8条
     */
    private ThreadPoolExecutor messageExecutor = new ThreadPoolExecutor(2, 4, 2, TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(), new ThreadFactoryBuilder().setNameFormat("messageExecutor-pool-%d").build());

    @Override
    public ThreadPoolExecutor getConsumerThreadPool() {
        return this.messageExecutor;
    }

    @Override
    public void execute(Message message) {
        log.info("**************************************MessageExecutor start********************************************");
        String content = ObjectUtils.toString(message.getContent());
        if (StringUtils.isBlank(content)) {
            log.error("MessageExecuto##execute : 消息内容为空。message = {}", message);
            return;
        }
        PayCarrierOrderNotifyDTO payCarrierOrderNotifyDTO = JSON.parseObject(ObjectUtils.toString(message.getContent()), PayCarrierOrderNotifyDTO.class);
        log.info("MessageExecuto##execute payCarrierOrderNotifyDTO = {}", payCarrierOrderNotifyDTO);
        if (CarrierOrderTypeEnum.FERRY.getValue().equals(payCarrierOrderNotifyDTO.getOrderType())) {
            dealWithFerryOrderPayMessage(payCarrierOrderNotifyDTO);
        } else if (payCarrierOrderNotifyDTO.getCarrierNo().startsWith("W")) {
            dealWithTransportOrderPayMessage(payCarrierOrderNotifyDTO);
        } else {
            // 批量收款，后面财务改造支付平台接口，可以指定消息就不用通过类型、前缀来区分.2019-10-17
            dealWithBatchGatheringPayMessage(payCarrierOrderNotifyDTO);
        }
        log.info("**************************************MessageExecutor end********************************************");
    }

    /**
     * 处理批量收款消息
     * @param payCarrierOrderNotifyDTO 支付平台收钱后推送的消息
     */
    private void dealWithBatchGatheringPayMessage(PayCarrierOrderNotifyDTO payCarrierOrderNotifyDTO) {
        String batchGatheringNo = payCarrierOrderNotifyDTO.getCarrierNo();
        if (StringUtils.isBlank(batchGatheringNo)) {
            log.error("MessageExecuto##dealWithBatchGathering : 消息没有返回流水号");
            return;
        }
        ResultDTO<List<BatchGatheringDetailDTO>> updateResult = batchGatheringService.updateStatusByBatchGatheringNo(batchGatheringNo);
        if (updateResult.isUnSuccess()) {
            log.info("MessageExecuto##dealWithBatchGathering : 批量支付更新支付状态失败。queryBatchResult = {}", updateResult);
            return;
        }
        List<String> transportOrderNos = updateResult.getModel().stream().map(BatchGatheringDetailDTO::getTransportOrderNo).collect(Collectors.toList());
        ResultDTO<List<TransportOrderDTO>> queryTransportOrderDTOs = transportOrderService.queryTransportOrders(transportOrderNos);
        if (queryTransportOrderDTOs.isUnSuccess()) {
            log.info("MessageExecuto##dealWithBatchGathering : 查询运单信息失败。queryTransportOrderDTOs = {}", queryTransportOrderDTOs);
            return;
        }
        List<TransportOrderDTO> transportOrderDTOs = queryTransportOrderDTOs.getModel();
        for (TransportOrderDTO transportOrderDTO : transportOrderDTOs) {
            transportOrderDTO.setGatheringStatus(GatheringStatusEnum.PAID.getType());
            if (TransportOrderStateEnum.CHECKED.getType().equals(transportOrderDTO.getState())) {
                // 已验收状态的运单修改支付状态和运单状态
                transportOrderDTO.setState(TransportOrderStateEnum.EXPRESSED.getType());
            }
            ResultDTO resultDTO = transportOrderService.updateTransportOrder(transportOrderDTO, null);
            if (resultDTO.isUnSuccess()) {
                log.error("MessageExecuto##dealWithTransportOrderPayMessage : 更新运单收款状态失败.transportOrderNo = {}, resultDTO = {}",
                        transportOrderDTO.getTransportOrderNo(), resultDTO);
                continue;
            }
            syncTmsTransportOrder(payCarrierOrderNotifyDTO.getScanner(), transportOrderDTO.getTransportOrderNo());
        }
    }

    /**
     * 同步tms运单
     * @param username 用户名
     * @param transportOrderNo 运单号
     */
    private void syncTmsTransportOrder(String username, String transportOrderNo) {
        try {
            com.baturu.tms.api.dto.transport.TransportOrderDTO tmsTransportOrder = com.baturu.tms.api.dto.transport.TransportOrderDTO.builder()
                    .orderNo(transportOrderNo)
                    .updateUserName(username)
                    .build();
            ResultDTO<UserDTO> userDTOResultDTO = userService.queryUserByUsername(username);
            if (userDTOResultDTO.isSuccess() && userDTOResultDTO.getModel() != null) {
                tmsTransportOrder.setUpdateUserId(userDTOResultDTO.getModel().getId().longValue());
            }
            tmsTransportOrder.setPayStatus(PayStateEnum.ALREADY_PAY.getType());
            ResultDTO result = syncTmsTransportOrderService.syncTmsTransportOrder(tmsTransportOrder);
            if (result.isUnSuccess()) {
                log.error("运单更新支付状态 - 同步tms运单信息失败。错误原因 : {}", result.getErrorMsg());
            }
        } catch (Exception e) {
            log.error("运单更新支付状态 - 同步tms运单信息异常。", e);
        }
    }

    /**
     * 处理运单收款后返回的消息
     * @param payCarrierOrderNotifyDTO 支付平台收钱后推送的消息
     */
    private void dealWithTransportOrderPayMessage(PayCarrierOrderNotifyDTO payCarrierOrderNotifyDTO) {
        String transportOrderNo = payCarrierOrderNotifyDTO.getCarrierNo();
        if (StringUtils.isBlank(transportOrderNo)) {
            log.error("MessageExecuto##dealWithTransportOrderPayMessage : 消息没有返回运单号。payCarrierOrderNotifyDTO = {}", payCarrierOrderNotifyDTO);
            return;
        }
        ResultDTO<TransportOrderDTO> orderDTOResultDTO = transportOrderService.queryTransportOrdersByTransportOrderNo(transportOrderNo);
        if (orderDTOResultDTO.isUnSuccess()) {
            log.info("MessageExecuto##dealWithTransportOrderPayMessage : 查询运单失败。transportOrderNo = {}, orderDTOResultDTO = {}",
                    transportOrderNo, orderDTOResultDTO);
            return;
        }
        // 如果接口调用成功，transportOrderDTO不可能为空。目前的接口逻辑是这样实现的
        TransportOrderDTO transportOrderDTO = orderDTOResultDTO.getModel();
        // 不是分摊收费，那么收到钱就改收款状态
        Integer gatheringStatus = GatheringStatusEnum.PAID.getType();
        if (AppConstant.PAY_TYPE_SHARE.equals(transportOrderDTO.getPayType())) {
            GatheringStatusEnum gatheringStatusEnum = isPay(transportOrderDTO);
            gatheringStatus = gatheringStatusEnum.getType();
        }
        transportOrderDTO.setGatheringStatus(gatheringStatus);
        log.info("MessageExecuto##dealWithTransportOrderPayMessage transportOrderDTO = {}", transportOrderDTO);
        ResultDTO resultDTO = transportOrderService.updateTransportOrder(transportOrderDTO, null);
        if (resultDTO.isUnSuccess()) {
            log.error("MessageExecuto##dealWithTransportOrderPayMessage : 更新运单收款状态失败.transportOrderNo = {}, resultDTO = {}", transportOrderNo, resultDTO);
        }
        // 已支付，同步tms
        if (GatheringStatusEnum.PAID.getType().equals(gatheringStatus)) {
            syncTmsTransportOrder(payCarrierOrderNotifyDTO.getScanner(), transportOrderNo);
        }
    }

    /**
     * 判断分摊收费的运单是否收到了全部的钱。分摊收费要全部收了才改收款状态
     * @param transportOrderDTO 运单信息
     * @return Boolean 全部收了返回true,否则返回false
     */
    private GatheringStatusEnum isPay(TransportOrderDTO transportOrderDTO) {
        String transportOrderNo = transportOrderDTO.getTransportOrderNo();
        Boolean nowPay = checkPay(transportOrderNo, CarrierPayTypeEnum.CASH_IMMEDIATELY.getValue());
        if (nowPay && transportOrderDTO.getTotalPayment().compareTo(transportOrderDTO.getNowPayment()) == 0) {
            return GatheringStatusEnum.PAID;
        }
        Boolean arrivePay = checkPay(transportOrderNo, CarrierPayTypeEnum.CASH_ON_DELIVERY.getValue());
        if (arrivePay && transportOrderDTO.getTotalPayment().compareTo(transportOrderDTO.getArrivePayment()) == 0) {
            return GatheringStatusEnum.PAID;
        }
        if (nowPay && arrivePay) {
            return GatheringStatusEnum.PAID;
        } else if (nowPay) {
            return GatheringStatusEnum.NOW_PREPAID;
        } else if (arrivePay) {
            return GatheringStatusEnum.ARRIVE_PREPAID;
        } else {
            return GatheringStatusEnum.UNPAY;
        }
    }

    /**
     * 根据支付方式+运单号查询支付结果
     * @param transportOrderNo 运单号
     * @param payType 支付方式
     * @return 支付成功返回true
     */
    private Boolean checkPay(String transportOrderNo, Integer payType) {
        ResultDTO<PayCarrierOrderDTO> payResultDTO = payCarrierOrderQueryService
                .findPayCarrierOrderDTOByParam(PayCarrierOrderQueryParam.builder().carrierType(payType).carrierNo(transportOrderNo).build());
        if (payResultDTO.isUnSuccess()) {
            log.info("MessageExecuto##checkPay : 调用查询运单支付记录接口失败。payType = {}, transportOrderNo = {}, payResultDTO = {}",
                    payType, transportOrderNo, payResultDTO);
            return Boolean.FALSE;
        }
        PayCarrierOrderDTO payCarrierOrderDTO = payResultDTO.getModel();
        if (null == payCarrierOrderDTO || !PayOrderStatusEnum.PAY_SUCCESS.getValue().equals(payCarrierOrderDTO.getPayStatus())) {
            log.info("MessageExecuto##checkPay : 【{}】还没完成付款不修改收款状态", transportOrderNo);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 处理摆渡单收款后返回的消息
     * @param payCarrierOrderNotifyDTO 支付平台收钱后推送的消息
     */
    private void dealWithFerryOrderPayMessage(PayCarrierOrderNotifyDTO payCarrierOrderNotifyDTO) {
        String ferryOrderNo = payCarrierOrderNotifyDTO.getCarrierNo();
        if (StringUtils.isBlank(ferryOrderNo)) {
            log.error("MessageExecuto##dealWithFerryOrderPayMessage : 消息没有返回摆渡单号。payCarrierOrderNotifyDTO = {}", payCarrierOrderNotifyDTO);
            return;
        }
        FerryOrderDTO ferryOrderDTO = FerryOrderDTO.builder()
                .ferryNo(ferryOrderNo)
                .payState(FerryOrderConstant.FINISH_PAYED)
                .build();

        ResultDTO<FerryOrderDTO> resultDTO = ferryOrderService.updateState(ferryOrderDTO);
        if (resultDTO.isUnSuccess()) {
            log.info("MessageExecuto##dealWithFerryOrderPayMessage : 更新摆渡单收款状态失败。ferryOrderDTO = {}, resultDTO = {}", ferryOrderNo, resultDTO);
        }
    }
}
