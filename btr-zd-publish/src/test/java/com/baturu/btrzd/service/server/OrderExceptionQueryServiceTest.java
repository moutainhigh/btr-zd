package com.baturu.btrzd.service.server;

import com.baturu.btrzd.service.BaseTest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.OrderExceptionDTO;
import com.baturu.zd.service.business.OrderExceptionService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by liuduanyang on 2019/5/8
 */
@Slf4j
public class OrderExceptionQueryServiceTest extends BaseTest {

    @Autowired
    private OrderExceptionService orderExceptionService;

    @Test
    public void testSave() throws Exception {
        OrderExceptionDTO orderExceptionDTO = OrderExceptionDTO.builder()
                                                    .transportOrderId(1)
                                                    .transportOrderNo("W123456")
                                                    .active(true)
                                                    .blameName(null)
                                                    .createTime(new Date())
                                                    .createUserId(1)
                                                    .createUserName("LDy")
                                                    .departmentId(1)
                                                    .departmentName("xxx")
                                                    .images(Lists.newArrayList("http://1", "http://2"))
                                                    .handleResult(1)
                                                    .packageId(1)
                                                    .packageNo("Wxxxx")
                                                    .remark("remark")
                                                    .type(1)
                                                    .updateTime(new Date())
                                                    .updateUserId(1)
                                                    .createUserName("zhangsan")
                                                    .build();
        ResultDTO<OrderExceptionDTO> resultDTO = orderExceptionService.save(orderExceptionDTO);
        Assert.assertTrue(resultDTO.isSuccess());
    }

    @Test
    public void testGetByPackageNo() {
        String packageNo = "Wxxxx";
        List<OrderExceptionDTO> orderExceptionDTOList = orderExceptionService.getByPackageNo(packageNo);
        log.info("orderExceptionDTOList: ", orderExceptionDTOList);
        Assert.assertNotNull(orderExceptionDTOList);
        Assert.assertTrue(orderExceptionDTOList.size() > 0);
    }
}
