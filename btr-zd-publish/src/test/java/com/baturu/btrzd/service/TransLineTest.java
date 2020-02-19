package com.baturu.btrzd.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.crm.api.dto.team.PrincipalUser;
import com.baturu.crm.api.dto.team.Team;
import com.baturu.crm.api.param.team.PrincipalUserQuery;
import com.baturu.crm.api.param.team.TeamQuery;
import com.baturu.crm.api.service.PrincipalUserRpcService;
import com.baturu.crm.api.service.TeamRpcService;
import com.baturu.logistics.api.dtos.sociTms.SocialDeliverDatasDTO;
import com.baturu.logistics.api.requestParam.SocialTmsQueryRequest;
import com.baturu.logistics.api.service.socialTms.SocialLogisticService;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.tms.api.constant.transport.TransportTypeEnum;
import com.baturu.tms.api.dto.sorting.warehouse.WarehouseDTO;
import com.baturu.tms.api.dto.transport.TransportInfoDTO;
import com.baturu.tms.api.request.sorting.BtlTransportParam;
import com.baturu.tms.api.request.sorting.TransportQueryRequest;
import com.baturu.tms.api.request.sorting.warehouse.WarehouseQueryRequest;
import com.baturu.tms.api.request.transport.TransportPointParam;
import com.baturu.tms.api.service.business.outer.PartnerServiceAreaService;
import com.baturu.tms.api.service.business.transport.TransportBizService;
import com.baturu.tms.api.service.business.transport.TransportOrderBizService;
import com.baturu.tms.api.service.common.sorting.warehouse.WarehouseService;
import com.baturu.zd.controller.web.TransportOrderController;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.TransLineDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.dto.wx.WxAddressSnapshotDTO;
import com.baturu.zd.enums.DeliveryTypeEnum;
import com.baturu.zd.enums.PayTypeEnum;
import com.baturu.zd.request.business.FerryFreightQueryRequest;
import com.baturu.zd.service.business.FerryFreightService;
import com.baturu.zd.service.business.SendMsgService;
import com.baturu.zd.service.common.TransLineService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * created by ketao by 2019/03/27
 **/
@Slf4j
public class TransLineTest extends BaseTest {

    @Autowired
    private TransLineService transLineService;

    @Reference(check = false,version = "1.0.0")
    private SocialLogisticService socialLogisticService;

