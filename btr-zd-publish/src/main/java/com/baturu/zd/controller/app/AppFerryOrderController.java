package com.baturu.zd.controller.app;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.tms.api.dto.sorting.PackageDTO;
import com.baturu.tms.api.request.sorting.PackageFillingParam;
import com.baturu.tms.api.service.common.sorting.CommonPackageQueryService;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.FerryOrderConstant;
import com.baturu.zd.constant.PageConstant;
import com.baturu.zd.dto.FerryOrderDTO;
import com.baturu.zd.dto.FerryOrderDetailsDTO;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.request.business.AppFerryOrderRequest;
import com.baturu.zd.request.business.FerryOrderQueryRequest;
import com.baturu.zd.service.business.FerryOrderDetailsService;
import com.baturu.zd.service.business.FerryOrderService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuduanyang
 * @since 2019/3/22
 */
@RestController
@Slf4j
@RequestMapping("app/ferryOrder")
public class AppFerryOrderController extends AbstractAppBaseController {

    /** 已拆包状态 */
    private static final Long IS_SPLIT = -1L;
    /** 已取消状态 */
    private static final Integer IS_CANCEL = 1;

    @Autowired
    private FerryOrderService ferryOrderService;

    @Autowired
    private FerryOrderDetailsService ferryOrderDetailsService;

    @Reference(check = false)
    private CommonPackageQueryService commonPackageQueryService;

