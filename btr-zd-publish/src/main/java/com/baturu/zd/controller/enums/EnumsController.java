package com.baturu.zd.controller.enums;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.enums.*;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * created by ketao by 2019/03/18
 **/
@RestController
@Slf4j
@RequestMapping("zd/enums")
public class EnumsController {

    @RequestMapping(value = "value", method = RequestMethod.POST)
    public ResultDTO values(@RequestBody Set<Integer> types) {
        if (types == null || types.size() == 0) {
            return ResultDTO.failed("枚举类型为空");
        }
        return ResultDTO.succeedWith(this.getEnumMap(types));
    }

    /**
     * 枚举类型适配
     *
     * @param types 1：支付方式;2:银行名称;3：配送方式;4:运单状态；5：包裹状态;6:地址类型下拉;
     *              7：菜单等级;8：预约单状态下拉;9：服务网点类型;10:区域下拉;11:权限类型下拉;12:按钮事件类型;13:装车单状态类型;14:异常类型
     * @return
     */
    private Map<String, Object> getEnumMap(Set<Integer> types) {
        Map<String, Object> map = Maps.newHashMap();
        for (Integer type : types) {
            switch (type) {
                case 1:
                    map.put("payTypeList", PayTypeEnum.descMap);
                    break;
                case 2:
                    map.put("bankNameList", BankNameEnum.descMap);
                    break;
                case 3:
                    map.put("deliveryTypeList", DeliveryTypeEnum.descMap);
                    break;
                case 4:
                    map.put("transportOrderStateList", TransportOrderStateEnum.descMap);
                    break;
                case 5:
                    map.put("packageStateList", PackageStateEnum.descMap);
                    break;
                case 6:
                    map.put("wxAddressTypeList", WxAddressTypeEnum.descMap);
                    break;
                case 7:
                    map.put("pageLevelList", PageLevelEnum.descMap);
                    break;
                case 8:
                    map.put("reservationOrderStateList", ReservationOrderStateEnum.descMap);
                    break;
                case 9:
                    map.put("servicePointTypeList", ServicePointTypeEnum.descMap);
                    break;
                case 10:
                    map.put("regionList", RegionEnum.descMap);
                    break;
                case 11:
                    map.put("permissionTypeList", PermissionTypeEnum.descMap);
                    break;
                case 12:
                    map.put("ButtonEventList", ButtonEventEnum.descMap);
                    break;
                case 13:
                    map.put("ButtonEventList", EntruckingStateEnum.descMap);
                    break;
                case 14:
                    map.put("ExceptionTypeList", ExceptionTypeEnum.descMap);
                    break;
                case 15:
                    map.put("ExceptionStateList", ExceptionStateEnum.descMap);
                    break;
                case 16:
                    map.put("ExceptionHandleResultList", ExceptionHandleResultEnum.descMap);
                    break;
                case 17:
                    map.put("BlameStateList", BlameStateEnum.descMap);
                    break;
                case 18:
                    map.put("CollectAmountList", CollectAmountEnum.descMap);
                    break;
                default:
                    break;
            }
        }
        return map;
    }


}
