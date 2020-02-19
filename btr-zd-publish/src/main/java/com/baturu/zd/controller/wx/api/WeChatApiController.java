package com.baturu.zd.controller.wx.api;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.baturu.message.constant.SmsType;
import com.baturu.message.request.SmsMessageRequest;
import com.baturu.message.service.MessageService;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.BaseConstant;
import com.baturu.zd.constant.WxSignConstant;
import com.baturu.zd.controller.BaseController;
import com.baturu.zd.dto.PackageImprintDTO;
import com.baturu.zd.dto.wx.*;
import com.baturu.zd.request.business.IdentificationQueryRequest;
import com.baturu.zd.request.business.ReservationOrderQueryRequest;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.request.wx.WxSignQueryRequest;
import com.baturu.zd.service.business.*;
import com.baturu.zd.service.common.AuthenticationService;
import com.baturu.zd.service.server.ReservationOrderQueryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * created by ketao by 2019/03/05
 **/

@RestController
@Slf4j
@RequestMapping("wx/api")
public class WeChatApiController extends BaseController {

    @Autowired
    private WxSignService wxSignService;
    @Autowired
    private IdentificationService identificationService;
    @Autowired
    private ReservationOrderService reservationOrderService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private TransportOrderService transportOrderService;
    @Autowired
    private SignBindService signBindService;
    @Autowired
    private PackageImprintService packageImprintService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ReservationOrderQueryService reservationOrderQueryService;
    @Reference(check = false)
    private MessageService messageService;


    @RequestMapping(value = "identification", method = RequestMethod.POST)
    public ResultDTO identification(@RequestBody IdentificationDTO identificationDTO) {
        WxSignDTO wxSgin = authenticationService.getWxSign();
        if (identificationDTO.getId() == null) {
            identificationDTO.setCreateUserId(wxSgin.getId());
        } else {
            identificationDTO.setUpdateUserId(wxSgin.getId());
        }
        return identificationService.saveIdentification(identificationDTO);
    }


    @RequestMapping(value = "updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO updatePassword(@RequestBody WxSignDTO wxSignDTO) {
        WxSignDTO wxSign = authenticationService.getWxSign();
        wxSignDTO.setUpdateUserId(wxSign.getId());
        log.info("密码修改参数：{}", wxSignDTO);
        return wxSignService.updatePassword(wxSignDTO);
    }

