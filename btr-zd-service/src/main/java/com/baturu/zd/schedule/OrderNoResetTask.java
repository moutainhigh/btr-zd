package com.baturu.zd.schedule;

import com.baturu.zd.mapper.FerryOrderMapper;
import com.baturu.zd.mapper.TransportOrderMapper;
import com.baturu.zd.mapper.common.EntruckingMapper;
import com.baturu.zd.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author liuduanyang
 * @since 2019/3/25 11:32
 */
@Component
@Slf4j
public class OrderNoResetTask {

    @Autowired
    private OrderNoOffset orderNoOffset;

    @Autowired
    private FerryOrderMapper ferryOrderMapper;

    @Autowired
    private EntruckingMapper entruckingMapper;

    @Autowired
    private TransportOrderMapper transportOrderMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void execute() {
        log.debug("OrderNoResetTask execute time : {}", DateUtil.getCurrentDate());
        // reset
        Integer nextFerryOrderId = ferryOrderMapper.getNextId();
        Integer nextTransportOrderId = transportOrderMapper.getNextId();
        Integer nextEntruckingId = entruckingMapper.getNextId();
        log.info("OrderNoResetTask execute : nextFerryOrderId = {}, nextTransportOrderId = {}, nextEntruckingId", nextFerryOrderId, nextTransportOrderId, nextEntruckingId);
        orderNoOffset.setFerryOrderOffset(nextFerryOrderId);
        orderNoOffset.setTransportOrderOffset(nextTransportOrderId);
        orderNoOffset.setEntruckingOffset(nextEntruckingId);
    }
}
