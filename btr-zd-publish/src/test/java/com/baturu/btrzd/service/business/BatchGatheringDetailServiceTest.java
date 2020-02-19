package com.baturu.btrzd.service.business;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.BatchGatheringDetailDTO;
import com.baturu.zd.service.business.BatchGatheringDetailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by caizhuliang on 2019/10/17.
 */
@Slf4j
public class BatchGatheringDetailServiceTest extends BaseTest {

    @Autowired
    private BatchGatheringDetailService batchGatheringDetailService;

    @Test
    public void testQueryByBatchGatheringNo() {
        ResultDTO<List<BatchGatheringDetailDTO>> resultDTO = batchGatheringDetailService.queryByBatchGatheringNo("20191017170702");
        log.info("BatchGatheringDetailServiceTest##testQueryByBatchGatheringNo - resultDTO = {}", resultDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

}