    /**
     * 创建摆渡单
     * @param ferryOrderRequest
     * @return ResultDTO
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResultDTO save(@RequestBody AppFerryOrderRequest ferryOrderRequest) {
        Integer qty = ferryOrderRequest.getQty();
        BigDecimal amount = ferryOrderRequest.getAmount();
        String packages = ferryOrderRequest.getPackages();

        if (qty <= 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "摆渡单总件数非法");
        }
        if (amount.doubleValue() <= 0.0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "摆渡单总费用非法");
        }
        if (StringUtils.isBlank(packages)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "包裹号必传");
        }

        // 根据包裹号创建摆渡单明细
        List<FerryOrderDetailsDTO> ferryOrderDetails = createFerryOrderDetailsByPackages(packages);

        // 从校验token返回的ResultDO中返回用户信息
        AppUserLoginInfoDTO appUserLoginInfoDTO = getCurrentUserInfo();
        FerryOrderDTO ferryOrderDTO = FerryOrderDTO.builder()
                                            .qty(qty)
                                            .amount(amount)
                                            .payState(FerryOrderConstant.NOT_PAYED)
                                            .createUserId(appUserLoginInfoDTO.getUserId())
                                            .createUserName(appUserLoginInfoDTO.getName())
                                            .updateUserId(appUserLoginInfoDTO.getUserId())
                                            .ferryOrderDetails(ferryOrderDetails)
                                            .build();
        try {
            ferryOrderService.save(ferryOrderDTO);
            ferryOrderDTO.setPackageNoList(Lists.newArrayList(packages.split(",")));
        } catch(Exception e) {
            log.info("创建摆渡单失败");
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_503, "创建摆渡单失败");
        }
        ResultDTO resultDTO = ResultDTO.succeedWith(ferryOrderDTO);
        resultDTO.setErrorCode(AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
        resultDTO.setErrorMsg(null);
        return resultDTO;
    }

    /**
     * 摆渡单列表查询接口
     * @param ferryOrderQueryRequest
     * @return ResultDTO
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultDTO listByPage(FerryOrderQueryRequest ferryOrderQueryRequest) {
        if (ferryOrderQueryRequest.getSize() == null || ferryOrderQueryRequest.getSize() <= 0) {
            ferryOrderQueryRequest.setSize(PageConstant.size);
        }
        if (ferryOrderQueryRequest.getCurrent() == null || ferryOrderQueryRequest.getCurrent() <= 0) {
            ferryOrderQueryRequest.setCurrent(PageConstant.current);
        }
        if (ferryOrderQueryRequest.getPayState() == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "支付状态必传");
        }
        ferryOrderQueryRequest.setCreateUserId(getCurrentUserInfo().getUserId());
        ResultDTO<PageDTO> resultDTO = ferryOrderService.queryForPage(ferryOrderQueryRequest);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        PageDTO pageDTO = resultDTO.getModel();
        List<FerryOrderDTO> records = pageDTO.getRecords();
        // 设置包裹号列表
        for (FerryOrderDTO ferryOrderDTO : records) {
            List<String> packageNoList = ferryOrderDetailsService.getPackageNoList(ferryOrderDTO.getId());
            ferryOrderDTO.setPackageNoList(packageNoList);
        }
        return ResultDTO.succeedWith(records);
    }

    /**
     * 摆渡单扫包裹码获取包裹号接口
     * @param packageId 包裹id
     * @param packageType 包裹类型
     * @return ResultDTO
     */
    @RequestMapping(value = "/package", method = RequestMethod.GET)
    public ResultDTO getByPackageId(Integer packageId, Integer packageType) {
        if (packageId == null || packageId <= 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "包裹id参数错误");
        }
        ResultDTO<PackageDTO> resultDTO = commonPackageQueryService.queryById(packageId, packageType);
        PackageDTO packageDTO = resultDTO.getModel();
        if (resultDTO.isUnSuccess() || null == packageDTO) {
            log.info("getByPackageId error : 查询包裹信息失败。packageId={},packageType={},resultDTO={}", packageId, packageType, resultDTO);
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_503, "查不到包裹");
        }
        if (null != packageDTO.getOrderMapId() && IS_SPLIT.equals(packageDTO.getOrderMapId())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_502, "该包裹状态为已拆包");
        }
        if (null != packageDTO.getIsCancel() && IS_CANCEL.equals(packageDTO.getIsCancel())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_502, "该包裹状态为已取消");
        }
        return ResultDTO.succeedWith(packageDTO.getPackageNo(), AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

    /**
     * 摆渡单包裹号校验接口
     * @param packageNo 包裹号
     * @return
     */
    @RequestMapping(value = "/validatePackageNo/{packageNo}", method = RequestMethod.GET)
    public ResultDTO validatePackageNo(@PathVariable String packageNo) {
        if (StringUtils.isBlank(packageNo)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "包裹号不能为空");
        }
        ResultDTO<PackageDTO> resultDTO = commonPackageQueryService.queryByPackageNo(packageNo, PackageFillingParam.builder().build());
        PackageDTO packageDTO = resultDTO.getModel();

        if (packageDTO == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查询不到相应的包裹数据");
        }

        if (null != packageDTO.getOrderMapId() && IS_SPLIT.compareTo(packageDTO.getOrderMapId()) == 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_502, "该包裹状态为已拆包");
        }
        if (null != packageDTO.getIsCancel() && IS_CANCEL.compareTo(packageDTO.getIsCancel()) == 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_502, "该包裹状态为已取消");
        }
        return ResultDTO.succeedWith(Boolean.TRUE, AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

    /**
     * 根据包裹号创建摆渡单明细
     * @param packages
     * @return
     */
    private List<FerryOrderDetailsDTO> createFerryOrderDetailsByPackages(String packages) {
        String[] packageNos = packages.split(AppConstant.COMMA_SEPARATOR);
        List<FerryOrderDetailsDTO> ferryOrderDetailsDTOS = new ArrayList<>(packageNos.length);
        for (String packageNo : packageNos) {
            FerryOrderDetailsDTO ferryOrderDetailsDTO = new FerryOrderDetailsDTO();
            ferryOrderDetailsDTO.setPackageNo(packageNo);
            ferryOrderDetailsDTOS.add(ferryOrderDetailsDTO);
        }

        return ferryOrderDetailsDTOS;
    }
}
