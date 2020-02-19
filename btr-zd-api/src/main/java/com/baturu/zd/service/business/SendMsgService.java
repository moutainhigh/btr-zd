package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.TransLineDTO;
import com.baturu.zd.dto.TransportOrderDTO;

import java.util.List;

/**
 * Created by caizhuliang on 2019/4/3.
 */
public interface SendMsgService {

    ResultDTO<String> sendMsg(String payOrderNo);

    /**
     * 推送包裹数据到TMS
     * @return
     */
    void sendTransportOrderAndPackageMessageToTMS(TransportOrderDTO transportOrderDTO, List<PackageDTO> packageDTOS, TransLineDTO transLineDTO);

}
