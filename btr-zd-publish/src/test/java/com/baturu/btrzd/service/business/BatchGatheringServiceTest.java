package com.baturu.btrzd.service.business;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.BatchGatheringDTO;
import com.baturu.zd.enums.BatchGatheringStatusEnum;
import com.baturu.zd.service.business.BatchGatheringService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by caizhuliang on 2019/10/17.
 */
@Slf4j
public class BatchGatheringServiceTest extends BaseTest {

    @Autowired
    private BatchGatheringService batchGatheringService;

    @Test
    public void testCreate() {
        List<String> transportOrderNos = Lists.newArrayList("W90619000938");
        BatchGatheringDTO batchGatheringDTO = BatchGatheringDTO.builder()
                .status(BatchGatheringStatusEnum.UNPAY.getType())
                .build();
        batchGatheringDTO.setCreateUserId(999999);
        batchGatheringDTO.setCreateTime(new Date());
        ResultDTO<BatchGatheringDTO> resultDTO = batchGatheringService.create(batchGatheringDTO, transportOrderNos);
        log.info("BatchGatheringServiceTest#testCreate - resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

}
