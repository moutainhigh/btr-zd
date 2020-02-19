package com.baturu.zd.service.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.EntruckingConstant;
import com.baturu.zd.constant.EntruckingDetailsConstant;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.TransLineDTO;
import com.baturu.zd.dto.app.AppEntruckingDTO;
import com.baturu.zd.dto.app.AppEntruckingDetailsDTO;
import com.baturu.zd.dto.common.EntruckingDTO;
import com.baturu.zd.entity.common.EntruckingDetailsEntity;
import com.baturu.zd.entity.common.EntruckingEntity;
import com.baturu.zd.mapper.common.EntruckingDetailsMapper;
import com.baturu.zd.mapper.common.EntruckingMapper;
import com.baturu.zd.request.business.EntruckingQueryRequest;
import com.baturu.zd.schedule.OrderNoOffset;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.common.TransLineService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.common.Entruckingform;
import com.baturu.zd.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 * create by laijinjie in 2019/3/21
 * 装车单服务实现类 对前端
 */
@Service("entruckingService")
@Slf4j
public class EntruckingServiceImpl extends AbstractServiceImpl implements EntruckingService {

    private static final int TEN = 10;

    @Autowired
    private EntruckingMapper entruckingMapper;
    @Autowired
    private EntruckingDetailsMapper entruckingDetailsMapper;
    @Autowired
    private OrderNoOffset orderNoOffset;
    @Autowired
    private TransLineService transLineService;
    @Autowired
    private PackageService packageService;

