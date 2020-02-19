package com.baturu.zd.service.business;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.tms.api.dto.transport.TransportOrderDTO;
import com.baturu.tms.api.service.business.transport.TransportOrderBizService;
import com.baturu.zd.dto.common.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * created by ketao by 2019/06/05
 **/
@Service("syncTmsTransportOrderService")
@Slf4j
public class SyncTmsTransportOrderServiceImpl {


    @Autowired
    private UserService userService;
    @Reference(check = false)
    private TransportOrderBizService transportOrderBizService;

    /**
     * 运单数据同步tms
     *
     * @param transportOrderDTO tms运单对象
     * @return
     */
    public ResultDTO syncTmsTransportOrder(TransportOrderDTO transportOrderDTO) {
        if (transportOrderDTO == null || StringUtils.isBlank(transportOrderDTO.getOrderNo())) {
            return ResultDTO.failed("运单同步数据对应单号为空");
        }
        if (transportOrderDTO.getUpdateUserId() == null) {
            log.info("运单【{}】同步tms时更新用户id为空", transportOrderDTO.getOrderNo());
        }
        if (transportOrderDTO.getUpdateUserId() != null && StringUtils.isBlank(transportOrderDTO.getUpdateUserName())) {
            ResultDTO<UserDTO> resultDTO = userService.queryUserById(transportOrderDTO.getUpdateUserId().intValue());
            if (resultDTO.isSuccess() && resultDTO.getModel() != null) {
                transportOrderDTO.setUpdateUserName(resultDTO.getModel().getName());
            }
        }
        log.info("tms运单同步对象===>:{}", transportOrderDTO);
        ResultDTO resultDTO = transportOrderBizService.updateTransportOrderByOrderNo(transportOrderDTO);
        if(resultDTO.isUnSuccess()){
            log.info("单号【{}】同步更新tms运单信息失败：{}",transportOrderDTO.getOrderNo(),resultDTO.getErrorMsg());
            return ResultDTO.failed(resultDTO.getErrorMsg());
        }
        return ResultDTO.succeed();
    }
}
