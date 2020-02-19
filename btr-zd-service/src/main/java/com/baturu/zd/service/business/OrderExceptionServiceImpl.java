package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.OrderExceptionConstant;
import com.baturu.zd.dto.*;
import com.baturu.zd.entity.OrderExceptionEntity;
import com.baturu.zd.enums.ExceptionHandleResultEnum;
import com.baturu.zd.enums.ExceptionStateEnum;
import com.baturu.zd.mapper.OrderExceptionMapper;
import com.baturu.zd.request.business.OrderExceptionQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.OrderExceptionTransform;
import com.baturu.zd.util.DateUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 运单异常service
 * @author liuduanyang
 * @since 2019/5/7
 */
@Service("orderExceptionService")
@Slf4j
public class OrderExceptionServiceImpl extends AbstractServiceImpl implements OrderExceptionService {

    @Autowired
    private OrderExceptionMapper orderExceptionMapper;

    @Autowired
    private PackageService packageService;

    @Autowired
    private BlameService blameService;

    @Autowired
    private TransportOrderService transportOrderService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public List<OrderExceptionDTO> getByPackageNo(String packageNo) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq(OrderExceptionConstant.PACKAGE_NO, packageNo);

        List<OrderExceptionDTO> orderExceptionDTOS = Lists2.transform(orderExceptionMapper.selectList(wrapper), OrderExceptionTransform.ENTITY_TO_DTO);

        // 设置异常信息的定责部门
        setBlameDepartment(orderExceptionDTOS);

