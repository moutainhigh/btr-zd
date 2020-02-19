package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.DispatchReportDTO;
import com.baturu.zd.dto.DispatchReportDetailDTO;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 报表service实现类
 * @author liuduanyang
 * @since 2019/5/8
 */
@Service("reportService")
@Slf4j
public class ReportServiceImpl extends AbstractServiceImpl implements ReportService {

    @Autowired
    private TransportOrderService transportOrderService;

    @Autowired
    private PackageService packageService;

    @Override
    public ResultDTO<DispatchReportDTO> getDispatchReport(TransportOrderQueryRequest transportOrderQueryRequest) {
        Long totalTransportOrderNum;
        Integer totalPackageNum = 0;
        BigDecimal totalCommision = new BigDecimal("0.00");

        // 查询时间范围内该用户创建的已配送运单
        ResultDTO<PageDTO<TransportOrderDTO>> resultDTO = transportOrderService.queryTransportOrdersInPage(transportOrderQueryRequest);
        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed(resultDTO.getErrorMsg());
        }
        PageDTO<TransportOrderDTO> pageDTO = resultDTO.getModel();
        totalTransportOrderNum = pageDTO.getTotal();

        // 报表明细
        List<TransportOrderDTO> transportOrderDTOS = pageDTO.getRecords();
        List<DispatchReportDetailDTO> dispatchReportDetailDTOS = new ArrayList<>(transportOrderDTOS.size());
        for (TransportOrderDTO transportOrderDTO : transportOrderDTOS) {
            // 每个运单的提成(配送费用+运费的40%)
            BigDecimal commision = new BigDecimal("0.00");
            if (transportOrderDTO.getDispatchPayment() != null) {
                commision =  transportOrderDTO.getDispatchPayment().add(transportOrderDTO.getFreight().multiply(new BigDecimal(0.4)));
            }
            ResultDTO<List<PackageDTO>> listResultDTO =
                    packageService.queryPackagesByTransportOrderNo(transportOrderDTO.getTransportOrderNo(), Collections.emptyList());
            if (listResultDTO.isUnSuccess()) {
                return ResultDTO.failed(listResultDTO.getErrorMsg());
            }

            List<PackageDTO> packageDTOS = listResultDTO.getModel();
            DispatchReportDetailDTO dispatchReportDetailDTO = DispatchReportDetailDTO.builder()
                                                                    .transportOrderNo(transportOrderDTO.getTransportOrderNo())
                                                                    .packageNum(packageDTOS.size())
                                                                    .commision(commision)
                                                                    .customer(transportOrderDTO.getRecipientAddr().getName())
                                                                    .build();
            dispatchReportDetailDTOS.add(dispatchReportDetailDTO);

            // 总提成和总包裹数
            totalPackageNum += packageDTOS.size();
            totalCommision = totalCommision.add(commision);
        }

        DispatchReportDTO dispatchReportDTO = DispatchReportDTO.builder()
                                                    .transportOrderNum(totalTransportOrderNum)
                                                    .packageNum(totalPackageNum)
                                                    .commision(totalCommision.setScale(2, BigDecimal.ROUND_UP))
                                                    .dispatchReportDetailList(dispatchReportDetailDTOS)
                                                    .build();
        return ResultDTO.succeedWith(dispatchReportDTO);
    }
}
