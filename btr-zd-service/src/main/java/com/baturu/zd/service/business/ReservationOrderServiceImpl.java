package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.config.datasource.DataSourceType;
import com.baturu.zd.config.datasource.Datasource;
import com.baturu.zd.constant.ReservationOrderConstant;
import com.baturu.zd.constant.WxSignConstant;
import com.baturu.zd.dto.common.ReservationOrderExcelDTO;
import com.baturu.zd.dto.wx.ReservationOrderDTO;
import com.baturu.zd.dto.wx.ReservationOrderSummaryDTO;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.dto.wx.WxSignDTO;
import com.baturu.zd.entity.wx.ReservationOrderEntity;
import com.baturu.zd.enums.WxAddressTypeEnum;
import com.baturu.zd.mapper.wx.ReservationOrderMapper;
import com.baturu.zd.request.business.ReservationOrderQueryRequest;
import com.baturu.zd.request.server.WxAddressBaseQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.common.AuthenticationService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.service.server.WxAddressQueryService;
import com.baturu.zd.transform.wx.ReservationOrderTransform;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * created by ketao by 2019/03/06
 **/
@Service("reservationOrderService")
@Slf4j
public class ReservationOrderServiceImpl extends AbstractServiceImpl implements ReservationOrderService {

    @Autowired
    private ReservationOrderMapper reservationOrderMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private WxAddressQueryService wxAddressQueryService;

    @Transactional(rollbackFor = Exception.class)
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    @Override
    public ResultDTO<ReservationOrderDTO> saveReservationOrder(ReservationOrderDTO reservationOrderDTO) {
        ResultDTO resultDTO = this.checkReservationOrder(reservationOrderDTO);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }

