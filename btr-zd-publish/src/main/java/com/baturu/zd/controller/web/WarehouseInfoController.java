package com.baturu.zd.controller.web;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.controller.app.AbstractAppBaseController;
import com.baturu.zd.service.business.WarehouseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * create by pengdi in 2019/3/25
 * TMS仓库信息controller   部分业务会需要获取分拣仓信息
 */
@RestController
@Slf4j
@RequestMapping(value = "/warehouse/find")
public class WarehouseInfoController extends AbstractAppBaseController  {
    @Autowired
    WarehouseInfoService warehouseInfoService;

    @RequestMapping(value = "/allWarehouse",method = RequestMethod.GET)
    public ResultDTO<List<Map<String,Object>>> findAllWarehouse(Integer regionId){
        return warehouseInfoService.findAllWarehouse(regionId);
    }
}
