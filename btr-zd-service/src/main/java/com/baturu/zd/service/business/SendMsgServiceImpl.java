package com.baturu.zd.service.business;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.baturu.message.producer.Producer;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.paybuss.pay2.constants.CarrierOrderTypeEnum;
import com.baturu.paybuss.pay2.constants.CarrierPayTypeEnum;
import com.baturu.paybuss.pay2.dto.PayCarrierOrderNotifyDTO;
import com.baturu.tms.api.constant.sorting.BookCaseStateEnum;
import com.baturu.tms.api.constant.sorting.OrderTypeEnum;
import com.baturu.tms.api.constant.sorting.PackageTypeEnum;
import com.baturu.tms.api.constant.transport.*;
import com.baturu.tms.api.dto.outside.TransportOrderCreateMessageDTO;
import com.baturu.tms.api.dto.sorting.PackageRouteDTO;
import com.baturu.tms.api.dto.transport.*;
import com.baturu.tms.api.request.sorting.BtlTransportParam;
import com.baturu.tms.api.request.sorting.TransportQueryRequest;
import com.baturu.tms.api.request.transport.TransportPointParam;
import com.baturu.tms.api.service.business.transport.TransportBizService;
import com.baturu.tms.api.service.business.transport.TransportOrderBizService;
import com.baturu.tms.api.topic.InnerTopicType;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.TransLineDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.enums.DeliveryTypeEnum;
import com.baturu.zd.enums.PayTypeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by caizhuliang on 2019/4/3.
 */
@Service("sendMsgService")
@Slf4j
public class SendMsgServiceImpl implements SendMsgService {

    @Autowired(required = false)
    private Producer producer;
//    @Reference(check = false)
//    private WarehouseService warehouseService;
    @Reference(check = false)
    private TransportBizService transportBizService;
    @Reference(check = false)
    private TransportOrderBizService transportOrderBizService;

    @Override
    public ResultDTO<String> sendMsg(String payOrderNo) {
        Object payCarrierOrderNotifyDTO = JSON.toJSON(PayCarrierOrderNotifyDTO.builder()
                .carrierNo(payOrderNo)
                .carrierPayType(CarrierPayTypeEnum.CASH_ON_DELIVERY.getValue())
                .orderType(CarrierOrderTypeEnum.NORMAL.getValue())
                .build());
        producer.send(AppConstant.LOGISTICS_APP_PAY_SUCCESS, payCarrierOrderNotifyDTO);
        return ResultDTO.successfy("success");
    }

    /**
     * 异步推送运单和包裹数据到TMS
     *
     * @return
     */
    @Override
    @Async("taskExecutor")
    public void sendTransportOrderAndPackageMessageToTMS(TransportOrderDTO transportOrderDTO, List<PackageDTO> packageDTOS, TransLineDTO transLineDTO) {
        if (null == transLineDTO) {
            log.error("推送包裹数据到TMS，运单的路线信息为空，运单：【{}】", transportOrderDTO);
            return;
        }
        //1，封装获取运单数据
        ResultDTO<TransportOrderCreateMessageDTO> transportOrderMessage = this.getTransportOrderMessage(transportOrderDTO, transLineDTO);
        if (transportOrderMessage.isUnSuccess()) {
            return;
        }
        //2，封装获取包裹数据
        ResultDTO<List<TransportPackageDTO>> listResultDTO = this.getTransportPackageMessage(transportOrderDTO, packageDTOS, transLineDTO);
        if (listResultDTO.isUnSuccess()) {
            return;
        }
        //3，推送运单和包裹数据
        this.sendToTms(transportOrderMessage.getModel(), listResultDTO.getModel());
    }