    @RequestMapping(value = "reservationOrder", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO reservationOrder(@RequestBody ReservationOrderVO reservationOrderVO) {
        WxSignDTO wxSign = authenticationService.getWxSign();
        ReservationOrderDTO reservationOrderDTO = ReservationOrderVO.toDTO(reservationOrderVO);
        ResultDTO<WxSignDTO> resultDTO = wxSignService.selectById(wxSign.getId());
        if (resultDTO.isUnSuccess() || resultDTO.getModel() == null) {
            return ResultDTO.failed("登录信息查询失败");
        }
        WxSignDTO wxSignDTO = resultDTO.getModel();
        if (reservationOrderDTO.getId() == null) {
            reservationOrderDTO.setCreateUserId(wxSign.getId());
            reservationOrderDTO.setOperatorId((wxSignDTO.getOwnerId() == 0) ? wxSign.getId() : wxSign.getOwnerId());
        } else {
            ResultDTO<ReservationOrderDTO> reservationOrderDTOResultDTO = reservationOrderQueryService.queryById(reservationOrderDTO.getId());
            if (reservationOrderDTOResultDTO.isSuccess() && reservationOrderDTOResultDTO.getModel() != null) {
                ReservationOrderDTO model = reservationOrderDTOResultDTO.getModel();
                //既不是创建人也不是处理人（母账号）
                if ((!model.getCreateUserId().equals(wxSignDTO.getId()) && (!wxSignDTO.getId().equals(model.getOperatorId())))) {
                    return ResultDTO.failed("无权修改/删除");
                }
            }
        }
        reservationOrderDTO.setUpdateUserId(wxSign.getId());
        log.info("生成/修改预约单入参;{}", JSON.toJSONString(reservationOrderDTO));
        return reservationOrderService.saveReservationOrder(reservationOrderDTO);
    }

    @RequestMapping(value = "identificationBySignId", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO identificationBySignId() {
        WxSignDTO wxSign = authenticationService.getWxSign();
        IdentificationQueryRequest request = IdentificationQueryRequest.builder().build();
        request.setCreateUserId(wxSign.getId());
        ResultDTO<List<IdentificationDTO>> resultDTO = identificationService.queryIdentifications(request);
        if (resultDTO.isSuccess() && resultDTO.getModel().size() > 0) {
            return ResultDTO.succeedWith(resultDTO.getModel().get(0));
        }
        return resultDTO;
    }


    @RequestMapping(value = "queryReservationOrders", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryReservationOrders(@ModelAttribute ReservationOrderQueryRequest reservationOrderQueryRequest) {
        log.info("查询预约单入参：{}", reservationOrderQueryRequest);
        WxSignDTO wxSign = authenticationService.getWxSign();
        reservationOrderQueryRequest.setSource(BaseConstant.SOURCE_TYPE_WECHAT);
        ResultDTO<WxSignDTO> resultDTO = wxSignService.selectById(wxSign.getId());
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        reservationOrderQueryRequest.setOperatorId(resultDTO.getModel().getOwnerId() == 0 ? wxSign.getId() : wxSign.getOwnerId());
        return reservationOrderService.queryReservationOrdersInPage(reservationOrderQueryRequest);
    }



    @RequestMapping(value = "queryIdentifications", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryIdentifications(@ModelAttribute IdentificationQueryRequest identificationQueryRequest) {
        return identificationService.queryIdentificationsInPage(identificationQueryRequest);
    }



    @RequestMapping(value = "queryTransportOrders", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryTransportOrders(@ModelAttribute TransportOrderQueryRequest transportOrderQueryRequest) {
        transportOrderQueryRequest.setSource(BaseConstant.SOURCE_TYPE_WECHAT);
        return transportOrderService.queryTransportOrdersInPage(transportOrderQueryRequest);
    }

    @RequestMapping(value = "confirmSignInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO confirmSignInfo(@RequestBody SignBindDTO signBindDTO) {
        WxSignDTO wxSign = authenticationService.getWxSign();
        log.info("修改绑定确认：登录账号：{}，旧账号：{}", wxSign.getSignPhone(), signBindDTO.getSignPhone());
        if (!wxSign.getSignPhone().equals(signBindDTO.getSignPhone())) {
            return ResultDTO.failed("填写的旧账号与当前登录的账号不一致");
        }
        return signBindService.confimSignInfo(signBindDTO);
    }

    @RequestMapping(value = "newSignBind", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO newSignBind(@RequestBody SignBindDTO signBindDTO) {
        WxSignDTO wxSign = authenticationService.getWxSign();
        log.info("绑定修改参数：{}", signBindDTO);
        return signBindService.newSignBind(signBindDTO);
    }

    @RequestMapping(value = "checkIdentification", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO checkIdentification() {
        WxSignDTO wxSign = authenticationService.getWxSign();
        IdentificationQueryRequest request = IdentificationQueryRequest.builder().build();
        request.setCreateUserId(wxSign.getId());
        ResultDTO<List<IdentificationDTO>> resultDTO = identificationService.queryIdentifications(request);
        if (resultDTO.isSuccess()) {
            if (CollectionUtils.isEmpty(resultDTO.getModel())) {
                //未认证
                return ResultDTO.successfy(Boolean.FALSE);
            } else {
                //已认证
                return ResultDTO.successfy(Boolean.TRUE);
            }
        } else {
            return resultDTO;
        }
    }

    @RequestMapping(value = "queryTransportImprint", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO<List<PackageImprintDTO>> queryTransportImprint(String transportOrderNo) {
        return packageImprintService.queryByTransportOrderNo(transportOrderNo);
    }


    @RequestMapping(value = "addChildAccount", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO addChildAccount(@RequestBody WxSignDTO wxSignDTO) {
        WxSignDTO wxSign = authenticationService.getWxSign();
        if (wxSign.getSignPhone().equals(wxSignDTO.getSignPhone())) {
            return ResultDTO.failed("不可添加自己为子账号");
        }
        wxSignDTO.setCreateUserId(wxSign.getId());
        wxSignDTO.setOwnerId(wxSign.getId());
        wxSignDTO.setOpenId(wxSignDTO.getSignPhone());
        log.info("子账号添加参数{}", wxSignDTO);
        ResultDTO<WxSignDTO> resultDTO = wxSignService.saveChildAccount(wxSignDTO);
        try {
            if (resultDTO.isSuccess()) {
                Map<String, String> parameters = Maps.newHashMap();
                parameters.put("phone", wxSignDTO.getSignPhone());
                parameters.put("password", wxSignDTO.getPassword());
                SmsMessageRequest request = SmsMessageRequest.builder()
                        .templateName(WxSignConstant.ZD_WECHAT_CHILD_ACCOUNT)
                        .parameters(parameters)
                        .build();
                request.setAppKey("btr-zd-publish");
                request.setSmsType(SmsType.SMS_BUSINESS.getValue());
                request.setReceivers(Lists.newArrayList(wxSignDTO.getSignPhone()));
                com.baturu.kit.dto.ResultDTO<Void> result = messageService.sendSms(request);
                if (!result.isSuccess()) {
                    log.info("微信子账号添加短信通知接口调用失败：{}", result.getMsg());
                }
            }
        } catch (Exception e) {
            log.error("微信子账号添加短信通知接口发送异常：", e);
        }
        return resultDTO;
    }



    @RequestMapping(value = "queryChildAccounts", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO<List<WxSignDTO>> queryChildAccounts(WxSignQueryRequest wxSignQueryRequest) {
        WxSignDTO wxSign = authenticationService.getWxSign();
        if (wxSign.getId() == null || wxSign.getId() == 0) {
            log.info("当前登录用户获取失败:{}", wxSign);
            return ResultDTO.failed(501, "当前登录用户获取失败");
        }
        wxSignQueryRequest.setOwnerId(wxSign.getId());
        return wxSignService.selectInPage(wxSignQueryRequest);
    }



    @RequestMapping(value = "logout", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO wxLogout() {
        WxSignDTO wxSign = authenticationService.getWxSign();
        Boolean result = redisTemplate.delete(wxSign.getOpenId());
        if (!result) {
            log.info("登录信息不存在，{}", wxSign.getOpenId());
        }
        return ResultDTO.succeedWith(Boolean.TRUE);
    }

    @RequestMapping(value = "getOwnerAccount", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO getOwnerAccount() {
        WxSignDTO wxSign = authenticationService.getWxSign();
        ResultDTO<WxSignDTO> resultDTO = wxSignService.selectById(wxSign.getId());
        if (resultDTO.isSuccess()) {
            WxSignDTO current = resultDTO.getModel();
            ResultDTO<List<IdentificationDTO>> listResultDTO = identificationService.
                    queryIdentifications(IdentificationQueryRequest.builder().createUserId(current.getOwnerId()).build());
            if (listResultDTO.isSuccess() && CollectionUtils.isNotEmpty(listResultDTO.getModel())) {
                return ResultDTO.succeedWith(listResultDTO.getModel().get(0));
            }

        }
        return ResultDTO.failed();
    }

}