        ReservationOrderEntity reservationOrderEntity = ReservationOrderTransform.DTO_TO_ENTITY.apply(reservationOrderDTO);
        if (reservationOrderDTO.getId() == null) {
            reservationOrderEntity.setCreateTime(new Date());
            int num = reservationOrderMapper.insert(reservationOrderEntity);
            if (num == 0) {
                return ResultDTO.failed("预约失败");
            }
            reservationOrderDTO.setId(reservationOrderEntity.getId());
        } else {
            // 通过预约单新增运单成功后，把运单号回填到预约单里
            if (StringUtils.isNotBlank(reservationOrderDTO.getTransportOrderNo())) {
                reservationOrderEntity.setTransportOrderNo(reservationOrderDTO.getTransportOrderNo());
            }
            int num = reservationOrderMapper.updateById(reservationOrderEntity);
            if (num == 0) {
                return ResultDTO.failed("预约单修改失败");
            }
        }
        return ResultDTO.succeedWith(reservationOrderDTO);
    }

    @Override
    public ResultDTO queryReservationOrdersInPage(ReservationOrderQueryRequest reservationOrderQueryRequest) {
        QueryWrapper wrapper = this.initWrapper(reservationOrderQueryRequest);
        Page page = getPage(reservationOrderQueryRequest.getCurrent(), reservationOrderQueryRequest.getSize());
        IPage iPage = reservationOrderMapper.selectPage(page, wrapper);
        List<ReservationOrderDTO> reservationOrders = Lists2.transform(iPage.getRecords(), ReservationOrderTransform.ENTITY_TO_DTO);
        this.fillAddress(reservationOrders, reservationOrderQueryRequest);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(reservationOrders);
        pageDTO.setTotal(iPage.getTotal());
        return ResultDTO.succeedWith(pageDTO);
    }

    @Override
    public ResultDTO queryReservationOrdersInWebPage(ReservationOrderQueryRequest reservationOrderQueryRequest) {
        Page page = getPage(reservationOrderQueryRequest.getCurrent(), reservationOrderQueryRequest.getSize());
        List<ReservationOrderEntity> reservationOrderEntities = reservationOrderMapper.selectPageList(page, reservationOrderQueryRequest);
        List<ReservationOrderDTO> reservationOrders = Lists2.transform(reservationOrderEntities, ReservationOrderTransform.ENTITY_TO_DTO);
        this.fillAddress(reservationOrders, reservationOrderQueryRequest);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(reservationOrders);
        pageDTO.setTotal(page.getTotal());
        return ResultDTO.succeedWith(pageDTO);
    }

    /**
     * 查询预约单统计信息
     *
     * @return
     */
    @Override
    public ResultDTO queryReservationOrderSummary(ReservationOrderQueryRequest reservationOrderQueryRequest) {
        ReservationOrderSummaryDTO reservationOrderSummaryDTO = reservationOrderMapper.queryReservationOrderSummary(reservationOrderQueryRequest);
        return ResultDTO.successfy(reservationOrderSummaryDTO);
    }

    /**
     * 预约单查询导出excel
     *
     * @param reservationOrderQueryRequest
     * @return
     */
    @Override
    public List<ReservationOrderExcelDTO> exportReservationOrderExcel(ReservationOrderQueryRequest reservationOrderQueryRequest) {
        List<ReservationOrderExcelDTO> reservationOrderExcelDTOS = reservationOrderMapper.queryReservationOrderExcel(reservationOrderQueryRequest);
        return reservationOrderExcelDTOS;
    }

    private QueryWrapper initWrapper(ReservationOrderQueryRequest reservationOrderQueryRequest) {
        QueryWrapper wrapper = new QueryWrapper();
        if (StringUtils.isNotBlank(reservationOrderQueryRequest.getReservationNo())) {
            wrapper.eq(ReservationOrderConstant.RESERVATION_ORDER_NO, reservationOrderQueryRequest.getReservationNo());
        }
        if (reservationOrderQueryRequest.getState() != null && reservationOrderQueryRequest.getState() != 0) {
            wrapper.eq(ReservationOrderConstant.STATE, reservationOrderQueryRequest.getState());
        }
        if (reservationOrderQueryRequest.getId() != null) {
            wrapper.eq(ReservationOrderConstant.ID, reservationOrderQueryRequest.getId());
        }
        if (reservationOrderQueryRequest.getOperatorId() != null) {
            wrapper.eq(ReservationOrderConstant.OPERATOR_ID,reservationOrderQueryRequest.getOperatorId());
        }
        //手机号码后四位查询收货地址id
        if (StringUtils.isNotBlank(reservationOrderQueryRequest.getRecipientPhoneFix()) || StringUtils.isNotBlank(reservationOrderQueryRequest.getRecipientName())) {
            WxAddressBaseQueryRequest wxAddressBaseQueryRequest = WxAddressBaseQueryRequest.builder()
                    .type(WxAddressTypeEnum.RECIPIENT.getType())
                    .phone(reservationOrderQueryRequest.getRecipientPhoneFix())
                    .name(reservationOrderQueryRequest.getRecipientName())
                    .queryAll(Boolean.TRUE)
                    .build();
            if (reservationOrderQueryRequest.getWxUserId() != null) {
                wxAddressBaseQueryRequest.setCreateUserIds(Sets.newHashSet(reservationOrderQueryRequest.getWxUserId()));
            }
            ResultDTO<List<WxAddressDTO>> resultDTO = wxAddressQueryService.queryByParam(wxAddressBaseQueryRequest);
            if (resultDTO.isSuccess() && resultDTO.getModel().size() > 0) {
                Set<Integer> addressIds = resultDTO.getModel().stream().map(WxAddressDTO::getId).collect(Collectors.toSet());
                wrapper.in(ReservationOrderConstant.RECIPIENT_ADDR_ID, addressIds);
            } else {
                wrapper.eq(ReservationOrderConstant.RECIPIENT_ADDR_ID, -1);
            }
        }
        if (reservationOrderQueryRequest.getActive() == null) {
            wrapper.eq(ReservationOrderConstant.ACTIVE, Boolean.TRUE);
        } else {
            wrapper.eq(ReservationOrderConstant.ACTIVE, reservationOrderQueryRequest.getActive());
        }
        wrapper.orderByDesc(ReservationOrderConstant.CREATE_TIME);
        return wrapper;
    }

    private ResultDTO checkReservationOrder(ReservationOrderDTO reservationOrderDTO) {
        if (reservationOrderDTO == null) {
            return ResultDTO.failed("预约单参数为空");
        }
        if (reservationOrderDTO.getId() == null) {
            if (reservationOrderDTO.getRecipientAddrId() == null) {
                return ResultDTO.failed("收货地址为空");
            }
            if (reservationOrderDTO.getSenderAddrId() == null) {
                return ResultDTO.failed("寄件地址为空");
            }
            if (reservationOrderDTO.getQty() == null) {
                return ResultDTO.failed("件数为空");
            }
            if (reservationOrderDTO.getPayType() == null) {
                return ResultDTO.failed("付款方式为空");
            }
            ResultDTO<String> resultDTO = this.initReservationNo();
            if (resultDTO.isUnSuccess()) {
                return resultDTO;
            }
            reservationOrderDTO.setReservationNo(resultDTO.getModel());

        }
        return ResultDTO.succeed();
    }

    /**
     * 初始化预约单号
     *
     * @return
     */
    private ResultDTO<String> initReservationNo() {
        WxSignDTO wxSgin = authenticationService.getWxSign();
        if (StringUtils.isBlank(wxSgin.getSignPhone())) {
            return ResultDTO.failed("登录信息异常");
        }
        StringBuilder reservationNo = new StringBuilder();
        reservationNo.append(WxSignConstant.RESERVATION_NO_PREFIX);
        reservationNo.append(wxSgin.getSignPhone().substring(wxSgin.getSignPhone().length() - 4));
        return ResultDTO.succeedWith(reservationNo.toString());
    }

    /**
     * 填充地址簿
     *
     * @param reservationOrders
     */
    private void fillAddress(List<ReservationOrderDTO> reservationOrders, ReservationOrderQueryRequest reservationOrderQueryRequest) {
        if (CollectionUtils.isEmpty(reservationOrders)) {
            return;
        }
        Set<Integer> senderIds = reservationOrders.stream().map(ReservationOrderDTO::getSenderAddrId).collect(Collectors.toSet());
        Set<Integer> recipentIds = reservationOrders.stream().map(ReservationOrderDTO::getRecipientAddrId).collect(Collectors.toSet());
        Set<Integer> addrIds = Sets.newHashSet();
        addrIds.addAll(senderIds);
        addrIds.addAll(recipentIds);
        WxAddressBaseQueryRequest wxAddressBaseQueryRequest = WxAddressBaseQueryRequest.builder().ids(addrIds).build();

        ResultDTO<List<WxAddressDTO>> resultDTO = wxAddressQueryService.queryByParam(wxAddressBaseQueryRequest);
        if (resultDTO.isSuccess()) {
            Map<Integer, WxAddressDTO> wxAddressDTOMap = resultDTO.getModel().stream().collect(Collectors.toMap(w -> w.getId(), w -> w, (w1, w2) -> w1));
            if (!wxAddressDTOMap.isEmpty()) {
                reservationOrders.stream().forEach(r -> {
                    r.setSenderAddress(wxAddressDTOMap.get(r.getSenderAddrId()));
                    r.setRecipientAddress(wxAddressDTOMap.get(r.getRecipientAddrId()));
                });
            }
        }
    }

}