        return orderExceptionDTOS;
    }

    @Override
    public ResultDTO<OrderExceptionDTO> save(OrderExceptionDTO orderExceptionDTO) {
        orderExceptionDTO.setCreateTime(DateUtil.getCurrentDate());
        orderExceptionDTO.setUpdateTime(DateUtil.getCurrentDate());
        orderExceptionDTO.setHandleResult(ExceptionHandleResultEnum.UNHANDLE.getType());
        orderExceptionDTO.setState(ExceptionStateEnum.WAIT_HANDLE.getType());
        orderExceptionDTO.setActive(true);

        int count = orderExceptionMapper.insert(OrderExceptionTransform.DTO_TO_ENTITY.apply(orderExceptionDTO));
        if (count <= 0) {
            return ResultDTO.failed("创建运单异常信息失败!");
        }

        return ResultDTO.succeed();
    }

    @Override
    public ResultDTO<PageDTO> listByPage(OrderExceptionQueryRequest request) {
        QueryWrapper wrapper = buildWrapper(request);
        Page page = super.getPage(request.getCurrent(), request.getSize());
        IPage iPage = orderExceptionMapper.selectPage(page, wrapper);

        List<OrderExceptionDTO> orderExceptionDTOS = Lists2.transform(iPage.getRecords(), OrderExceptionTransform.ENTITY_TO_DTO);
        // 将orderExceptionDTOS转换成WebOrderExceptionDTO列表
        List<WebOrderExceptionDTO> webOrderExceptionDTOS = new ArrayList<>(orderExceptionDTOS.size());
        for (OrderExceptionDTO orderExceptionDTO : orderExceptionDTOS) {

            // 查询包裹信息
            ResultDTO<PackageDTO> packageResultDTO = packageService.queryPackageByPackageNo(orderExceptionDTO.getPackageNo());
            if (packageResultDTO.isUnSuccess()) {
                log.info("找不到包裹，包裹号={}", orderExceptionDTO.getPackageNo());
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查询不到包裹数据");
            }
            PackageDTO packageDTO = packageResultDTO.getModel();

            // 查询运单信息
            ResultDTO<TransportOrderDTO> transportOrderResultDTO = transportOrderService.queryTransportOrdersByTransportOrderNo(orderExceptionDTO.getTransportOrderNo());
            if (transportOrderResultDTO.isUnSuccess()) {
                log.info("找不到运单，运单号={}", orderExceptionDTO.getTransportOrderNo());
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查询不到运单数据");
            }
            TransportOrderDTO transportOrderDTO = transportOrderResultDTO.getModel();

            // 构造返回给web的WebOrderExceptionDTO
            WebOrderExceptionDTO webOrderExceptionDTO = WebOrderExceptionDTO.builder()
                                                                .id(orderExceptionDTO.getId())
                                                                .transportOrderNo(orderExceptionDTO.getTransportOrderNo())
                                                                .packageNo(orderExceptionDTO.getPackageNo())
                                                                .packageState(packageDTO.getState())
                                                                .bulk(calculateExceptionNumber(orderExceptionDTO.getTransportOrderNo(), transportOrderDTO.getBulk()))
                                                                .payment(calculateExceptionNumber(orderExceptionDTO.getTransportOrderNo(), transportOrderDTO.getTotalPayment()))
                                                                .type(orderExceptionDTO.getType())
                                                                .icon1(orderExceptionDTO.getIcon1())
                                                                .icon2(orderExceptionDTO.getIcon2())
                                                                .icon3(orderExceptionDTO.getIcon3())
                                                                .icon4(orderExceptionDTO.getIcon4())
                                                                .images(getImages(orderExceptionDTO))
                                                                .remark(orderExceptionDTO.getRemark())
                                                                .state(orderExceptionDTO.getState())
                                                                .handleResult(orderExceptionDTO.getHandleResult())
                                                                .reportTime(orderExceptionDTO.getCreateTime())
                                                                .build();

            webOrderExceptionDTOS.add(webOrderExceptionDTO);
        }

        PageDTO pageDTO = new PageDTO();
        pageDTO.setTotal(iPage.getTotal());
        pageDTO.setRecords(webOrderExceptionDTOS);
        return ResultDTO.succeedWith(pageDTO);
    }


    @Override
    public List<OrderExceptionExcelDTO> exportExcel(OrderExceptionQueryRequest request) {
        QueryWrapper wrapper = buildWrapper(request);
        List<OrderExceptionEntity> orderExceptionEntities = orderExceptionMapper.selectList(wrapper);

        List<OrderExceptionExcelDTO> orderExceptionExcelDTOS = new ArrayList<>(orderExceptionEntities.size());
        for (OrderExceptionEntity orderExceptionEntity : orderExceptionEntities) {
            // 查询包裹信息
            ResultDTO<PackageDTO> packageResultDTO = packageService.queryPackageByPackageNo(orderExceptionEntity.getPackageNo());
            if (packageResultDTO.isUnSuccess()) {
                log.info("找不到包裹，包裹号={}", orderExceptionEntity.getPackageNo());
                return null;
            }
            PackageDTO packageDTO = packageResultDTO.getModel();

            // 查询运单信息
            ResultDTO<TransportOrderDTO> resultDTO = transportOrderService.queryTransportOrdersByTransportOrderNo(orderExceptionEntity.getTransportOrderNo());
            if (resultDTO.isUnSuccess()) {
                log.info("查询不到包裹数据,packageNo={}", orderExceptionEntity.getPackageNo());
                return null;
            }
            TransportOrderDTO transportOrderDTO = resultDTO.getModel();

            OrderExceptionExcelDTO orderExceptionExcelDTO = OrderExceptionExcelDTO.builder()
                                                                    .transportOrderNo(orderExceptionEntity.getTransportOrderNo())
                                                                    .packageNo(orderExceptionEntity.getPackageNo())
                                                                    .packageState(packageDTO.getState())
                                                                    .bulk(calculateExceptionNumber(transportOrderDTO.getTransportOrderNo(), transportOrderDTO.getBulk()))
                                                                    .payment(calculateExceptionNumber(transportOrderDTO.getTransportOrderNo(), transportOrderDTO.getTotalPayment()))
                                                                    .type(orderExceptionEntity.getType())
                                                                    .remark(orderExceptionEntity.getRemark())
                                                                    .state(orderExceptionEntity.getState())
                                                                    .handleResult(orderExceptionEntity.getHandleResult())
                                                                    .reportTime(orderExceptionEntity.getCreateTime())
                                                                    .build();
            orderExceptionExcelDTOS.add(orderExceptionExcelDTO);
        }

        return orderExceptionExcelDTOS;
    }

    @Override
    public OrderExceptionDTO getById(Integer id) {
        return OrderExceptionTransform.ENTITY_TO_DTO.apply(orderExceptionMapper.selectById(id));
    }

    @Override
    public ResultDTO updateById(OrderExceptionDTO orderExceptionDTO) {
        orderExceptionDTO.setUpdateTime(DateUtil.getCurrentDate());
        int count = orderExceptionMapper.updateById(OrderExceptionTransform.DTO_TO_ENTITY.apply(orderExceptionDTO));
        if (count <= 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "更新异常记录失败");
        }

        return ResultDTO.succeed();
    }

    @Override
    public ResultDTO listByTransportOrderNo(String transportOrderNo, Integer current, Integer size) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq(OrderExceptionConstant.TRANSPORT_ORDER_NO, transportOrderNo);

        Page page = super.getPage(current, size);
        IPage iPage = orderExceptionMapper.selectPage(page, wrapper);
        List<OrderExceptionDTO> orderExceptionDTOS = Lists2.transform(iPage.getRecords(), OrderExceptionTransform.ENTITY_TO_DTO);


        if (orderExceptionDTOS == null) {
            orderExceptionDTOS = Lists.newArrayList();
        }
        setBlameDepartment(orderExceptionDTOS);

        // 将图片封装成一个list
        for (OrderExceptionDTO orderExceptionDTO : orderExceptionDTOS) {
            // 获取images并设置到orderExceptionDTO
            orderExceptionDTO.setImages(getImages(orderExceptionDTO));
        }

        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(orderExceptionDTOS);
        pageDTO.setTotal(iPage.getTotal());
        return ResultDTO.succeedWith(pageDTO);
    }

    /**
     * 设置异常信息的责任部门
     * @param orderExceptionDTOS
     */
    private void setBlameDepartment(List<OrderExceptionDTO> orderExceptionDTOS) {
        if (CollectionUtils.isEmpty(orderExceptionDTOS)) {
            return;
        }
        for (OrderExceptionDTO orderExceptionDTO : orderExceptionDTOS) {

            List<BlameDTO> blameDTOS = blameService.getByOrderExceptionId(orderExceptionDTO.getId());
            StringBuilder sb = new StringBuilder();
            for (BlameDTO blameDTO : blameDTOS) {
                sb.append(blameDTO.getBlameName());
                sb.append(",");
            }
            String blameName = sb.toString();
            log.info("异常信息责任部门={}", blameName);
            if (StringUtils.isNotBlank(blameName)) {
                blameName = blameName.substring(0, blameName.length() - 1);
            }
            orderExceptionDTO.setBlameName(blameName);
        }
    }

    private QueryWrapper buildWrapper(OrderExceptionQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if (request.getTransportOrderNo() != null) {
            wrapper.eq(OrderExceptionConstant.TRANSPORT_ORDER_NO, request.getTransportOrderNo());
        }
        if (request.getPackageNo() != null) {
            wrapper.eq(OrderExceptionConstant.PACKAGE_NO, request.getPackageNo());
        }
        if (request.getType() != null) {
            wrapper.eq(OrderExceptionConstant.TYPE, request.getType());
        }
        if (request.getState() != null) {
            wrapper.eq(OrderExceptionConstant.STATE, request.getState());
        }
        if (request.getHandleResult() != null) {
            wrapper.eq(OrderExceptionConstant.HANDLE_RESULT, request.getHandleResult());
        }
        if (request.getStartTime() != null) {
            wrapper.gt(OrderExceptionConstant.CREATE_TIME, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            wrapper.lt(OrderExceptionConstant.CREATE_TIME, request.getEndTime());
        }

        wrapper.eq(OrderExceptionConstant.ACTIVE, Boolean.TRUE);
        wrapper.orderByDesc(OrderExceptionConstant.CREATE_TIME);

        return wrapper;
    }

    /**
     * 封装图片到images中
     * @param orderExceptionDTO
     * @return
     */
    private List<String> getImages(OrderExceptionDTO orderExceptionDTO) {
        List<String> images = new ArrayList<String>(4){{
            add(orderExceptionDTO.getIcon1());
            add(orderExceptionDTO.getIcon2());
            add(orderExceptionDTO.getIcon3());
            add(orderExceptionDTO.getIcon4());
        }};

        return images;
    }

    /**
     * 计算异常记录的包裹体积
     * 运单体积/n个包裹
     * @param transportOrderNo 运单号
     * @param number 运单体积或运单费用
     */
    private BigDecimal calculateExceptionNumber(String transportOrderNo, BigDecimal number) {
        if (number == null || number.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        Long totalAmout = packageService.queryTotalAmout(transportOrderNo);
        return number.divide(new BigDecimal(totalAmout), 2, BigDecimal.ROUND_HALF_UP);
    }
}
