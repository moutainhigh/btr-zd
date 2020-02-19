package com.baturu.zd.listener;

import com.baturu.zd.mapper.FerryOrderMapper;
import com.baturu.zd.mapper.TransportOrderMapper;
import com.baturu.zd.mapper.common.EntruckingMapper;
import com.baturu.zd.schedule.OrderNoOffset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 应用启动时监听当时的偏移量应该是多少
 * @author CaiZhuliang
 * @since 2019-3-29
 */
@Component
@Slf4j
public class OrderNoOffsetListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final int ENTRUCKINGNO_PREFIX_LENGTH = 8;
    private static final int TRANSPORTORDERNO_PREFIX_LENGTH = 6;
    private static final int FERRYORDERNO_PREFIX_LENGTH = 7;

    @Autowired
    private TransportOrderMapper transportOrderMapper;
    @Autowired
    private OrderNoOffset orderNoOffset;
    @Autowired
    private EntruckingMapper entruckingMapper;
    @Autowired
    private FerryOrderMapper ferryOrderMapper;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("*************************OrderNoOffsetListener start***************************");
        setTransportOrderOffset();
        setEntruckingOffset();
        setFerryOrderOffset();
    }

    private void setEntruckingOffset() {
        int nextEntruckingId = entruckingMapper.getNextId();
        String currentMaxEntruckingNo = entruckingMapper.getCurrentMaxEntruckingNo();
        if (StringUtils.isBlank(currentMaxEntruckingNo)) {
            // 有可能是第一次上线，也有可能是此时还没有单
            if (0 == entruckingMapper.count()) {
                return;
            }
            log.info("OrderNoOffsetListener##setEntruckingOffset : nextEntruckingId = {}, currentMaxEntruckingNo = {}", nextEntruckingId, currentMaxEntruckingNo);
            orderNoOffset.setEntruckingOffset(nextEntruckingId);
            return;
        }
        int maxNo = Integer.valueOf(currentMaxEntruckingNo.substring(ENTRUCKINGNO_PREFIX_LENGTH));
        // 这个逻辑的原因，通过例子说明。早上生成3个运单，中午因为紧急任务重新发布，这时偏移量变成了1，这就不对了。因此要算出当天本来的偏移量
        int offset = nextEntruckingId - maxNo - 1;
        log.info("OrderNoOffsetListener##setEntruckingOffset : nextEntruckingId = {}, currentMaxEntruckingNo = {}, maxNo = {}, offset = {}",
                nextEntruckingId, currentMaxEntruckingNo, maxNo, offset);
        orderNoOffset.setEntruckingOffset(offset);
    }

    private void setTransportOrderOffset() {
        int nextTransportOrderId = transportOrderMapper.getNextId();
        String currentMaxTransportOrderNo = transportOrderMapper.getCurrentMaxTransportOrderNo();
        if (StringUtils.isBlank(currentMaxTransportOrderNo)) {
            // 有可能是第一次上线，也有可能是此时还没有单
            if (0 == transportOrderMapper.count()) {
                return;
            }
            log.info("OrderNoOffsetListener##setTransportOrderOffset : nextTransportOrderId = {}, currentMaxTransportOrderNo = {}", nextTransportOrderId, currentMaxTransportOrderNo);
            orderNoOffset.setTransportOrderOffset(nextTransportOrderId);
            return;
        }
        int currentTransportOrderNo = Integer.valueOf(currentMaxTransportOrderNo.substring(TRANSPORTORDERNO_PREFIX_LENGTH));
        // 这个逻辑的原因，通过例子说明。早上生成3个运单，中午因为紧急任务重新发布，这时偏移量变成了1，这就不对了。因此要算出当天本来的偏移量
        int transportOrderOffset = nextTransportOrderId - currentTransportOrderNo - 1;
        log.info("OrderNoOffsetListener##setTransportOrderOffset : nextTransportOrderId = {}, currentMaxTransportOrderNo = {}, currentTransportOrderNo = {}, transportOrderOffset = {}",
                nextTransportOrderId, currentMaxTransportOrderNo, currentTransportOrderNo, transportOrderOffset);
        orderNoOffset.setTransportOrderOffset(transportOrderOffset);
    }

    private void setFerryOrderOffset() {
        int nextFerryOrderId = ferryOrderMapper.getNextId();
        String currentMaxferryOrderNo = ferryOrderMapper.getCurrentMaxFerryOrderNo();
        if (StringUtils.isBlank(currentMaxferryOrderNo)) {
            // 有可能是第一次上线，也有可能是此时还没有单
            if (0 == ferryOrderMapper.count()) {
                return;
            }
            log.info("OrderNoOffsetListener##setFerryOrderOffset : nextFerryOrderId = {}, currentMaxferryOrderNo = {}", nextFerryOrderId, currentMaxferryOrderNo);
            orderNoOffset.setFerryOrderOffset(nextFerryOrderId);
            return;
        }
        int currentFerryOrderNo = Integer.valueOf(currentMaxferryOrderNo.substring(FERRYORDERNO_PREFIX_LENGTH));

        int ferryOrderOffset = nextFerryOrderId - currentFerryOrderNo - 1;
        orderNoOffset.setFerryOrderOffset(ferryOrderOffset);
    }
}