    @Test
    public void testQueryTransLine(){
        SocialTmsQueryRequest socialTmsQueryRequest = SocialTmsQueryRequest.builder().warehouseCode("BTL_S_001")
                .provinceId(19).cityId(440600).countyId(1747).townId(440606006).build();
        ResultDTO<SocialDeliverDatasDTO> resultDTO = transLineService.queryTransLine(socialTmsQueryRequest);
        log.info("testQueryTransLine resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

    @Test
    public void m0(){
        TransLineDTO transLineDTO = TransLineDTO.builder().transportOrderNo("W20190327002").firstWarehouse("广州").firstWarehouseCode("BTL001").firstPosition("广州-汕头")
                .firstPositionCode("F1").bizId(1).bizName("汕头").partnerId(1).partnerName("王蛋").partnerPhone("13588888888").build();
        ResultDTO<TransLineDTO> resultDTO = transLineService.saveTransLine(transLineDTO);
        System.out.println(resultDTO.getModel());
    }


    @Test
    public void m5(){
        SocialTmsQueryRequest request = SocialTmsQueryRequest.builder().warehouseCode("BTL_S_001").provinceId(3).cityId(130700).countyId(2).townId(0).build();
        ResultDTO resultDTO = socialLogisticService.deliverDatas(request);
        Assert.assertTrue(resultDTO.isSuccess());
    }

    @Autowired
    private FerryFreightService ferryFreightService;
    @Test
    public void m2(){
        ferryFreightService.selectForPage(FerryFreightQueryRequest.builder().build());
    }

  /*  @Reference
    private LogisticsPackageQueryService logisticsPackageQueryService;
    @Test
    public void m3(){
        ResultDTO<PackageDTO> packageDTOResultDTO = logisticsPackageQueryService.queryByPackageId(1604656L, 1, PackageQueryFillingParam.builder().needOrderInfo(false).build());
        return;
    }*/

    @Reference
    private WarehouseService warehouseQueryService;
    @Test
    public void m4(){
        ResultDTO<List<WarehouseDTO>> resultDTO = warehouseQueryService
                .queryByParam(WarehouseQueryRequest.builder().regionIds(Sets.newHashSet(1)).build());
        return;
    }

    @Autowired
    private SendMsgService sendMsgService;

    @Test
    public void m6(){
        WxAddressSnapshotDTO se = WxAddressSnapshotDTO.builder().id(1817).name("测试下收件").phone("18836698652").build();
        WxAddressSnapshotDTO re = WxAddressSnapshotDTO.builder().id(1817).name("测试下寄件人").phone("13126398092").provinceId(1).cityId(110000).countyId(1).townId(110101001).build();
        TransportOrderDTO transportOrderDTO = TransportOrderDTO.builder().id(663).transportOrderNo("W905220000081").state(0).senderAddrSnapshotId(1818).recipientAddrSnapshotId(1817)
                .qty(1).payType(2).weight(BigDecimal.ZERO).bulk(BigDecimal.ZERO).freight(BigDecimal.ONE).deliveryType(DeliveryTypeEnum.HOME.getType())
                .payType(PayTypeEnum.ARRIVE.getType()).createUserId(8).createTime(new Date()).senderAddr(se).recipientAddr(re).build();
        TransLineDTO transLineDTO = TransLineDTO.builder().transportOrderNo("W905220000081").firstWarehouse("广州").firstWarehouseCode("BTL_S_001").firstPosition("B*aa").firstPositionCode("1015")
                .bizName("河源龙川").bizId(184).partnerId(77).partnerName("河源龙川").partnerPhone("13723587749").provinceId(1).cityId(110000).countyId(1).townId(110101001).build();
        PackageDTO packageDTO = PackageDTO.builder().id(1911).transportOrderNo("W905220000081").packageNo("W905220000081_01").transportOrderId(663).state(0).payment(BigDecimal.ONE).weight(BigDecimal.ZERO)
                .inventoriedState(0).bulk(BigDecimal.ZERO).isNailBox(false).createUserId(8).createTime(new Date())
                .build();
        sendMsgService.sendTransportOrderAndPackageMessageToTMS(transportOrderDTO, Lists.newArrayList(packageDTO),transLineDTO);
    }

    @Reference(check = false)
    private TransportOrderBizService transportOrderBizService;


    @Test
    public void m7(){
        ResultDTO<List<com.baturu.tms.api.dto.transport.TransportOrderDTO>> resultDTO =
                transportOrderBizService.queryTransportOrderByOrderNos(Sets.newHashSet("W90418000012"));
        Assert.assertTrue(resultDTO.isSuccess());
    }

    @Reference(check = false)
    private TransportBizService transportBizService;
    @Test
    public void m8(){
        TransportQueryRequest transportQueryRequest = TransportQueryRequest.builder().transportType(TransportTypeEnum.BTL_ONLINE.getType())
                .destinationTransportPointParam(TransportPointParam.builder().pointId(331)
                        .provinceID(19).cityID(441500)
                        .countyID(1682).townId(441523100).build())
                .originTransportPointParam(TransportPointParam.builder().build())
                .btlTransportParam(BtlTransportParam.builder().supplierId(0).fromWarehouseCode("BTL_S_001").build()).build();
        ResultDTO<TransportInfoDTO> transportInfoDTOResultDTO = transportBizService.queryTransportLine(transportQueryRequest);
        return;
    }



    @Autowired
    private TransportOrderController transportOrderController;

    @Test
    public void m9(){
        ResultDTO resultDTO = transportOrderController.queryInPage(com.baturu.zd.request.business.TransportOrderQueryRequest.builder().createTimeStart(new Date())
                .createTimeEnd(new Date()).build(), null);
        return;
    }

    @Reference(check = false)
    private TeamRpcService teamRpcService;
    @Reference(check = false)
    private PrincipalUserRpcService principalUserRpcService;


    @Test
    public void m10(){
        com.baturu.crm.api.dto.ResultDTO<List<Team>> list = teamRpcService.list(TeamQuery.builder().teamIds(Lists.newArrayList(77L)).build());
        com.baturu.crm.api.dto.ResultDTO<List<PrincipalUser>> byParam = principalUserRpcService.findByParam(PrincipalUserQuery.builder().teamId(77L).build());
        return;
    }

}
