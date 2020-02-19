package com.baturu.zd.controller.app;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.tms.api.dto.transport.DriverDTO;
import com.baturu.tms.api.dto.transport.VehicleDTO;
import com.baturu.tms.api.request.transport.DriverQueryRequest;
import com.baturu.tms.api.request.transport.VehicleQueryRequest;
import com.baturu.tms.api.service.common.transport.DriverQueryService;
import com.baturu.tms.api.service.common.transport.VehicleQueryService;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.app.CarInfoDTO;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 逐道收单APP车辆查询控制器
 * @author CaiZhuliang
 * @since 2019-3-28
 */
@RestController
@Slf4j
@RequestMapping("app/car")
public class CarController extends AbstractAppBaseController {

    @Reference(check = false)
    private VehicleQueryService vehicleQueryService;
    @Reference(check = false)
    private DriverQueryService driverQueryService;

    @RequestMapping(value = "/queryCarInfo",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryCarInfo(String vehiclePlate) {
        ResultDTO<List<VehicleDTO>> vehicleResultDTO = vehicleQueryService.queryByParam(VehicleQueryRequest.builder().vehiclePlate(vehiclePlate).active(true).build());
        List<VehicleDTO> vehicleDTOs = vehicleResultDTO.getModel();
        if (!vehicleResultDTO.isSuccess() || CollectionUtils.isEmpty(vehicleDTOs)) {
            log.info("EntruckingController queryCarInfo error : 获取车辆信息失败。vehiclePlate = {}, vehicleResultDTO = {}", vehiclePlate, vehicleResultDTO);
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_503, "未查到此车牌");
        }
        List<CarInfoDTO> carInfoDTOs = new ArrayList<>(vehicleDTOs.size());
        for (VehicleDTO vehicleDTO : vehicleDTOs) {
            CarInfoDTO carInfoDTO = CarInfoDTO.builder().driverName(vehicleDTO.getDriverName()).vehiclePlate(vehicleDTO.getVehiclePlate()).build();
            if (null != vehicleDTO.getDriverId()) {
                Set<Integer> ids = Sets.newHashSet(vehicleDTO.getDriverId());
                ResultDTO<List<DriverDTO>> driverResultDTO = driverQueryService.queryByParam(DriverQueryRequest.builder().ids(ids).active(true).build());
                List<DriverDTO> driverDTOs = driverResultDTO.getModel();
                if (!driverResultDTO.isSuccess() || CollectionUtils.isEmpty(driverDTOs)) {
                    log.info("EntruckingController queryCarInfo error : 获取司机信息失败。ids = {}, driverResultDTO = {}", ids, driverResultDTO);
                } else {
                    carInfoDTO.setMobilePhone(driverDTOs.get(0).getMobilePhone());
                }
            }
            carInfoDTOs.add(carInfoDTO);
        }
        return ResultDTO.succeedWith(carInfoDTOs, AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

}