    private void sendToTms(TransportOrderCreateMessageDTO transportOrderCreateMessageDTO, List<TransportPackageDTO> transportPackageDTOS) {
        try {
            Object transportOrderCreateMessageDTOSObject = JSON.toJSON(transportOrderCreateMessageDTO);
            log.info("推送运单数据到TMS【{}】", transportOrderCreateMessageDTOSObject);
            producer.send(InnerTopicType.TRANSPORT_ORDER_CREATE_MESSAGE, transportOrderCreateMessageDTOSObject);
            Integer queryTimes = 0;
            while (true) {
                Thread.sleep(1000);
                ResultDTO<List<com.baturu.tms.api.dto.transport.TransportOrderDTO>> listResultDTO = transportOrderBizService
                        .queryTransportOrderByOrderNos(com.google.common.collect.Sets.newHashSet(transportOrderCreateMessageDTO.getTransportOrderDTO().getOrderNo()));
                log.info("运单【{}】查询结果：{}", transportOrderCreateMessageDTO.getTransportOrderDTO().getOrderNo(), listResultDTO);
                if (listResultDTO.isSuccess() || CollectionUtils.isNotEmpty(listResultDTO.getModel())) {
                    log.info("tms运单同步后查询结果：{}:{}", transportOrderCreateMessageDTO.getTransportOrderDTO().getOrderNo(), listResultDTO.getModel());
                    transportPackageDTOS.stream().forEach(p -> {
                        p.setTransportOrderId(listResultDTO.getModel().get(0).getTransportOrderId());
                        p.getPackageRouteDTOs().stream().forEach(r -> r.setTransportOrderId(listResultDTO.getModel().get(0).getTransportOrderId()));
                    });
                    break;
                } else if (queryTimes > 4) {//查询五次
                    break;
                }
                queryTimes++;
                Thread.sleep(2000);
            }
            transportPackageDTOS.forEach(t -> {
                Object packageObject = JSON.toJSON(t);
                log.info("推送包裹数据到TMS【{}】", packageObject);
                producer.send(InnerTopicType.TRANSPORT_PACKAGE_CREATE_MESSAGE, packageObject);
            });
            log.info(transportOrderCreateMessageDTO.getTransportOrderDTO().getOrderNo() + "推送运单和包裹数据到TMS完成！");
        } catch (ClassCastException e) {
            log.error(transportOrderCreateMessageDTO.getTransportOrderDTO().getOrderNo() + "推送运单和包裹数据到TMS数据转换失败", e);
        } catch (Exception e) {
            log.error(transportOrderCreateMessageDTO.getTransportOrderDTO().getOrderNo() + "推送运单和包裹数据到TMS失败", e);
        }
    }

