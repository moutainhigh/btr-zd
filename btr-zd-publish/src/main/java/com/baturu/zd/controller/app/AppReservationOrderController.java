package com.baturu.zd.controller.app;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.ReservationOrderConstant;
import com.baturu.zd.dto.app.AppReservationOrderDTO;
import com.baturu.zd.dto.wx.ReservationOrderDTO;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.request.business.ReservationOrderQueryRequest;
import com.baturu.zd.request.server.WxAddressBaseQueryRequest;
import com.baturu.zd.service.business.ReservationOrderService;
import com.baturu.zd.service.business.WxAddressSnapshotService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.service.server.ReservationOrderQueryServiceImpl;
import com.baturu.zd.service.server.WxAddressQueryService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 逐道收单APP预约单控制器
 * @author liuduanyang
 * @since 2019/3/21
 */
@RestController
@Slf4j
@RequestMapping("app/reservation")
public class AppReservationOrderController extends AbstractAppBaseController {

    @Autowired
    private ReservationOrderService reservationOrderService;

    @Autowired
    private ReservationOrderQueryServiceImpl reservationOrderQueryService;

    @Autowired
    private WxAddressSnapshotService wxAddressSnapshotService;

    @Autowired
    private WxAddressQueryService wxAddressQueryService;


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultDTO listByPage(ReservationOrderQueryRequest reservationOrderQueryRequest) {
        String reservationNo = reservationOrderQueryRequest.getReservationNo();
        Integer state = reservationOrderQueryRequest.getState();

        if (reservationNo != null && !reservationNo.startsWith(ReservationOrderConstant.prefix)) {
            StringBuilder sb = new StringBuilder();
            sb.append(ReservationOrderConstant.prefix).append(reservationNo);
            reservationOrderQueryRequest.setReservationNo(sb.toString());
        }
        ResultDTO checkResult = checkOrderState(state);
        if (checkResult.isUnSuccess()) {
            return checkResult;
        }
        if (reservationOrderQueryRequest.getSize() == 0) {
            reservationOrderQueryRequest.setSize(10);
        }
        if (reservationOrderQueryRequest.getCurrent() == 0) {
            reservationOrderQueryRequest.setCurrent(1);
        }

        ResultDTO resultDTO = reservationOrderService.queryReservationOrdersInPage(reservationOrderQueryRequest);
        // 分页查询失败
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }

        // 分页查询数据处理
        List<AppReservationOrderDTO> appReservationOrderDTOList = new ArrayList<>(10);
        PageDTO pageDTO  = (PageDTO) resultDTO.getModel();
        List<ReservationOrderDTO> reservationOrderDTOList = pageDTO.getRecords();
        for (ReservationOrderDTO reservationOrderDTO : reservationOrderDTOList) {
            AppReservationOrderDTO appReservationOrderDTO = new AppReservationOrderDTO();
            this.copyProperties(reservationOrderDTO, appReservationOrderDTO, false);
            appReservationOrderDTOList.add(appReservationOrderDTO);
        }
        resultDTO.setModel(appReservationOrderDTOList);
        resultDTO.setErrorCode(200);
        resultDTO.setErrorMsg(null);
        return resultDTO;
    }

    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public ResultDTO getById(@PathVariable Integer id) {
        if (id == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "必传参数reservationId没传");
        }

        ResultDTO<ReservationOrderDTO> resultDTO = reservationOrderQueryService.queryById(id);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        ReservationOrderDTO reservationOrderDTO = resultDTO.getModel();
        if (resultDTO == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查询不到预约单");
        }

        // 填充地址簿信息
        this.fillAddress(reservationOrderDTO);

        AppReservationOrderDTO appReservationOrderDTO = new AppReservationOrderDTO();
        this.copyProperties(reservationOrderDTO, appReservationOrderDTO, true);

        return ResultDTO.succeedWith(appReservationOrderDTO);
    }

    /**
     * 填充地址簿信息
     * @param reservationOrder
     */
    private void  fillAddress(ReservationOrderDTO reservationOrder){
        if (reservationOrder == null) {
            return;
        }
        Set<Integer> addrIds = Sets.newHashSet();
        addrIds.add(reservationOrder.getRecipientAddrId());
        addrIds.add(reservationOrder.getSenderAddrId());
        WxAddressBaseQueryRequest wxAddressBaseQueryRequest = WxAddressBaseQueryRequest.builder()
                .ids(addrIds)
                .build();

        // 查询地址簿快照
        ResultDTO<List<WxAddressDTO>> resultDTO = wxAddressQueryService.queryByParam(wxAddressBaseQueryRequest);
        if (resultDTO.isUnSuccess()) {
            return;
        }
        Map<Integer, WxAddressDTO> wxAddressDTOMap = resultDTO.getModel().stream().collect(Collectors.toMap(o -> o.getId(), o -> o, (o1, o2) -> o1));
        reservationOrder.setSenderAddress(wxAddressDTOMap.get(reservationOrder.getSenderAddrId()));
        reservationOrder.setRecipientAddress(wxAddressDTOMap.get(reservationOrder.getRecipientAddrId()));
    }

    private void copyProperties(ReservationOrderDTO reservationOrderDTO, AppReservationOrderDTO appReservationOrderDTO, Boolean isDetail) {
        appReservationOrderDTO.setId(reservationOrderDTO.getId());
        appReservationOrderDTO.setReservationNo(reservationOrderDTO.getReservationNo());
        appReservationOrderDTO.setQty(reservationOrderDTO.getQty());

        // copy地址信息
        appReservationOrderDTO.setSenderAddr(reservationOrderDTO.getSenderAddress());
        appReservationOrderDTO.setRecipientAddr(reservationOrderDTO.getRecipientAddress());

        if (isDetail) {
            BeanUtils.copyProperties(reservationOrderDTO, appReservationOrderDTO);
        }
    }

    /**
     * 校验预约单状态
     * @param orderState
     * @return
     */
    private ResultDTO checkOrderState(Integer orderState) {
        if (orderState == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "必传参数state没传");
        }
        if (!orderState.equals(ReservationOrderConstant.ORDER_ALL) &&
                !orderState.equals(ReservationOrderConstant.ORDER_BOOK) &&
                !orderState.equals(ReservationOrderConstant.ORDER_CANCEL) &&
                !orderState.equals(ReservationOrderConstant.ORDER_OPEN)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "参数state非法");
        }
        return ResultDTO.succeed();
    }
}