    /**
     * 分页查询装车单列表
     *
     * @param request
     * @return
     */
    @Override
    public ResultDTO<PageDTO> queryEntruckingDTOForPage(EntruckingQueryRequest request) {
        ResultDTO validateResult = this.validateRequest(request);
        if (validateResult.isUnSuccess()) {
            return validateResult;
        }
        Page page = super.getPage(request.getCurrent(), request.getSize());
        List<EntruckingEntity> entruckingEntities = entruckingMapper.selectPageList(page, request);
        List<EntruckingDTO> entruckingDTOS = Lists2.transform(entruckingEntities, Entruckingform.ENTITY_TO_DTO);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setTotal(page.getTotal());
        pageDTO.setRecords(entruckingDTOS);
        return ResultDTO.successfy(pageDTO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<EntruckingDTO> saveEntruckingInApp(AppEntruckingDTO appEntruckingDTO) {
        ResultDTO validateAppRequestParamResult = validateAppRequestParam(appEntruckingDTO);
        if (validateAppRequestParamResult.isUnSuccess()) {
            return validateAppRequestParamResult;
        }
        // 保存主记录
        EntruckingEntity entruckingEntity = getEntruckingEntity(appEntruckingDTO);
        ResultDTO<List<TransLineDTO>> transLineResultDTO = transLineService.queryByWarehouseCode(appEntruckingDTO.getReceivingWarehouseCode());
        if (transLineResultDTO.isUnSuccess()) {
            log.error("saveEntruckingInApp error : 查询不到仓库信息。transLineResultDTO = {}", transLineResultDTO);
            return ResultDTO.failed("查询不到仓库信息");
        }
        entruckingEntity.setReceivingWarehouse(transLineResultDTO.getModel().get(0).getFirstWarehouse());
        int insertEntruckingEntityRow = entruckingMapper.insert(entruckingEntity);
        if (insertEntruckingEntityRow <= 0) {
            log.error("saveEntruckingInApp error : 保存装车单记录失败。entruckingEntity = {}", entruckingEntity);
            return ResultDTO.failed("保存装车单记录失败");
        }
        String entruckingNo = getEntruckingNo(entruckingEntity.getId());
        log.debug("saveEntruckingInApp : entruckingNo = {}", entruckingNo);
        entruckingEntity.setEntruckingNo(entruckingNo);

        // 保存明细
        List<AppEntruckingDetailsDTO> appEntruckingDetailsDTOs = appEntruckingDTO.getEntruckingDetails();
        for (AppEntruckingDetailsDTO appEntruckingDetailsDTO : appEntruckingDetailsDTOs) {
            ResultDTO createDetailsResult = createEntruckingDetails(appEntruckingDetailsDTO, entruckingEntity);
            if (createDetailsResult.isUnSuccess()) {
                log.info("saveEntruckingInApp error : 创建装车单明细失败。createDetailsResult = {}", createDetailsResult);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return createDetailsResult;
            }
        }

        // 回填装车单号
        int updateEntruckingEntityRow = entruckingMapper.updateById(entruckingEntity);
        if (updateEntruckingEntityRow <= 0) {
            log.error("saveEntruckingInApp error : 保存装车单号失败。entruckingEntity = {}", entruckingEntity);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultDTO.failed("保存装车单记录失败");
        }
        return ResultDTO.successfy(Entruckingform.ENTITY_TO_DTO.apply(entruckingEntity));
    }

    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO createEntruckingDetails(AppEntruckingDetailsDTO appEntruckingDetailsDTO, EntruckingEntity entruckingEntity) {
        // 查询包裹订单状态，如果不是已开单的状态，则不生成装车明细
        ResultDTO<PackageDTO> packageResultDTO = packageService.queryPackageByPackageNo(appEntruckingDetailsDTO.getPackageNo());
        if (!AppConstant.PACKAGE.STATE_CREATED_ORDER.equals(packageResultDTO.getModel().getState())) {
            return ResultDTO.failed(String.format("【%s】不是已开单状态", packageResultDTO.getModel().getTransportOrderNo()));
        }
        // 更新运单包裹状态
        PackageDTO packageDTO = packageResultDTO.getModel();
        packageDTO.setState(AppConstant.PACKAGE.STATE_ENTRUCKED);
        ResultDTO<PackageDTO> updatePackageResultDTO = packageService.updatePackageById(packageDTO);
        if (updatePackageResultDTO.isUnSuccess()) {
            log.error("createEntruckingDetails error : 更新运单包裹状态失败。updatePackageResultDTO = {}", updatePackageResultDTO);
            return ResultDTO.failed("更新运单包裹状态失败");
        }

        // 添加包裹费用
        BigDecimal totalPayment = entruckingEntity.getTotalPayment();
        if (totalPayment == null) {
            totalPayment = new BigDecimal(0.0);
        }
        BigDecimal add = totalPayment.add(packageDTO.getPayment());
        entruckingEntity.setTotalPayment(add);

        EntruckingDetailsEntity entruckingDetailsEntity = EntruckingDetailsEntity.builder().transportOrderNo(appEntruckingDetailsDTO.getTransportOrderNo())
                .packageNo(appEntruckingDetailsDTO.getPackageNo()).entruckingNo(entruckingEntity.getEntruckingNo()).build();
        entruckingDetailsEntity.setCreateUserId(entruckingEntity.getCreateUserId());
        entruckingDetailsEntity.setCreateTime(entruckingEntity.getCreateTime());
        entruckingDetailsEntity.setActive(true);
        try {
            int insertEntruckingDetailsEntityRow = entruckingDetailsMapper.insert(entruckingDetailsEntity);
            if (insertEntruckingDetailsEntityRow <= 0) {
                log.error("createEntruckingDetails error : 保存装车单明细记录失败。entruckingDetailsEntity = {}", entruckingDetailsEntity);
                return ResultDTO.failed("保存装车单明细记录失败");
            }
        } catch (Throwable t) {
            QueryWrapper<EntruckingDetailsEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(AppConstant.TABLE.PACKAGE_ORDER_NO, appEntruckingDetailsDTO.getPackageNo()).eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
            EntruckingDetailsEntity detailsEntity = entruckingDetailsMapper.selectOne(queryWrapper);
            if (null != detailsEntity) {
                QueryWrapper<EntruckingEntity> wrapper = new QueryWrapper<>();
                wrapper.eq(EntruckingDetailsConstant.ENTRUCKING_NO, detailsEntity.getEntruckingNo()).eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
                EntruckingEntity entity = entruckingMapper.selectOne(wrapper);
                log.info("createEntruckingDetails error : 当前包裹号【{}】被车牌号【{}】装车成功。", detailsEntity.getPackageNo(), entity.getPlateNumber());
                return ResultDTO.failed(String.format("当前包裹号被车牌【%s】装车成功", entity.getPlateNumber()));
            }
            log.info("createEntruckingDetails error : 保存数据时发生运行时异常。", t);
            return ResultDTO.failed("保存装车单明细记录失败");
        }
        return ResultDTO.succeed();
    }

    /**
     * 根据ID生成装车单号
     */
    private String getEntruckingNo(Integer id) {
        Integer orderSeq = id - orderNoOffset.getEntruckingOffset();
        StringBuilder sb = new StringBuilder();
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        // 设置年月日
        sb.append(year);
        if (month < TEN) {
            sb.append(0);
        }
        sb.append(month);
        if (day < TEN) {
            sb.append(0);
        }
        sb.append(day);
        String seq = String.valueOf(orderSeq);
        int length = AppConstant.ENTRUCKING_NUMBER_MAX_SIZE - seq.length();
        for (int i = 0; i < length; i++) {
            sb.append("0");
        }
        sb.append(seq);
        return sb.toString();
    }

    /**
     * 通过AppEntruckingDTO获得EntruckingEntity
     */
    private EntruckingEntity getEntruckingEntity(AppEntruckingDTO appEntruckingDTO) {
        EntruckingEntity entity = EntruckingEntity.builder().plateNumber(appEntruckingDTO.getPlateNumber())
                .transportOrderNum(appEntruckingDTO.getTransportOrderNum()).state(AppConstant.ENTRUCKING_STATE_1).qty(appEntruckingDTO.getQty())
                .servicePointId(appEntruckingDTO.getServicePointId()).bulk(appEntruckingDTO.getBulk()).createUserName(appEntruckingDTO.getCreateUserName())
                .receivingWarehouseCode(appEntruckingDTO.getReceivingWarehouseCode()).build();
        entity.setCreateUserId(appEntruckingDTO.getCreateUserId());
        entity.setCreateTime(DateUtil.getCurrentDate());
        return entity;
    }

    /**
     * 校验APP请求新增装车单的数据
     *
     * @param appEntruckingDTO 装车单信息
     * @return
     */
    private ResultDTO validateAppRequestParam(AppEntruckingDTO appEntruckingDTO) {
        if (null == appEntruckingDTO) {
            return ResultDTO.failed("请填写装车单信息");
        }
        if (StringUtils.isBlank(appEntruckingDTO.getPlateNumber())) {
            return ResultDTO.failed("请填写车牌号码");
        }
        if (null == appEntruckingDTO.getTransportOrderNum() || appEntruckingDTO.getTransportOrderNum() <= 0) {
            return ResultDTO.failed("请填写运单数");
        }
        if (null == appEntruckingDTO.getQty() || appEntruckingDTO.getQty() <= 0) {
            return ResultDTO.failed("请填写总件数");
        }
        if (StringUtils.isBlank(appEntruckingDTO.getReceivingWarehouseCode())) {
            return ResultDTO.failed("请填写目的仓");
        }
        if (CollectionUtils.isEmpty(appEntruckingDTO.getEntruckingDetails())) {
            return ResultDTO.failed("请填写运单明细");
        } else {
            List<AppEntruckingDetailsDTO> entruckingDetails = appEntruckingDTO.getEntruckingDetails();
            for (AppEntruckingDetailsDTO appEntruckingDetailsDTO : entruckingDetails) {
                if (StringUtils.isBlank(appEntruckingDetailsDTO.getPackageNo())) {
                    log.info("EntruckingServiceImpl validateAppRequestParam error : appEntruckingDetailsDTO = {}", appEntruckingDetailsDTO);
                    return ResultDTO.failed("包裹号不能为空");
                }
            }
        }
        return ResultDTO.succeed();
    }

    private QueryWrapper buildWrapper(EntruckingQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if (StringUtils.isNotBlank(request.getEntruckingNo())) {
            wrapper.eq(EntruckingConstant.ENTRUCKING_NO, request.getEntruckingNo());
        }
        if (StringUtils.isNotBlank(request.getPlateNumber())) {
            wrapper.eq(EntruckingConstant.PLATE_NUMBER, request.getPlateNumber());
        }
        if (request.getServicePointId() != null && request.getServicePointId() > 0) {
            wrapper.eq(EntruckingConstant.SERVICE_POINT_ID, request.getServicePointId());
        }
        if (request.getStartTime() != null) {
            wrapper.ge(EntruckingConstant.CREATE_TIME, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            wrapper.le(EntruckingConstant.CREATE_TIME, request.getEndTime());
        }
        return wrapper;
    }

    private ResultDTO validateRequest(EntruckingQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed("查询装车单信息::参数为空");
        }
        return ResultDTO.succeed();
    }
}