    /**
     * 封装获取需要推送的包裹
     *
     * @param transportOrderDTO
     * @return
     */
    private ResultDTO<List<TransportPackageDTO>> getTransportPackageMessage(TransportOrderDTO transportOrderDTO, List<PackageDTO> packageDTOS, TransLineDTO transLineDTO) {
        //路由数据获取
        ResultDTO<List<PackageRouteDTO>> resultDTO = this.initPackageRoute(transLineDTO, transportOrderDTO);
        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed(resultDTO.getErrorMsg());
        }
        List<PackageRouteDTO> routeDTOS = resultDTO.getModel();
        List<TransportPackageDTO> transportPackageDTOS = new ArrayList<>();
        PackageRouteDTO firstRoute = routeDTOS.get(0);
        PackageRouteDTO lastRoute = routeDTOS.get(routeDTOS.size() - 1);
        packageDTOS.forEach(p -> {
            Set<PackageRouteDTO> routes = Sets.newHashSet();
            BigDecimal bulk = p.getBulk() == null ? BigDecimal.ZERO : p.getBulk();
            BigDecimal bulkInCM3 = bulk.multiply(new BigDecimal("1000000"));
            routeDTOS.stream().forEach(r -> {
                PackageRouteDTO routeDTO = new PackageRouteDTO();
                BeanUtils.copyProperties(r, routeDTO);
                routeDTO.setPackageNo(p.getPackageNo());
                routes.add(routeDTO);
            });
            TransportPackageDTO transportPackageDTO = TransportPackageDTO
                    .builder()
                    .id(p.getId())
                    .packageNo(p.getPackageNo())
//                    .transportOrderId(transportOrderDTO.getTransportOrderNo())
                    .orderNo(transportOrderDTO.getTransportOrderNo())
                    .orderType(OrderTypeEnum.OUTER_ORDER)
                    //供应商id
                    //.supplierId()
                    //供应商订单id
                    //.supplierOrderId()
                    .status(TransportPackageStateEnum.PACKED.getType())
                    .bulk(bulkInCM3)
                    .weight(p.getWeight())
                    //todo update by pengdi ，丽娟说新包裹是2.0的东西，仓库和仓位都应该用嘉杰新接口
                    .cityId(firstRoute.getCityId())
                    .cityName(firstRoute.getCityName())
                    .fromWarehouseId(firstRoute.getFromWarehouseId())
                    .fromWarehouseName(firstRoute.getFromWarehouseName())
                    .warehouseId(firstRoute.getFromWarehouseId())
                    .warehouseName(firstRoute.getFromWarehouseName())
                    //仓库直达收货点，取起始仓，调拨取目的仓
                    .toWarehouseId(lastRoute.getFromWarehouseId())
                    .toWarehouseName(lastRoute.getFromWarehouseName())
                    //包裹发货时间(供应商打包时间，中心仓装载时间
                    //.packageLoadingTime()
                    //抽检状态  -2-不需要抽检,-1：抽检不通过,0:通过, 1 待抽检 2 免抽检
                    // .inspectionState()
                    // 是否需要抽检 0:不需要，1：需要
                    //.needInspect()
                    //钉箱状态：-2-不需要钉箱 -1-已取消,0-待钉箱,1-已钉箱,
                    .bookcaseState((p.getIsNailBox()!=null&&p.getIsNailBox())?BookCaseStateEnum.WAIT_TO_BOOKCASE.getType():BookCaseStateEnum.NO_NEED_BOOKCASE.getType())
                    .needBookcase(p.getIsNailBox())
                    .collectionState(false)
                    .receivingPointId(transLineDTO.getBizId())
                    .receivingPointName(transLineDTO.getBizName())
                    .partnerId(transLineDTO.getPartnerId())
                    .partnerName(transLineDTO.getPartnerName())
                    .active(1)
                    .createTime(transportOrderDTO.getCreateTime())
                    //.updateUserId()
                    //.updateUserName()
                    //.updateTime()
                    .packageType(PackageTypeEnum.OUTER)
                    //线下单类型
                    //.offlineOrderTypeEnum()
                    //包裹明细(取消时使用：全部取消时，此集合为空;部分取消时此集合才需要使用)
                    //.transportPackageDetailDTOs()
                    //路由信息
                    .packageRouteDTOs(routes)
                    .build();
            transportPackageDTOS.add(transportPackageDTO);
        });
        return ResultDTO.successfy(transportPackageDTOS);
    }

    /**
     * 封装获取需要推送的运单
     *
     * @param transportOrderDTO
     * @return
     */
    private ResultDTO<TransportOrderCreateMessageDTO> getTransportOrderMessage(TransportOrderDTO transportOrderDTO, TransLineDTO transLineDTO) {
        // 判断送货方式
        Integer shippingMethod = null;
        if (DeliveryTypeEnum.SELF.getType().equals(transportOrderDTO.getDeliveryType())) {
            shippingMethod = ShippingMethodTypeEnum.SELFPICK.getType();
        } else if (DeliveryTypeEnum.HOME.getType().equals(transportOrderDTO.getDeliveryType())) {
            shippingMethod = ShippingMethodTypeEnum.DELIVERYTODOOR.getType();
        } else {
            log.error("推送运单数据到TMS，运单的送货方式为空，运单：【{}】", transportOrderDTO);
            return ResultDTO.failed("推送运单数据到TMS，运单的送货方式为空");
        }
        // 判断付款方式
        Integer payMethod = null;
        if (PayTypeEnum.NOW.getType().equals(transportOrderDTO.getPayType())) {
            payMethod = PayMethodStateEnum.CASH_PAYMENT.getType();
        } else if (PayTypeEnum.ARRIVE.getType().equals(transportOrderDTO.getPayType())) {
            payMethod = PayMethodStateEnum.WAIT_TO_PAY.getType();
        } else if (PayTypeEnum.DISCONFIG.getType().equals(transportOrderDTO.getPayType())) {
            payMethod = PayMethodStateEnum.ALLOCATED_EXPENSE.getType();
        } else {
            log.error("推送运单数据到TMS，运单的付款方式为空，运单：【{}】", transportOrderDTO);
            return ResultDTO.failed("推送运单数据到TMS，运单的送货方式为空");
        }
        //体积单位转换
        BigDecimal bulk = transportOrderDTO.getBulk() == null ? BigDecimal.ZERO : transportOrderDTO.getBulk();
        BigDecimal bulkInCM3 = bulk.multiply(new BigDecimal("1000000"));
        // 判断是否代收
        Boolean collectionOnDelivery = false;
        if (null != transportOrderDTO.getCollectAmount() && transportOrderDTO.getCollectAmount().compareTo(BigDecimal.ZERO) > 0) {
            collectionOnDelivery = true;
        }
        com.baturu.tms.api.dto.transport.TransportOrderDTO transportOrderTMSDTO = com.baturu.tms.api.dto.transport.TransportOrderDTO
                .builder()
                .transportOrderId(transportOrderDTO.getTransportOrderNo())
                .orderId(transportOrderDTO.getId())
                .orderNo(transportOrderDTO.getTransportOrderNo())
                .transportChannel(OrderTypeEnum.OUTER_ORDER.getType())
                //运单来源组织id
                //.fromOrgId()
                .fromOrgName(AppConstant.SYSTEM_NAME)
                //运费计算记录表ID
                //.freightCalRecordId()
                .volFreight(transportOrderDTO.getFreight())
                .transportState(com.baturu.tms.api.constant.transport.TransportOrderStateEnum.PACKED.getType())
                .shippingMethod(shippingMethod)
                .receivingPointId(transLineDTO.getBizId())
                .receivingPointName(transLineDTO.getBizName())
                .partnerId(transLineDTO.getPartnerId())
                .partnerName(transLineDTO.getPartnerName())
                .senderName(transportOrderDTO.getSenderAddr().getName())
                .senderPhone(transportOrderDTO.getSenderAddr().getPhone())
                .recipientsName(transportOrderDTO.getRecipientAddr().getName())
                .recipientsPhone(transportOrderDTO.getRecipientAddr().getPhone())
                .provinceId(transportOrderDTO.getRecipientAddr().getProvinceId())
                .countyId(transportOrderDTO.getRecipientAddr().getCountyId())
                .townId(transportOrderDTO.getRecipientAddr().getTownId())
                .cityId(transportOrderDTO.getRecipientAddr().getCityId())
                .address((transportOrderDTO.getRecipientAddr().getProvinceName()+transportOrderDTO.getRecipientAddr().getCityName()+
                        transportOrderDTO.getRecipientAddr().getCountyName()+transportOrderDTO.getRecipientAddr().getTownName()+transportOrderDTO.getRecipientAddr().getAddress()))
                .packageNum(transportOrderDTO.getQty())
                .bulk(bulkInCM3)
                .weight(transportOrderDTO.getWeight())
                .payMethod(payMethod)
                .payStatus(PayStateEnum.WAIT_TO_PAY.getType())
                .collectionOnDelivery(collectionOnDelivery)
                .active(transportOrderDTO.getActive())
                .remark(transportOrderDTO.getRemark())
                .createUserId(transportOrderDTO.getCreateUserId().longValue())
                .createUserName(transportOrderDTO.getCreateUserName())
                .createTime(transportOrderDTO.getCreateTime())
                // .updateUserId()
                //.updateUserName()
                // .updateTime()
                //运单明细信息 逐道物流没有运单的明细信息
                //.transportOrderDetailDTOs()
                .build();
        //封装消息数据
        TransportOrderCreateMessageDTO transportOrderCreateMessageDTO = TransportOrderCreateMessageDTO
                .builder()
                .orderNo(transportOrderDTO.getTransportOrderNo())
                .orderTypeEnum(OrderTypeEnum.OUTER_ORDER)
                .transportOrderDTO(transportOrderTMSDTO)
                .build();
        return ResultDTO.successfy(transportOrderCreateMessageDTO);
    }

    private ResultDTO<List<PackageRouteDTO>> initPackageRoute(TransLineDTO transLineDTO, TransportOrderDTO transportOrderDTO) {
        if (this.numIsValid(transLineDTO.getBizId()) && this.numIsValid(transportOrderDTO.getRecipientAddr().getProvinceId()) && this.numIsValid(transportOrderDTO.getRecipientAddr().getCityId())
                && this.numIsValid(transportOrderDTO.getRecipientAddr().getCountyId()) && this.numIsValid(transportOrderDTO.getRecipientAddr().getTownId())) {
            // 嘉杰说tms线上单的路线查询较安全，外部运单的没测试过
            TransportQueryRequest transportQueryRequest = TransportQueryRequest.builder()
                    .transportType(TransportTypeEnum.BTL_ONLINE.getType())
                    .destinationTransportPointParam(TransportPointParam.builder()
                            .pointId(transLineDTO.getBizId())
                            .pointName(transLineDTO.getBizName())
                            .provinceID(transLineDTO.getProvinceId())
                            .cityID(transLineDTO.getCityId())
                            .countyID(transLineDTO.getCountyId())
                            .townId(transLineDTO.getTownId()).build())
                    .originTransportPointParam(TransportPointParam.builder().build())
                    .btlTransportParam(BtlTransportParam.builder().supplierId(0).fromWarehouseCode(transLineDTO.getFirstWarehouseCode()).build()).build();
            ResultDTO<TransportInfoDTO> resultDTO = transportBizService.queryTransportLine(transportQueryRequest);
            if (resultDTO.isUnSuccess() || resultDTO.getModel() == null) {
                log.info("逐道运单信息同步路由信息获取失败，{}，{}", transportQueryRequest, resultDTO.getErrorMsg());
                return ResultDTO.failed(resultDTO.getErrorMsg());
            }
            TransportInfoDTO transportInfoDTO = resultDTO.getModel();
            if (CollectionUtils.isEmpty(transportInfoDTO.getTransportLineDTOS())) {
                log.info("查询路线信息为空：{}", transLineDTO.getTransportOrderNo());
                return ResultDTO.failed("查询路线信息为空");
            }
            List<PackageRouteDTO> routes = Lists.newArrayList();
            Integer parentId = 0;
            for (TransportLineDTO t : transportInfoDTO.getTransportLineDTOS()) {
                if (CollectionUtils.isEmpty(t.getLineDestinationDTOS())) {
                    log.info("查询路线信息目的信息为空-》{}：{}", t, transLineDTO.getTransportOrderNo());
                    return ResultDTO.failed("查询路线信息目的信息为空");
                }
                //嘉杰说目前默认每段只有一个
                LineDestinationDTO destinationDTO = t.getLineDestinationDTOS().get(0);
                PackageRouteDTO packageRouteDTO = PackageRouteDTO.builder()
                        .active(Boolean.TRUE)
                        .fromWarehouseId(t.getWarehouseId())
                        .fromWarehouseName(t.getWarehouseName())
                        .toBizType(DestinationTypeEnum.WAREHOUSE.getType().equals(destinationDTO.getDestinationType()) ? 0 : 1)
                        .toBizId(destinationDTO.getDestinationId())
                        .toBizName(destinationDTO.getDestinationName())
                        .cityId(t.getCityId())
                        .cityName(t.getCityName())
                        .status(10)
                        .parent(parentId)
                        .build();
                if (CollectionUtils.isNotEmpty(t.getLineClassCarrierDTOS())) {
                    LineClassCarrierDTO classCarrierDTO = t.getLineClassCarrierDTOS().get(0);
                    packageRouteDTO.setCarrierId(classCarrierDTO.getCarrierId());
                    packageRouteDTO.setCarrierName(classCarrierDTO.getCarrierName());
                }
                parentId = t.getId();
                routes.add(packageRouteDTO);
            }
            log.info("【{}】：路由信息结果：{}", transLineDTO.getTransportOrderNo(), routes);
            return ResultDTO.succeedWith(routes);
        } else {
            log.info("路由查询参数检验不通过:{}", transLineDTO);
            return ResultDTO.failed("路由查询参数检验不通过");
        }
    }


    private Boolean numIsValid(Integer num) {
        if (num == null || num == 0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 获取仓位id
     *
     * @return
     */
    private Integer getCityId(TransLineDTO transLineDTO) {
        String cityCode = transLineDTO.getFirstPositionCode();
        if (StringUtils.isNotBlank(cityCode)) {
            try {
                if (cityCode.contains("*")) {
                    return Integer.parseInt(cityCode.split("8")[1]);
                } else if (!"null".equals(cityCode)) {
                    return Integer.parseInt(cityCode);
                }
            } catch (Exception e) {
                log.info("运单【" + transLineDTO.getTransportOrderNo() + "】仓位id【" + transLineDTO.getFirstPositionCode() + "】转换异常:", e);
            }
        }
        return 0;
    }

}
