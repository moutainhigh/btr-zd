package com.baturu.zd.service.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.common.guava2.Lists2;
import com.baturu.logistics.api.dtos.sociTms.SocialDeliverDatasDTO;
import com.baturu.logistics.api.requestParam.SocialTmsQueryRequest;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.tms.api.constant.transport.PayMethodStateEnum;
import com.baturu.tms.api.constant.transport.ShippingMethodTypeEnum;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.BaseConstant;
import com.baturu.zd.constant.PackageConstant;
import com.baturu.zd.constant.TransportOrderConstant;
import com.baturu.zd.dto.*;
import com.baturu.zd.dto.common.UserDTO;
import com.baturu.zd.dto.web.TransportOrderWebDTO;
import com.baturu.zd.dto.wx.ReservationOrderDTO;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.dto.wx.WxAddressSnapshotDTO;
import com.baturu.zd.dto.wx.WxSignDTO;
import com.baturu.zd.entity.*;
import com.baturu.zd.entity.common.ServiceAreaEntity;
import com.baturu.zd.entity.common.ServicePointEntity;
import com.baturu.zd.entity.common.UserEntity;
import com.baturu.zd.enums.*;
import com.baturu.zd.mapper.*;
import com.baturu.zd.mapper.common.ServiceAreaMapper;
import com.baturu.zd.mapper.common.ServicePointMapper;
import com.baturu.zd.mapper.common.UserMapper;
import com.baturu.zd.request.business.PackageQueryRequest;
import com.baturu.zd.request.business.ServiceAreaQueryRequest;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.request.server.WxAddressSnapshotBaseQueryRequest;
import com.baturu.zd.request.wx.WxSignQueryRequest;
import com.baturu.zd.schedule.OrderNoOffset;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.common.AuthenticationService;
import com.baturu.zd.service.common.TransLineService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.service.dto.web.WebQueryTransportOrderDTO;
import com.baturu.zd.service.server.ReservationOrderQueryService;
import com.baturu.zd.service.server.WxAddressQueryService;
import com.baturu.zd.transform.PackageTransform;
import com.baturu.zd.transform.TransportOrderTransform;
import com.baturu.zd.util.DateUtil;
import com.baturu.zd.util.OrderNoUtil;
import com.baturu.zd.util.PackageUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ketao
 * @since 2019-3-15
 **/
@Service("transportOrderService")
@Slf4j
public class TransportOrderServiceImpl extends AbstractServiceImpl implements TransportOrderService {

    private static final String PACKAGE_NO_SEPARATOR = "_0";
    private static final String PACKAGE_REMARK = "包裹:%s在%s操作%s,操作员:%s";
    private static final String ORDER_REMARK = "运单%s:%s在%s收货开单";

    @Autowired
    private TransportOrderMapper transportOrderMapper;
    @Autowired
    private WxAddressQueryService wxAddressQueryService;
    @Autowired
    private WxAddressSnapshotService wxAddressSnapshotService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private PackageMapper packageMapper;
    @Autowired
    private OrderNoOffset orderNoOffset;
    @Autowired
    private OrderImprintMapper orderImprintMapper;
    @Autowired
    private PackageImprintMapper packageImprintMapper;
    @Autowired
    private TransLineService transLineService;
    @Autowired
    private ServicePointMapper servicePointMapper;
    @Autowired
    private ReservationOrderQueryService reservationOrderQueryService;
    @Autowired
    private ReservationOrderService reservationOrderService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TransLineMapper transLineMapper;
    @Autowired
    private SendMsgService sendMsgService;
    @Autowired
    private PackageService packageService;
    @Autowired
    private WxSignService wxSignService;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;
    @Autowired
    private EditLogService editLogService;
    @Autowired
    private SyncTmsTransportOrderServiceImpl syncTmsTransportOrderService;

    @Override
    public ResultDTO<WebQueryTransportOrderDTO> queryTransportOrdersForWebInPage(TransportOrderQueryRequest request) {
        log.info("TransportOrderServiceImpl#queryTransportOrdersForWebInPage - request = {}", request);
        if (request == null) {
            return ResultDTO.failed("运单查询参数为空");
        }
        // 分页信息
        Page page = super.getPage(request.getCurrent(), request.getSize());
        List<TransportOrderWebDTO> transportOrderWebDTOS = transportOrderMapper.queryTransportOrdersInPage(page, request);
        this.fillCreateUserName(transportOrderWebDTOS);
        PageDTO<TransportOrderWebDTO> pageDTO = new PageDTO<>();
        pageDTO.setRecords(transportOrderWebDTOS);
        pageDTO.setTotal(page.getTotal());
        // 统计各项数据
        Integer sumPackageNum = packageMapper.queryPackageSumByTransportOrderRequest(request);
        BigDecimal sumTotalPayment = BigDecimal.ZERO;
        BigDecimal sumFreight = BigDecimal.ZERO;
        BigDecimal sumNailBoxPayment = BigDecimal.ZERO;
        BigDecimal sumSupportValuePayment = BigDecimal.ZERO;
        BigDecimal sumCollectPayment = BigDecimal.ZERO;
        BigDecimal sumCollectAmount = BigDecimal.ZERO;
        List<TransportOrderWebDTO> transportOrderWebDTOs = transportOrderMapper.queryTransportOrders(request);
        for (TransportOrderWebDTO dto : transportOrderWebDTOs) {
            if (dto.getTotalPayment() != null) {
                sumTotalPayment = sumTotalPayment.add(dto.getTotalPayment());
            }
            if (dto.getFreight() != null) {
                sumFreight = sumFreight.add(dto.getFreight());
            }
            if (dto.getNailBoxPayment() != null) {
                sumNailBoxPayment = sumNailBoxPayment.add(dto.getNailBoxPayment());
            }
            if (dto.getSupportValuePayment() != null) {
                sumSupportValuePayment = sumSupportValuePayment.add(dto.getSupportValuePayment());
            }
            if (dto.getCollectPayment() != null) {
                sumCollectPayment = sumCollectPayment.add(dto.getCollectPayment());
            }
            if (dto.getCollectAmount() != null) {
                sumCollectAmount = sumCollectAmount.add(dto.getCollectAmount());
            }
        }

        return ResultDTO.succeedWith(WebQueryTransportOrderDTO.builder()
                .pageDTO(pageDTO)
                .sumPackageNum(sumPackageNum)
                .sumTotalPayment(sumTotalPayment)
                .sumFreight(sumFreight)
                .sumNailBoxPayment(sumNailBoxPayment)
                .sumSupportValuePayment(sumSupportValuePayment)
                .sumCollectPayment(sumCollectPayment)
                .sumCollectAmount(sumCollectAmount)
                .build());
    }


    @Override
    public ResultDTO<Integer> queryOrderSumByPackageRequest(PackageQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed("包裹汇总查询参数为空");
        }
        return ResultDTO.succeedWith(transportOrderMapper.queryOrderSumByPackageRequest(request));
    }

    @Override
    public ResultDTO<PageDTO<TransportOrderDTO>> queryTransportOrdersInPage(TransportOrderQueryRequest transportOrderQueryRequest) {
        if (transportOrderQueryRequest == null) {
            return ResultDTO.failed("参数为空");
        }
        log.info("运单查询参数：{}", transportOrderQueryRequest);
        QueryWrapper<TransportOrderEntity> wrapper = this.initWrapper(transportOrderQueryRequest);
        Page page = super.getPage(transportOrderQueryRequest.getCurrent(), transportOrderQueryRequest.getSize());
        IPage iPage = transportOrderMapper.selectPage(page, wrapper);
        List<TransportOrderDTO> transportOrderDTOS = Lists2.transform(iPage.getRecords(), TransportOrderTransform.ENTITY_TO_DTO);
        List<TransportOrderDTO> list = fillAddress(transportOrderDTOS);
        log.info("queryTransportOrdersInPage list = {}", list);
        PageDTO pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        pageDTO.setRecords(list);
        return ResultDTO.succeedWith(pageDTO);
    }

    @Override
    public ResultDTO<List<String>> queryTransportOrderByPartnerId(String partnerId, Integer packageState) {
        if (partnerId == null) {
            return ResultDTO.failed("合伙人id为空");
        }
        List<String> transportOrderNos = transportOrderMapper.queryTransportOrderByPartnerId(partnerId, packageState);
        return ResultDTO.succeedWith(transportOrderNos);
    }

    @Override
    public ResultDTO<TransportOrderDTO> queryTransportOrdersByTransportOrderNo(String transportOrderNo) {
        if (StringUtils.isBlank(transportOrderNo)) {
            return ResultDTO.failed("运单号为空");
        }
        QueryWrapper<TransportOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AppConstant.TABLE.TRANSPORT_ORDER_NO_KEY, transportOrderNo)
                .eq(TransportOrderConstant.ACTIVE, Boolean.TRUE);
        List<TransportOrderEntity> entities = transportOrderMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return ResultDTO.failed("查询不到该运单号的详细信息");
        }
        List<TransportOrderDTO> transportOrderDTOS = Lists2.transform(entities, TransportOrderTransform.ENTITY_TO_DTO);
        return ResultDTO.successfy(fillAddress(transportOrderDTOS).get(0));
    }

    /**
     * 新增运单
     *
     * @param transportOrderDTO 运单信息
     * @param reservationId     预约单id，直接生成运单传空
     * @param resource          数据来源
     * @return
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<TransportOrderDTO> insertTransportOrder(TransportOrderDTO transportOrderDTO, Integer reservationId, String resource) {
        //获取预约单号
        ResultDTO getReservationNoResult = getReservationNo(reservationId);
        if (getReservationNoResult.isUnSuccess()) {
            return getReservationNoResult;
        }
        String reservationNo = ObjectUtils.toString(getReservationNoResult.getModel());

        //生成地址簿快照
        ResultDTO recipientResult = createAddressSnapshot(transportOrderDTO, WxAddressTypeEnum.RECIPIENT, resource, reservationId);
        if (recipientResult.isUnSuccess()) {
            return recipientResult;
        }
        ResultDTO senderResult = createAddressSnapshot(transportOrderDTO, WxAddressTypeEnum.SENDER, resource, reservationId);
        if (senderResult.isUnSuccess()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return senderResult;
        }

        //查询网点信息
        ServicePointEntity servicePointEntity = servicePointMapper.selectById(transportOrderDTO.getServicePointId());
        if (null == servicePointEntity) {
            log.error("insertTransportOrder error:查询不到逐道业务网点信息。servicePointId = {}", transportOrderDTO.getServicePointId());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultDTO.failed("查询不到逐道业务网点信息");
        }
        //设置分拣仓
        transportOrderDTO.setWarehouseId(servicePointEntity.getWarehouseId());
        //通过Tms查询路线
        ResultDTO<ZdSocialDeliverDatasDTO> socialDeliverDatasResultDTO = getSocialDeliverDatasDTO(transportOrderDTO, servicePointEntity);
        if (socialDeliverDatasResultDTO.isUnSuccess()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultDTO.failed(socialDeliverDatasResultDTO.getErrorMsg());
        }

        // 生成运单记录和包裹记录
        TransportOrderEntity transportOrderEntity = TransportOrderTransform.DTO_TO_ENTITY.apply(transportOrderDTO);
        List<PackageEntity> packageEntityList = new ArrayList<>(transportOrderEntity.getQty());
        ResultDTO savePackageAndTransportOrderResult = savePackageAndTransportOrder(transportOrderEntity, packageEntityList);
        if (savePackageAndTransportOrderResult.isUnSuccess()) {
            return savePackageAndTransportOrderResult;
        }

        //更新预约单状态为已开单，回填运单号字段
        if (StringUtils.isNotBlank(reservationNo)) {
            ResultDTO updateResult = updateReservationOrder(reservationId, transportOrderEntity);
            if (updateResult.isUnSuccess()) {
                log.error("insertTransportOrder error:更新预约单失败。reservationId={},transportOrderNo={},updateResult={}",
                        reservationId, transportOrderEntity.getTransportOrderNo(), updateResult);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultDTO.failed("更新预约单失败");
            }
        }

        // 生成轨迹信息，状态为开单
        ResultDTO saveImprintResult = saveOrderImprintAndPackageImprint(reservationNo, transportOrderEntity, packageEntityList, reservationId);
        if (saveImprintResult.isUnSuccess()) {
            log.error("insertTransportOrder error:生成运单轨迹和包裹轨迹异常。saveImprintResult = {}", saveImprintResult);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return saveImprintResult;
        }

        // 保存路线信息
        ResultDTO<TransLineDTO> saveTransLineResult = saveTransLine(socialDeliverDatasResultDTO.getModel(), transportOrderEntity);
        if (saveTransLineResult.isUnSuccess()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultDTO.failed(saveTransLineResult.getErrorMsg());
        }

        //异步推送运单和包裹数据到TMS
        sendMsgService.sendTransportOrderAndPackageMessageToTMS(transportOrderDTO, Lists2.transform(packageEntityList, PackageTransform.ENTITY_TO_DTO), saveTransLineResult.getModel());

        transportOrderDTO.setId(transportOrderEntity.getId());
        transportOrderDTO.setTransportOrderNo(transportOrderEntity.getTransportOrderNo());
        return ResultDTO.successfy(transportOrderDTO);
    }

    /**
     * 获取预约单号
     *
     * @param reservationId 预约单ID
     * @return ResultDTO<String>
     */
    private ResultDTO<String> getReservationNo(Integer reservationId) {
        String reservationNo = StringUtils.EMPTY;
        if (reservationId != null && reservationId > 0) {
            ResultDTO<ReservationOrderDTO> reservationOrderDTOResultDTO = reservationOrderQueryService.queryById(reservationId);
            ReservationOrderDTO reservationOrderDTO = reservationOrderDTOResultDTO.getModel();
            if (reservationOrderDTOResultDTO.isUnSuccess() || null == reservationOrderDTO) {
                return ResultDTO.failed("预约单为空");
            }
            // 预约单只有是已预约的状态才能生成运单
            if (!ReservationOrderStateEnum.RESERVE.getType().equals(reservationOrderDTO.getState())) {
                return ResultDTO.failed("此预约单已开单/已取消，请勿重复开单");
            }
            reservationNo = reservationOrderDTO.getReservationNo();
        }
        return ResultDTO.successfy(reservationNo);
    }

    /**
     * 生成运单轨迹和包裹轨迹
     *
     * @param reservationNo        预约单号
     * @param transportOrderEntity 运单记录
     * @param packageEntityList    运单记录底下的包裹记录
     * @param reservationId        预约单ID
     * @return ResultDTO
     */
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO saveOrderImprintAndPackageImprint(String reservationNo, TransportOrderEntity transportOrderEntity, List<PackageEntity> packageEntityList, Integer reservationId) {
        Integer operateType = StringUtils.isNotBlank(reservationNo)
                ? AppConstant.IMPRINT.OPERATE_TYPE_RESERVATION_ORDER : AppConstant.IMPRINT.OPERATE_TYPE_CREATE_ORDER;
        // 运单轨迹（记录日志）
        ResultDTO saveOrderImprintResult = saveOrderImprint(reservationNo, transportOrderEntity, operateType);
        if (saveOrderImprintResult.isUnSuccess()) {
            log.error("saveOrderImprintAndPackageImprint error:保存运单轨迹失败。");
            return saveOrderImprintResult;
        }
        // 包裹轨迹（记录日志）
        ResultDTO savePackageImprintResult = savePackageImprint(packageEntityList, transportOrderEntity, reservationId);
        if (savePackageImprintResult.isUnSuccess()) {
            log.error("saveOrderImprintAndPackageImprint error:保存包裹轨迹失败。");
            return savePackageImprintResult;
        }
        return ResultDTO.succeed();
    }

    /**
     * 获取路线信息
     *
     * @param transportOrderDTO  运单信息
     * @param servicePointEntity 网点信息
     * @return ResultDTO<SocialDeliverDatasDTO>
     */
    private ResultDTO<ZdSocialDeliverDatasDTO> getSocialDeliverDatasDTO(TransportOrderDTO transportOrderDTO, ServicePointEntity servicePointEntity) {
        // 获取四级地址
        Integer provinceId = transportOrderDTO.getRecipientAddr().getProvinceId();
        Integer cityId = transportOrderDTO.getRecipientAddr().getCityId();
        Integer countyId = transportOrderDTO.getRecipientAddr().getCountyId();
        Integer townId = transportOrderDTO.getRecipientAddr().getTownId();
        if (null == provinceId || null == cityId || null == countyId || null == townId) {
            ResultDTO<WxAddressDTO> wxAddressDTOResultDTO = wxAddressQueryService.queryById(transportOrderDTO.getRecipientAddrId());
            if (null == wxAddressDTOResultDTO || wxAddressDTOResultDTO.isUnSuccess() || null == wxAddressDTOResultDTO.getModel()) {
                log.error("getSocialDeliverDatasDTO error : 查询不到收件人信息。recipientAddrId = {}, wxAddressDTOResultDTO = {}",
                        transportOrderDTO.getRecipientAddrId(), wxAddressDTOResultDTO);
                return ResultDTO.failed("查询不到收件人信息");
            }
            WxAddressDTO wxAddressDTO = wxAddressDTOResultDTO.getModel();
            provinceId = wxAddressDTO.getProvinceId();
            cityId = wxAddressDTO.getCityId();
            countyId = wxAddressDTO.getCountyId();
            townId = wxAddressDTO.getTownId();
        }
        // 根据四级地址查到收货点的默认地址，再通过默认地址的四级地址来查询TMS的路线
        ServiceAreaQueryRequest request = ServiceAreaQueryRequest.builder().provinceId(provinceId).cityId(cityId).countyId(countyId).townId(townId).build();
        List<ServiceAreaEntity> areaEntities = serviceAreaMapper.queryDefaultServiceAreasByFour(request);
        if (CollectionUtils.isEmpty(areaEntities)) {
            log.info("getSocialDeliverDatasDTO : 没有配置默认地址。ServiceAreaQueryRequest request = {}", request);
        } else {
            // 目前只取其中一个默认地址即可
            ServiceAreaEntity entity = areaEntities.get(0);
            provinceId = entity.getProvinceId();
            cityId = entity.getCityId();
            countyId = entity.getCountyId();
            townId = entity.getTownId();
        }
        SocialTmsQueryRequest socialTmsQueryRequest = SocialTmsQueryRequest.builder().warehouseCode(servicePointEntity.getWarehouseCode())
                .provinceId(provinceId).cityId(cityId).countyId(countyId).townId(townId).build();
        ResultDTO<SocialDeliverDatasDTO> deliverDatasDTOResultDTO = transLineService.queryTransLine(socialTmsQueryRequest);
        SocialDeliverDatasDTO socialDeliverDatasDTO = deliverDatasDTOResultDTO.getModel();
        if (deliverDatasDTOResultDTO.isUnSuccess() || null == socialDeliverDatasDTO) {
            log.info("getSocialDeliverDatasDTO error : 无法送到运单指定地区。socialTmsQueryRequest = {},deliverDatasDTOResultDTO = {}", socialTmsQueryRequest, deliverDatasDTOResultDTO);
            return ResultDTO.failed("请联系客服人员配置默认地址。");
        }
        ZdSocialDeliverDatasDTO zdSocialDeliverDatasDTO = ZdSocialDeliverDatasDTO.builder().provinceId(provinceId).cityId(cityId).countyId(countyId).townId(townId).build();
        BeanUtils.copyProperties(socialDeliverDatasDTO, zdSocialDeliverDatasDTO);
        return ResultDTO.successfy(zdSocialDeliverDatasDTO);
    }

    /**
     * 生成运单记录和包裹记录
     *
     * @param transportOrderEntity
     * @param packageEntityList
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO savePackageAndTransportOrder(TransportOrderEntity transportOrderEntity, List<PackageEntity> packageEntityList) {
        //保存运单
        int insertTransportOrderRow = transportOrderMapper.insert(transportOrderEntity);
        if (insertTransportOrderRow <= 0) {
            log.error("TransportOrderServiceImpl savePackageAndTransportOrder error:保存运单记录失败。transportOrderEntity = {}", transportOrderEntity);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultDTO.failed("保存运单记录失败");
        }
        Integer orderSeq = transportOrderEntity.getId() - orderNoOffset.getTransportOrderOffset();
        String transportOrderNo = OrderNoUtil.generateOrderNo(AppConstant.TRANSPORT_ORDER_IDENTIFY, orderSeq, AppConstant.TRANSPORT_ORDER_NUMBER_MAX_SIZE);
        transportOrderEntity.setTransportOrderNo(transportOrderNo);
        int updateTransportOrderRow = transportOrderMapper.updateById(transportOrderEntity);
        if (updateTransportOrderRow <= 0) {
            log.error("TransportOrderServiceImpl savePackageAndTransportOrder error:无法将运单号更新到对应的运单记录。transportOrderEntity = {}", transportOrderEntity);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultDTO.failed("保存运单记录失败");
        }
        //保存包裹
        Integer packageNum = transportOrderEntity.getQty();
        //总体积、费用、重量
        BigDecimal sumPayment = transportOrderEntity.getTotalPayment() == null ? BigDecimal.ZERO : transportOrderEntity.getTotalPayment();
        BigDecimal sumBulk = transportOrderEntity.getBulk() == null ? BigDecimal.ZERO : transportOrderEntity.getBulk();
        BigDecimal sumWeight = transportOrderEntity.getWeight() == null ? BigDecimal.ZERO : transportOrderEntity.getWeight();
        //平均体积、费用
        BigDecimal avgPayment = sumPayment.divide(BigDecimal.valueOf(packageNum), 2, RoundingMode.HALF_EVEN);
        BigDecimal avgBulk = sumBulk.divide(BigDecimal.valueOf(packageNum), 2, RoundingMode.HALF_EVEN);
        BigDecimal avgWeight = sumWeight.divide(BigDecimal.valueOf(packageNum), 2, RoundingMode.HALF_EVEN);
        //已分配体积、费用、重量
        BigDecimal allocatedBulk = BigDecimal.ZERO;
        BigDecimal allocatedPayment = BigDecimal.ZERO;
        BigDecimal allocatedWeight = BigDecimal.ZERO;
        Boolean isNailBox = transportOrderEntity.getNailBox();
        Integer nailBoxNum = transportOrderEntity.getNailBoxNum() == null ? 0 : transportOrderEntity.getNailBoxNum();
        for (int i = 0; i < packageNum; i++) {
            //计算体积、费用 最后一个用减法
            BigDecimal payment;
            BigDecimal bulk;
            BigDecimal weight;
            if (i == packageNum - 1) {
                payment = sumPayment.subtract(allocatedPayment);
                bulk = sumBulk.subtract(allocatedBulk);
                weight = sumWeight.subtract(allocatedWeight);
            } else {
                payment = avgPayment;
                bulk = avgBulk;
                weight = avgWeight;
            }
            allocatedBulk = allocatedBulk.add(bulk);
            allocatedPayment = allocatedPayment.add(payment);
            allocatedWeight = allocatedWeight.add(weight);
            //生成包裹码
            String packageNo = createPackageNo(transportOrderNo, i + 1);
            PackageEntity packageEntity = PackageEntity.builder()
                    .packageNo(packageNo)
                    .transportOrderId(transportOrderEntity.getId())
                    .transportOrderNo(transportOrderNo)
                    .bulk(bulk)
                    .weight(weight)
                    .payment(payment)
                    .build();
            //设置钉箱 将前m个包裹设置为钉箱 m:运单钉箱数
            if (isNailBox && i < nailBoxNum) {
                packageEntity.setIsNailBox(Boolean.TRUE);
            } else {
                packageEntity.setIsNailBox(Boolean.FALSE);
            }
            packageEntity.setCreateUserId(transportOrderEntity.getCreateUserId());
            packageEntity.setCreateTime(transportOrderEntity.getCreateTime());
            packageEntity.setActive(true);

            int insertPackageRow = packageMapper.insert(packageEntity);
            if (insertPackageRow <= 0) {
                log.error("TransportOrderServiceImpl savePackageAndTransportOrder error:新增包裹记录失败。packageEntity = {}", packageEntity);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultDTO.failed("保存包裹记录失败。");
            }
            packageEntityList.add(packageEntity);
        }
        return ResultDTO.succeed();
    }


    /**
     * 更新预约单状态为已开单，回填运单号字段
     *
     * @param reservationId
     * @param transportOrderEntity
     */
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO updateReservationOrder(Integer reservationId, TransportOrderEntity transportOrderEntity) {
        ReservationOrderDTO reservationOrderDTO = new ReservationOrderDTO();
        reservationOrderDTO.setId(reservationId);
        reservationOrderDTO.setTransportOrderNo(transportOrderEntity.getTransportOrderNo());
        reservationOrderDTO.setUpdateUserId(transportOrderEntity.getCreateUserId());
        reservationOrderDTO.setState(ReservationOrderStateEnum.ORDERED.getType());
        ResultDTO<ReservationOrderDTO> saveReservationResult = reservationOrderService.saveReservationOrder(reservationOrderDTO);
        return saveReservationResult;
    }

    /**
     * 运单轨迹
     *
     * @param reservationNo
     * @param transportOrderEntity
     * @param operateType
     */
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO saveOrderImprint(String reservationNo, TransportOrderEntity transportOrderEntity, Integer operateType) {
        log.info("reservationNo = {}, transportOrderEntity = {}, operateType = {}", reservationNo, transportOrderEntity, operateType);
        ServicePointEntity servicePointEntity = servicePointMapper.selectById(transportOrderEntity.getServicePointId());
        UserEntity userEntity = userMapper.selectById(transportOrderEntity.getCreateUserId());
        String remark = String.format(ORDER_REMARK, transportOrderEntity != null ? transportOrderEntity.getTransportOrderNo() : StringUtils.EMPTY,
                userEntity != null ? userEntity.getName() : StringUtils.EMPTY,
                servicePointEntity != null ? servicePointEntity.getName() : StringUtils.EMPTY);
        OrderImprintEntity orderImprintEntity = OrderImprintEntity.builder()
                .reservationNo(reservationNo)
                .transportOrderNo(transportOrderEntity.getTransportOrderNo())
                .location(servicePointEntity.getName())
                .remark(remark)
                .operator(userEntity != null ? userEntity.getUsername() : StringUtils.EMPTY)
                .build();
        orderImprintEntity.setOperateType(operateType);
        orderImprintEntity.setCreateUserId(transportOrderEntity.getCreateUserId());
        orderImprintEntity.setCreateTime(transportOrderEntity.getCreateTime());
        orderImprintEntity.setActive(true);
        int row = orderImprintMapper.insert(orderImprintEntity);
        if (row <= 0) {
            return ResultDTO.failed("保存运单轨迹失败");
        }
        return ResultDTO.succeed();
    }

    /**
     * 包裹轨迹
     *
     * @param packageEntityList
     * @param transportOrderEntity
     * @param reservationId
     */
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO savePackageImprint(List<PackageEntity> packageEntityList, TransportOrderEntity transportOrderEntity, Integer reservationId) {
        log.info("packageEntityList = {}, transportOrderEntity = {}, reservationId = {}", packageEntityList, transportOrderEntity, reservationId);
        ServicePointEntity servicePointEntity = servicePointMapper.selectById(transportOrderEntity.getServicePointId());
        UserEntity userEntity = userMapper.selectById(transportOrderEntity.getCreateUserId());
        for (PackageEntity packageEntity : packageEntityList) {

            if (reservationId != null && reservationId > 0) {
                ResultDTO<ReservationOrderDTO> reservationOrderDTOResultDTO = reservationOrderQueryService.queryById(reservationId);
                if (reservationOrderDTOResultDTO.isUnSuccess()) {
                    return reservationOrderDTOResultDTO;
                }
                ReservationOrderDTO reservationOrderDTO = reservationOrderDTOResultDTO.getModel();

                PackageImprintEntity bookOrderImprintEntity = PackageImprintEntity.builder()
                        .operateType(AppConstant.IMPRINT.OPERATE_TYPE_RESERVATION_ORDER)
                        .transportOrderNo(transportOrderEntity.getTransportOrderNo())
                        .packageNo(packageEntity.getPackageNo())
                        .locationId(servicePointEntity.getId())
                        .location(servicePointEntity.getName())
                        .positionId(servicePointEntity.getId())
                        .remark(TransportOrderConstant.BOOK_ORDER_REMARK)
                        .operator(userEntity != null ? userEntity.getUsername() : "")
                        .build();
                bookOrderImprintEntity.setCreateUserId(transportOrderEntity.getCreateUserId());
                bookOrderImprintEntity.setCreateTime(reservationOrderDTO.getCreateTime());
                packageImprintMapper.insert(bookOrderImprintEntity);
            }

            String remark = String.format(PACKAGE_REMARK, packageEntity.getPackageNo(), servicePointEntity.getName(),
                    TransportOrderConstant.OPEN_ORDER, userEntity != null ? userEntity.getName() : StringUtils.EMPTY);
            PackageImprintEntity packageImprintEntity = PackageImprintEntity.builder()
                    .operateType(AppConstant.IMPRINT.OPERATE_TYPE_CREATE_ORDER)
                    .transportOrderNo(transportOrderEntity.getTransportOrderNo())
                    .packageNo(packageEntity.getPackageNo())
                    .locationId(servicePointEntity.getId())
                    .location(servicePointEntity.getName())
                    .remark(remark)
                    .operator(userEntity != null ? userEntity.getUsername() : StringUtils.EMPTY)
                    .build();
            packageImprintEntity.setCreateUserId(transportOrderEntity.getCreateUserId());
            packageImprintEntity.setCreateTime(transportOrderEntity.getCreateTime());
            int row = packageImprintMapper.insert(packageImprintEntity);
            if (row <= 0) {
                return ResultDTO.failed("保存包裹轨迹失败");
            }
        }
        return ResultDTO.succeed();
    }

    /**
     * 保存路线信息
     *
     * @param socialDeliverDatasDTO
     * @param transportOrderEntity
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<TransLineDTO> saveTransLine(ZdSocialDeliverDatasDTO socialDeliverDatasDTO, TransportOrderEntity transportOrderEntity) {
        ResultDTO<TransLineDTO> transLineResultDTO = transLineService.saveTransLine(getTransLineDTO(socialDeliverDatasDTO, transportOrderEntity));
        if (transLineResultDTO.isUnSuccess()) {
            log.info("TransportOrderServiceImpl saveTransLine error:生成路线失败。transLineResultDTO = {}", transLineResultDTO);
            return ResultDTO.failed("此地区无法送达，具体原因：" + transLineResultDTO.getErrorMsg());
        }
        return ResultDTO.successfy(transLineResultDTO.getModel());
    }

    private TransLineDTO getTransLineDTO(ZdSocialDeliverDatasDTO socialDeliverDatasDTO, TransportOrderEntity transportOrderEntity) {
        TransLineDTO transLineDTO = TransLineDTO.builder().bizId(socialDeliverDatasDTO.getBizId()).bizName(socialDeliverDatasDTO.getBizName())
                .firstPosition(socialDeliverDatasDTO.getFirstPosition()).firstPositionCode(socialDeliverDatasDTO.getFirstPositionCode())
                .firstWarehouse(socialDeliverDatasDTO.getFirstWarehouse()).firstWarehouseCode(socialDeliverDatasDTO.getFirstWarehouseCode())
                .fourthPosition(socialDeliverDatasDTO.getFourthPosition()).fourthPositionCode(socialDeliverDatasDTO.getFourthPositionCode())
                .fourthWarehouse(socialDeliverDatasDTO.getFourthWarehouse()).fourthWarehouseCode(socialDeliverDatasDTO.getFourthWarehouseCode())
                .partnerId(socialDeliverDatasDTO.getPartnerId()).partnerName(socialDeliverDatasDTO.getPartnerName()).partnerPhone(socialDeliverDatasDTO.getPartnerPhone())
                .secondPosition(socialDeliverDatasDTO.getSecPosition()).secondPositionCode(socialDeliverDatasDTO.getSecPositionCode())
                .secondWarehouse(socialDeliverDatasDTO.getSecWarehouse()).secondWarehouseCode(socialDeliverDatasDTO.getSecWarehouseCode())
                .thirdPosition(socialDeliverDatasDTO.getThirdPosition()).thirdPositionCode(socialDeliverDatasDTO.getThirdPositionCode())
                .thirdWarehouse(socialDeliverDatasDTO.getThirdWarehouse()).thirdWarehouseCode(socialDeliverDatasDTO.getThirdWarehouseCode())
                .transportOrderNo(transportOrderEntity.getTransportOrderNo()).provinceId(socialDeliverDatasDTO.getProvinceId())
                .cityId(socialDeliverDatasDTO.getCityId()).countyId(socialDeliverDatasDTO.getCountyId()).townId(socialDeliverDatasDTO.getTownId()).build();
        transLineDTO.setCreateUserId(transportOrderEntity.getCreateUserId());
        transLineDTO.setCreateTime(transportOrderEntity.getCreateTime());
        return transLineDTO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO updateTransportOrder(TransportOrderDTO transportOrderDTO, Integer updateUserId) {
        transportOrderDTO.setUpdateUserId(updateUserId);
        transportOrderDTO.setUpdateTime(DateUtil.getCurrentDate());
        TransportOrderEntity entity = TransportOrderTransform.DTO_TO_ENTITY.apply(transportOrderDTO);
        int row = transportOrderMapper.updateById(entity);
        if (row <= 0) {
            log.info("TransportOrderServiceImpl updateTransportOrder error:更新运单失败。entity = {}", entity);
            return ResultDTO.failed("更新运单失败");
        }
        return ResultDTO.succeed();
    }

    /**
     * 根据id作废运单
     *
     * @param transportOrderDTO
     * @param updateUserId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO abolishById(TransportOrderDTO transportOrderDTO, Integer updateUserId) {
        TransportOrderEntity transportOrderEntity = transportOrderMapper.selectById(transportOrderDTO.getId());
        if (transportOrderEntity == null) {
            return ResultDTO.failed("运单不存在，作废操作失败");
        }
        log.info("运单【{}】作废", transportOrderEntity.getTransportOrderNo());
        TransportOrderDTO order = TransportOrderTransform.ENTITY_TO_DTO.apply(transportOrderEntity);
        order.setId(transportOrderDTO.getId());
        order.setState(TransportOrderStateEnum.CANCELED.getType());
        order.setActive(Boolean.TRUE);
        ResultDTO resultDTO = this.updateTransportOrder(order, updateUserId);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        ResultDTO packageResultDTO = packageService.abolishByTransportOrder(transportOrderDTO, updateUserId);
        if (packageResultDTO.isSuccess()) {
            //同步更新tms运单
            log.info("同步tms运单作废信息【{}】",transportOrderEntity.getTransportOrderNo());
            com.baturu.tms.api.dto.transport.TransportOrderDTO tmsTransportOrder =
                    com.baturu.tms.api.dto.transport.TransportOrderDTO.builder().orderNo(transportOrderEntity.getTransportOrderNo())
                            .updateUserId(updateUserId == null ? 0L : updateUserId.longValue()).transportState(com.baturu.tms.api.constant.transport.TransportOrderStateEnum.CANCEL.getType()).build();
            syncTmsTransportOrderService.syncTmsTransportOrder(tmsTransportOrder);
        }
        return packageResultDTO;
    }

    /**
     * 运单的面单消息打印查询接口
     *
     * @param transportOrderNo 运单号
     * @param userName         打印人
     * @return
     */
    @Override
    public ResultDTO queryTransLine(String transportOrderNo, String userName) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("transport_order_no", transportOrderNo);
        TransportOrderEntity transportOrderEntity = transportOrderMapper.selectOne(queryWrapper);
        if (null == transportOrderEntity) {
            return ResultDTO.failed("运单不存在");
        }
        TransportOrderDTO transportOrderDTO = TransportOrderTransform.ENTITY_TO_DTO.apply(transportOrderEntity);
        List<TransportOrderDTO> transportOrderDTOS = new ArrayList<>();
        transportOrderDTOS.add(transportOrderDTO);
        this.fillAddress(transportOrderDTOS);
        WxAddressSnapshotDTO recipientAddr = transportOrderDTO.getRecipientAddr();
        // 前端打印客户字段取值，有公司名称就填公司名称，没有则填收件人
        if (StringUtils.isEmpty(recipientAddr.getCompany())) {
            recipientAddr.setCompany(recipientAddr.getName());
        }
        TransLineEntity transLineEntity = transLineMapper.selectOne(queryWrapper);
        List<PackageEntity> packageEntities = packageMapper.selectList(queryWrapper);
        Map<String, Boolean> collect = packageEntities.stream().collect(Collectors.toMap(PackageEntity::getPackageNo, PackageEntity::getIsNailBox));
        TransLineDTO transLineDTO = new TransLineDTO();
        BeanUtils.copyProperties(transLineEntity, transLineDTO);
        TransLinePackageDTO transLinePackageDTO = TransLinePackageDTO.builder()
                .deliveryType(transportOrderEntity.getDeliveryType())
                .payType(transportOrderEntity.getPayType())
                .remark(transportOrderEntity.getRemark())
                .transLine(transLineDTO)
                .packageNoNailBoxMap(collect)
                .createUserName(userName)
                .createTime(new Date())
                .goodName(transportOrderEntity.getGoodName())
                .recipientAddr(recipientAddr)
                .nailBox(transportOrderDTO.getNailBox())
                .build();
        return ResultDTO.successfy(transLinePackageDTO);
    }

    /**
     * 运单表导出excel
     *
     * @param request
     * @return
     */
    @Override
    public List<TransportOrderWebDTO> queryTransportOrdersExcel(TransportOrderQueryRequest request) {
        List<TransportOrderWebDTO> transportOrderWebDTOS = transportOrderMapper.queryTransportOrdersExcel(request);
        this.fillCreateUserName(transportOrderWebDTOS);
        return transportOrderWebDTOS;
    }


    /**
     * 填充创建人名称
     *
     * @param transportOrderWebDTOS
     */
    private void fillCreateUserName(List<TransportOrderWebDTO> transportOrderWebDTOS) {
        Set<Integer> userIds = transportOrderWebDTOS.stream().map(TransportOrderWebDTO::getCreateUserId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userIds) || userIds.size() > 800) {
            return;
        }
        List<UserDTO> userDTOS = userMapper.queryUserByIds(userIds);
        Map<Integer, String> id2NameMap = userDTOS.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO::getName, (n1, n2) -> n1));
        transportOrderWebDTOS.stream().forEach(t -> {
            t.setCreateUserName(id2NameMap.get(t.getCreateUserId()));
        });
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<TransportOrderDTO> updateTransportOrderState(TransportOrderDTO transportOrderDTO) {
        if (StringUtils.isBlank(transportOrderDTO.getTransportOrderNo())) {
            log.info("updateTransportOrderState error : 没传运单号。transportOrderDTO = {}", transportOrderDTO);
            return ResultDTO.failedWith(transportOrderDTO);
        }
        if (null == transportOrderDTO.getUpdateUserId() || transportOrderDTO.getUpdateUserId().equals(0)) {
            log.info("updateTransportOrderState error : updateUserId不能为空。transportOrderDTO = {}", transportOrderDTO);
            return ResultDTO.failedWith(transportOrderDTO);
        }
        QueryWrapper<TransportOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TransportOrderConstant.TRANSPORT_ORDER_NO, transportOrderDTO.getTransportOrderNo())
                .eq(TransportOrderConstant.ACTIVE, Boolean.TRUE);
        TransportOrderEntity entity = transportOrderMapper.selectOne(queryWrapper);
        if (null == entity) {
            log.info("updateTransportOrderState error : 查询不到该运单。transportOrderDTO = {}", transportOrderDTO);
            return ResultDTO.failedWith(transportOrderDTO);
        }
        entity.setState(transportOrderDTO.getState());
        entity.setUpdateUserId(transportOrderDTO.getUpdateUserId());
        if (null != transportOrderDTO.getUpdateTime()) {
            entity.setUpdateTime(transportOrderDTO.getUpdateTime());
        }
        int row = transportOrderMapper.updateById(entity);
        if (row <= 0) {
            log.info("updateTransportOrderState error : 更新失败。transportOrderDTO = {}", transportOrderDTO);
            return ResultDTO.failedWith(transportOrderDTO);
        }
        return ResultDTO.succeedWith(TransportOrderTransform.ENTITY_TO_DTO.apply(entity));
    }

    @Override
    public ResultDTO<Boolean> isChangeTransportOrderState(int operateType, String transportOrderNo, Collection<String> packageNos) {
        if (0 == operateType) {
            return ResultDTO.failed("操作类型不能为空");
        }
        if (StringUtils.isBlank(transportOrderNo)) {
            return ResultDTO.failed("运单号不能为空");
        }
        if (CollectionUtils.isEmpty(packageNos)) {
            return ResultDTO.failed("包裹号不能为空");
        }
        List<Integer> packageStates = PackageUtils.getHavetoCheckPackageState(operateType);
        if (CollectionUtils.isEmpty(packageStates)) {
            return ResultDTO.succeedWith(Boolean.FALSE);
        }
        QueryWrapper<PackageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TransportOrderConstant.TRANSPORT_ORDER_NO, transportOrderNo).in(PackageConstant.STATE, packageStates).eq(PackageConstant.ACTIVE, Boolean.TRUE);
        List<PackageEntity> list = packageMapper.selectList(queryWrapper);
        int size = CollectionUtils.isEmpty(list) ? 0 : list.size();
        Boolean isChangeTransportOrderState = size == packageNos.size() ? Boolean.TRUE : Boolean.FALSE;
        return ResultDTO.succeedWith(isChangeTransportOrderState);
    }

    /**
     * 根据运单号生成包裹号
     *
     * @param transportOrderNo 运单号
     * @param count            第几个包裹
     * @return 包裹号
     */
    private String createPackageNo(String transportOrderNo, int count) {
        String packageNo = count < 10 ? PACKAGE_NO_SEPARATOR + count : AppConstant.UNDERLINE_SEPARATOR + count;
        packageNo = transportOrderNo + packageNo;
        return packageNo;
    }

    private QueryWrapper<TransportOrderEntity> initWrapper(TransportOrderQueryRequest transportOrderQueryRequest) {
        QueryWrapper<TransportOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TransportOrderConstant.ACTIVE, Boolean.TRUE).orderByDesc(TransportOrderConstant.CREATE_TIME);
        if (StringUtils.isNotBlank(transportOrderQueryRequest.getTransportOrderNo())) {
            // 有运单号就能查
            wrapper.eq(TransportOrderConstant.TRANSPORT_ORDER_NO, transportOrderQueryRequest.getTransportOrderNo());
        }
        WxAddressSnapshotBaseQueryRequest wxAddressSnapshotBaseQueryRequest = compponentWxAddressSnapshotBaseQueryRequest(transportOrderQueryRequest);
        if (wxAddressSnapshotBaseQueryRequest != null) {
            if (BaseConstant.SOURCE_TYPE_WECHAT.equals(transportOrderQueryRequest.getSource())) {
                WxSignDTO wxSign = authenticationService.getWxSign();
                Set<String> phones = this.getSignPhones(wxSign);
                wxAddressSnapshotBaseQueryRequest.setPhones(phones);
            }
            ResultDTO<List<WxAddressSnapshotDTO>> resultDTO = wxAddressSnapshotService.queryByParam(wxAddressSnapshotBaseQueryRequest);
            if (resultDTO.isSuccess() && !CollectionUtils.isEmpty(resultDTO.getModel())) {
                Set<Integer> addressSnapshotIds = resultDTO.getModel().stream().map(WxAddressSnapshotDTO::getId).collect(Collectors.toSet());
                if (WxAddressTypeEnum.SENDER.getType().equals(wxAddressSnapshotBaseQueryRequest.getType())) {
                    wrapper.in(TransportOrderConstant.SENDER_ADDR_ID, addressSnapshotIds);
                } else if (WxAddressTypeEnum.RECIPIENT.getType().equals(wxAddressSnapshotBaseQueryRequest.getType())) {
                    wrapper.in(TransportOrderConstant.RECIPIENT_ADDR_ID, addressSnapshotIds);
                }
            } else {
                wrapper.eq(TransportOrderConstant.RECIPIENT_ADDR_ID, -1);
            }
        }
        if (transportOrderQueryRequest.getState() != null && !AppConstant.STATE_ALL.equals(transportOrderQueryRequest.getState())) {
            wrapper.eq(TransportOrderConstant.STATE, transportOrderQueryRequest.getState());
        }
        if (transportOrderQueryRequest.getStates() != null && transportOrderQueryRequest.getStates().size() > 0) {
            wrapper.in(TransportOrderConstant.STATE, transportOrderQueryRequest.getStates());
        }
        if (transportOrderQueryRequest.getTransportOrderNos() != null && transportOrderQueryRequest.getTransportOrderNos().size() > 0) {
            wrapper.in(TransportOrderConstant.TRANSPORT_ORDER_NO, transportOrderQueryRequest.getTransportOrderNos());
        }
        if (transportOrderQueryRequest.getGatheringStatus() != null) {
            wrapper.eq(TransportOrderConstant.GATHERING_STATUS, transportOrderQueryRequest.getGatheringStatus());
        }
        if (transportOrderQueryRequest.getDays() != null) {
            Date currentDate = DateUtil.getCurrentDate();
            String firstDateStr = DateUtil.formatYYYYMMDD(DateUtil.countdown(transportOrderQueryRequest.getDays() - 1)) + AppConstant.FIRST_DATETIME;
            String currentDateStr = DateUtil.formatYYYYMMDD(currentDate) + AppConstant.LAST_DATETIME;
            wrapper.between(TransportOrderConstant.CREATE_TIME, firstDateStr, currentDateStr);
        }
        if (null != transportOrderQueryRequest.getCreateUserId() && !transportOrderQueryRequest.getCreateUserId().equals(0)) {
            wrapper.eq(TransportOrderConstant.CREATE_USER_ID, transportOrderQueryRequest.getCreateUserId());
        }
        if (null != transportOrderQueryRequest.getServicePointId() && !transportOrderQueryRequest.getServicePointId().equals(0)) {
            wrapper.eq(TransportOrderConstant.SERVICE_POINT_ID, transportOrderQueryRequest.getServicePointId());
        }
        if (null != transportOrderQueryRequest.getWarehouseId() && !transportOrderQueryRequest.getWarehouseId().equals(0)) {
            wrapper.eq(TransportOrderConstant.WAREHOUSE_ID, transportOrderQueryRequest.getWarehouseId());
        }
        if (transportOrderQueryRequest.getType() != null && !transportOrderQueryRequest.getType().equals(0)) {
            wrapper.eq(TransportOrderConstant.TYPE, transportOrderQueryRequest.getType());
        }
        return wrapper;
    }

    private WxAddressSnapshotBaseQueryRequest compponentWxAddressSnapshotBaseQueryRequest(TransportOrderQueryRequest transportOrderQueryRequest) {
        WxAddressSnapshotBaseQueryRequest wxAddressSnapshotBaseQueryRequest = null;
        if (transportOrderQueryRequest.getSelfType() != null) {
            //根据从属类型
            wxAddressSnapshotBaseQueryRequest = WxAddressSnapshotBaseQueryRequest.builder().build();
            if (TransportOrderConstant.AM_SENDER.equals(transportOrderQueryRequest.getSelfType())) {
                //地址类型为发货地址、创建用户获取地址簿快照id
                wxAddressSnapshotBaseQueryRequest.setType(WxAddressTypeEnum.SENDER.getType());
            } else if (TransportOrderConstant.AM_RECIPIENT.equals(transportOrderQueryRequest.getSelfType())) {
                //地址类型为收货地址，收货人手机为登录人手机
                wxAddressSnapshotBaseQueryRequest.setType(WxAddressTypeEnum.RECIPIENT.getType());
            }
        }
        if (StringUtils.isNotBlank(transportOrderQueryRequest.getRecipientPhoneFix()) || StringUtils.isNotBlank(transportOrderQueryRequest.getRecipientName())) {
            if (wxAddressSnapshotBaseQueryRequest == null) {
                wxAddressSnapshotBaseQueryRequest = WxAddressSnapshotBaseQueryRequest.builder().build();
            }
            wxAddressSnapshotBaseQueryRequest.setType(WxAddressTypeEnum.RECIPIENT.getType());
            if (StringUtils.isNotBlank(transportOrderQueryRequest.getRecipientName())) {
                wxAddressSnapshotBaseQueryRequest.setName(transportOrderQueryRequest.getRecipientName());
            }
            if (StringUtils.isNotBlank(transportOrderQueryRequest.getRecipientPhoneFix())) {
                wxAddressSnapshotBaseQueryRequest.setPhone(transportOrderQueryRequest.getRecipientPhoneFix());
            }
        }
        return wxAddressSnapshotBaseQueryRequest;
    }

    /**
     * 母账号获取自己+所有子账号注册手机
     * 子账号获取自身注册手机
     *
     * @param wxSign
     * @return
     */
    private Set<String> getSignPhones(WxSignDTO wxSign) {
        Set<String> phones = Sets.newHashSet(wxSign.getSignPhone());
        ResultDTO<WxSignDTO> signDTOResultDTO = wxSignService.selectById(wxSign.getId());
        if (signDTOResultDTO.isUnSuccess()) {
            return phones;
        }
        if (signDTOResultDTO.getModel().getOwnerId().equals(0)) {
            ResultDTO<List<WxSignDTO>> resultDTO = wxSignService.selectByParam(WxSignQueryRequest.builder().ownerId(wxSign.getId()).build());
            if (resultDTO.isUnSuccess()) {
                log.info("TransportOrderServiceImpl#queryTransportOrdersInPage#initWrapper#getSignPhones#母账号查询失败,errorMsg:{}", resultDTO.getMsg());
                return phones;
            }
            if (CollectionUtils.isEmpty(resultDTO.getModel())) {
                return phones;
            }
            phones.addAll(resultDTO.getModel().stream().map(WxSignDTO::getSignPhone).collect(Collectors.toSet()));
        }
        return phones;
    }

    /**
     * 填充收发货地址信息
     *
     * @param transportOrderDTOS
     */
    private List<TransportOrderDTO> fillAddress(List<TransportOrderDTO> transportOrderDTOS) {
        Set<Integer> senderIds = transportOrderDTOS.stream().map(TransportOrderDTO::getSenderAddrSnapshotId).collect(Collectors.toSet());
        Set<Integer> recipientIds = transportOrderDTOS.stream().map(TransportOrderDTO::getRecipientAddrSnapshotId).collect(Collectors.toSet());
        Set<Integer> ids = Sets.newHashSet();
        ids.addAll(senderIds);
        ids.addAll(recipientIds);
        ResultDTO<List<WxAddressSnapshotDTO>> resultDTO = wxAddressSnapshotService.queryByParam(WxAddressSnapshotBaseQueryRequest.builder().ids(ids).build());
        if (resultDTO.isSuccess()) {
            Map<Integer, WxAddressSnapshotDTO> wxAddressSnapshotDTOMap = resultDTO.getModel().stream().collect(Collectors.toMap(WxAddressSnapshotDTO::getId, w -> w, (w1, w2) -> w1));
            transportOrderDTOS.stream().forEach(t -> {
                t.setSenderAddr(wxAddressSnapshotDTOMap.get(t.getSenderAddrSnapshotId()));
                t.setRecipientAddr(wxAddressSnapshotDTOMap.get(t.getRecipientAddrSnapshotId()));
            });
        }
        return transportOrderDTOS;
    }

    /**
     * 创建地址簿快照
     *
     * @param transportOrderDTO
     * @param type
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO createAddressSnapshot(TransportOrderDTO transportOrderDTO, WxAddressTypeEnum type, String resource, Integer reservationId) {
        ResultDTO<WxAddressSnapshotDTO> resultDTO;

        if (resource.equals(TransportOrderConstant.WEB) && reservationId == null) {
            //WEB直接生成运单，传整个地址簿快照
            WxAddressSnapshotDTO wxAddressSnapshotDTO = type.equals(WxAddressTypeEnum.SENDER) ? transportOrderDTO.getSenderAddr() : transportOrderDTO.getRecipientAddr();
            resultDTO = wxAddressSnapshotService.save(wxAddressSnapshotDTO);
        } else {
            //app、web预约单生成运单，app直接生成运单  都有地址簿id，根据地址簿生成快照
            resultDTO = this.saveAddressSnapshot(transportOrderDTO, type);
        }

        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        WxAddressSnapshotDTO wxAddressSnapshotDTO = resultDTO.getModel();
        if (type.equals(WxAddressTypeEnum.SENDER)) {
            transportOrderDTO.setSenderAddr(wxAddressSnapshotDTO);
            transportOrderDTO.setSenderAddrSnapshotId(wxAddressSnapshotDTO.getId());
        } else if (type.equals(WxAddressTypeEnum.RECIPIENT)) {
            transportOrderDTO.setRecipientAddr(wxAddressSnapshotDTO);
            transportOrderDTO.setRecipientAddrSnapshotId(wxAddressSnapshotDTO.getId());
        }
        return ResultDTO.succeed();
    }

    /**
     * 根据地址簿id生成地址簿快照
     *
     * @param transportOrderDTO
     * @param type
     * @return
     */
    private ResultDTO saveAddressSnapshot(TransportOrderDTO transportOrderDTO, WxAddressTypeEnum type) {
        Integer wxAddressId = type == WxAddressTypeEnum.SENDER ? transportOrderDTO.getSenderAddrId() : transportOrderDTO.getRecipientAddrId();
        //根据id查询地址簿信息
        ResultDTO<WxAddressDTO> wxAddressResult = wxAddressQueryService.queryById(wxAddressId);
        if (wxAddressResult.isUnSuccess()) {
            log.error("新增/更新预约单信息::获取收发货人地址簿失败,运单:[{}],errorMsg:[{}]", transportOrderDTO, wxAddressResult.getMsg());
            return wxAddressResult;
        }
        WxAddressDTO wxAddressDTO = wxAddressResult.getModel();
        return wxAddressSnapshotService.save(wxAddressDTO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO update(TransportOrderLogDTO transportOrderLogDTO) throws Exception {
        ResultDTO<TransportOrderDTO> resultDTO = checkValid(transportOrderLogDTO);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }

        TransportOrderDTO transportOrderDTO = resultDTO.getModel();

        //tms的运单对象
        com.baturu.tms.api.dto.transport.TransportOrderDTO tmsTransportOrder =
                com.baturu.tms.api.dto.transport.TransportOrderDTO.builder().orderNo(transportOrderDTO.getTransportOrderNo()).build();

        Integer senderAddrSnapshotId = transportOrderDTO.getSenderAddrSnapshotId();
        Integer recipientAddrSnapshotId = transportOrderDTO.getRecipientAddrSnapshotId();

        // 运单地址快照修改
        ResultDTO<Map<String, Object>> addressResultDTO = updateAddrSnapshot(tmsTransportOrder, senderAddrSnapshotId, recipientAddrSnapshotId, transportOrderLogDTO);
        if (addressResultDTO.isUnSuccess()) {
            return addressResultDTO;
        }
        // 运单信息修改
        ResultDTO<Map<String, Object>> tranportOrderResultDTO = updateTranportOrder(tmsTransportOrder, transportOrderLogDTO, transportOrderDTO);
        if (tranportOrderResultDTO.isUnSuccess()) {
            return tranportOrderResultDTO;
        }
        // 增加运单修改日志
        Map<String, Object> fieldMap = Maps.newHashMap();
        fieldMap.putAll(addressResultDTO.getModel());
        fieldMap.putAll(tranportOrderResultDTO.getModel());
        ResultDTO result = editLogService.create(transportOrderLogDTO, fieldMap);
        //判断运单修改部分是否需要同步到tms
        if (result.isSuccess() && transportOrderLogDTO.isSync()) {
            tmsTransportOrder.setUpdateUserId(transportOrderLogDTO.getUpdateUserId().longValue());
            syncTmsTransportOrderService.syncTmsTransportOrder(tmsTransportOrder);
        }
        return result;
    }

    @Override
    public ResultDTO<List<TransportOrderDTO>> queryTransportOrders(List<String> transportOrderNos) {
        if (CollectionUtils.isEmpty(transportOrderNos)) {
            ResultDTO.failed("运单号不能为空");
        }
        QueryWrapper<TransportOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(AppConstant.TABLE.TRANSPORT_ORDER_NO_KEY, transportOrderNos)
                .eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
        List<TransportOrderEntity> entities = transportOrderMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(entities)) {
            ResultDTO.failed("查询不到运单信息");
        }
        return ResultDTO.successfy(Lists2.transform(entities, TransportOrderTransform.ENTITY_TO_DTO));
    }

    /**
     * 运单地址快照信息修改
     *
     * @param tmsTransportOrderDTO    tms 运单数据同步对象
     * @param senderAddrSnapshotId
     * @param recipientAddrSnapshotId
     * @param transportOrderLogDTO
     * @return
     */
    private ResultDTO updateAddrSnapshot(com.baturu.tms.api.dto.transport.TransportOrderDTO tmsTransportOrderDTO,
                                         Integer senderAddrSnapshotId, Integer recipientAddrSnapshotId, TransportOrderLogDTO transportOrderLogDTO) {
        // key(字段名) -> value(修改前的值)
        Map<String, Object> sourceMap = new HashMap<>(16);

        ResultDTO<WxAddressSnapshotDTO> senderAddrSnapshotResultDTO = wxAddressSnapshotService.queryById(senderAddrSnapshotId);
        ResultDTO<WxAddressSnapshotDTO> recipientAddrSnapshotResultDTO = wxAddressSnapshotService.queryById(recipientAddrSnapshotId);
        if (senderAddrSnapshotResultDTO.isUnSuccess()) {
            return senderAddrSnapshotResultDTO;
        }
        if (recipientAddrSnapshotResultDTO.isUnSuccess()) {
            return recipientAddrSnapshotResultDTO;
        }
        WxAddressSnapshotDTO senderAddrSnapshotDTO = senderAddrSnapshotResultDTO.getModel();
        WxAddressSnapshotDTO recipientAddrSnapshotDTO = recipientAddrSnapshotResultDTO.getModel();

        if (StringUtils.isNotBlank(transportOrderLogDTO.getSenderName())) {
            sourceMap.put("senderName", senderAddrSnapshotDTO.getName());
            senderAddrSnapshotDTO.setName(transportOrderLogDTO.getSenderName());
            transportOrderLogDTO.setSync(Boolean.TRUE);
            tmsTransportOrderDTO.setSenderName(transportOrderLogDTO.getSenderName());
        }
        if (StringUtils.isNotBlank(transportOrderLogDTO.getSenderPhone())) {
            sourceMap.put("senderPhone", senderAddrSnapshotDTO.getPhone());
            senderAddrSnapshotDTO.setPhone(transportOrderLogDTO.getSenderPhone());
            transportOrderLogDTO.setSync(Boolean.TRUE);
            tmsTransportOrderDTO.setSenderPhone(transportOrderLogDTO.getSenderPhone());
        }
        if (StringUtils.isNotBlank(transportOrderLogDTO.getSenderAddress())) {
            sourceMap.put("senderAddress", senderAddrSnapshotDTO.getAddress());
            senderAddrSnapshotDTO.setAddress(transportOrderLogDTO.getSenderAddress());
        }
        if (StringUtils.isNotBlank(transportOrderLogDTO.getRecipientName())) {
            sourceMap.put("recipientName", recipientAddrSnapshotDTO.getName());
            recipientAddrSnapshotDTO.setName(transportOrderLogDTO.getRecipientName());
            transportOrderLogDTO.setSync(Boolean.TRUE);
            tmsTransportOrderDTO.setRecipientsName(transportOrderLogDTO.getRecipientName());
        }
        if (StringUtils.isNotBlank(transportOrderLogDTO.getRecipientPhone())) {
            sourceMap.put("recipientPhone", recipientAddrSnapshotDTO.getPhone());
            recipientAddrSnapshotDTO.setPhone(transportOrderLogDTO.getRecipientPhone());
            transportOrderLogDTO.setSync(Boolean.TRUE);
            tmsTransportOrderDTO.setRecipientsPhone(transportOrderLogDTO.getRecipientPhone());
        }
        if (StringUtils.isNotBlank(transportOrderLogDTO.getRecipientAddress())) {
            sourceMap.put("recipientAddress", recipientAddrSnapshotDTO.getAddress());
            recipientAddrSnapshotDTO.setAddress(transportOrderLogDTO.getRecipientAddress());
            transportOrderLogDTO.setSync(Boolean.TRUE);
            tmsTransportOrderDTO.setAddress((recipientAddrSnapshotDTO.getProvinceName()+recipientAddrSnapshotDTO.getCityName()
                    +recipientAddrSnapshotDTO.getCountyName()+recipientAddrSnapshotDTO.getTownName()+transportOrderLogDTO.getRecipientAddress()));
        }
        senderAddrSnapshotDTO.setUpdateTime(DateUtil.getCurrentDate());
        senderAddrSnapshotDTO.setUpdateUserId(transportOrderLogDTO.getUpdateUserId());
        recipientAddrSnapshotDTO.setUpdateTime(DateUtil.getCurrentDate());
        recipientAddrSnapshotDTO.setUpdateUserId(transportOrderLogDTO.getUpdateUserId());

        ResultDTO senderAddrResultDTO = wxAddressSnapshotService.update(senderAddrSnapshotDTO);
        ResultDTO recipientAddrResultDTO = wxAddressSnapshotService.update(recipientAddrSnapshotDTO);
        if (senderAddrResultDTO.isUnSuccess()) {
            return senderAddrResultDTO;
        }
        if (recipientAddrResultDTO.isUnSuccess()) {
            return recipientAddrResultDTO;
        }

        return ResultDTO.succeedWith(sourceMap);
    }

    /**
     * 运单信息修改
     *
     * @param tmsTransportOrder tms运单同步数据对象
     * @return
     */
    private ResultDTO<Map<String, Object>> updateTranportOrder(com.baturu.tms.api.dto.transport.TransportOrderDTO tmsTransportOrder
            , TransportOrderLogDTO transportOrderLogDTO, TransportOrderDTO transportOrderDTO) {
        // key(字段名) -> value(修改前的值)
        Map<String, Object> sourceMap = new HashMap<>(16);

        if (transportOrderLogDTO.getDeliveryType() != null) {
            sourceMap.put("deliveryType", transportOrderDTO.getDeliveryType());
            transportOrderDTO.setDeliveryType(transportOrderLogDTO.getDeliveryType());
            transportOrderLogDTO.setSync(Boolean.TRUE);
            tmsTransportOrder.setShippingMethod(DeliveryTypeEnum.HOME.getType().equals(transportOrderLogDTO.getDeliveryType()) ? ShippingMethodTypeEnum.DELIVERYTODOOR.getType() : ShippingMethodTypeEnum.SELFPICK.getType());
        }
        if (transportOrderLogDTO.getWeight() != null) {
            sourceMap.put("weight", transportOrderDTO.getWeight());
            transportOrderDTO.setWeight(transportOrderLogDTO.getWeight());
            transportOrderLogDTO.setSync(Boolean.TRUE);
            tmsTransportOrder.setWeight(transportOrderLogDTO.getWeight());
        }
        if (transportOrderLogDTO.getBulk() != null) {
            sourceMap.put("bulk", transportOrderDTO.getBulk());
            transportOrderDTO.setBulk(transportOrderLogDTO.getBulk());
            transportOrderLogDTO.setSync(Boolean.TRUE);
            tmsTransportOrder.setBulk(transportOrderLogDTO.getBulk().multiply(new BigDecimal("1000000")));
        }
        if (transportOrderLogDTO.getFreight() != null) {
            sourceMap.put("freight", transportOrderDTO.getFreight());
            transportOrderDTO.setFreight(transportOrderLogDTO.getFreight());
            transportOrderLogDTO.setSync(Boolean.TRUE);
            tmsTransportOrder.setVolFreight(transportOrderLogDTO.getFreight());
        }
        if (transportOrderLogDTO.getCollectPayment() != null) {
            sourceMap.put("collectPayment", transportOrderDTO.getCollectPayment());
            transportOrderDTO.setCollectPayment(transportOrderLogDTO.getCollectPayment());
        }
        if (transportOrderLogDTO.getSupportValuePayment() != null) {
            sourceMap.put("supportValuePayment", transportOrderDTO.getSupportValuePayment());
            transportOrderDTO.setSupportValuePayment(transportOrderLogDTO.getSupportValuePayment());
        }
        if (transportOrderLogDTO.getNailBoxPayment() != null) {
            sourceMap.put("nailBoxPayment", transportOrderDTO.getNailBoxPayment());
            transportOrderDTO.setNailBoxPayment(transportOrderLogDTO.getNailBoxPayment());
        }
        if (transportOrderLogDTO.getDispatchPayment() != null) {
            sourceMap.put("dispatchPayment", transportOrderDTO.getDispatchPayment());
            transportOrderDTO.setDispatchPayment(transportOrderLogDTO.getDispatchPayment());
        }
        if (transportOrderLogDTO.getPayType() != null) {
            sourceMap.put("payType", transportOrderDTO.getPayType());
            transportOrderDTO.setPayType(transportOrderLogDTO.getPayType());
            transportOrderLogDTO.setSync(Boolean.TRUE);
            tmsTransportOrder.setPayMethod(PayTypeEnum.NOW.getType().equals(transportOrderLogDTO.getPayType()) ? PayMethodStateEnum.CASH_PAYMENT.getType()
                    : (PayTypeEnum.ARRIVE.getType().equals(transportOrderLogDTO.getPayType()) ? PayMethodStateEnum.WAIT_TO_PAY.getType() : PayMethodStateEnum.ALLOCATED_EXPENSE.getType()));
        }
        if (transportOrderLogDTO.getNowPayment() != null) {
            sourceMap.put("nowPayment", transportOrderDTO.getNowPayment());
            transportOrderDTO.setNowPayment(transportOrderLogDTO.getNowPayment());
        }
        if (transportOrderLogDTO.getTotalPayment() != null) {
            sourceMap.put("totalPayment", transportOrderDTO.getTotalPayment());
            transportOrderDTO.setTotalPayment(transportOrderLogDTO.getTotalPayment());
        }
        if (transportOrderLogDTO.getArrivePayment() != null) {
            sourceMap.put("arrivePayment", transportOrderDTO.getArrivePayment());
            transportOrderDTO.setArrivePayment(transportOrderLogDTO.getArrivePayment());
        }
        ResultDTO resultDTO = updateTransportOrder(transportOrderDTO, transportOrderLogDTO.getUpdateUserId());
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        return ResultDTO.succeedWith(sourceMap);
    }

    /**
     * 检查运单编辑数据的正确性
     *
     * @return
     */
    private ResultDTO checkValid(TransportOrderLogDTO transportOrderLogDTO) {
        ResultDTO<TransportOrderDTO> resultDTO = queryTransportOrdersByTransportOrderNo(transportOrderLogDTO.getIdentification());
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        TransportOrderDTO transportOrderDTO = resultDTO.getModel();
        if (transportOrderDTO == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "运单数据不存在");
        }

        // 已配送不能编辑
        if ((TransportOrderStateEnum.EXPRESSED.getType().equals(transportOrderDTO.getState())
                || TransportOrderStateEnum.CANCELED.getType().equals(transportOrderDTO.getState()))) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "运单已配送不可再编辑");
        }

        Boolean isUpdatePayment = transportOrderLogDTO.getFreight() != null ||
                transportOrderLogDTO.getDispatchPayment() != null ||
                transportOrderLogDTO.getNailBoxPayment() != null ||
                transportOrderLogDTO.getSupportValuePayment() != null ||
                transportOrderLogDTO.getCollectPayment() != null ||
                transportOrderLogDTO.getFreight() != null ||
                transportOrderLogDTO.getNowPayment() != null ||
                transportOrderLogDTO.getTotalPayment() != null;

        log.info("运单信息={}", transportOrderDTO);
        log.info("修改信息={}", transportOrderLogDTO);
        log.info("费用修改={}", isUpdatePayment);

        if (GatheringStatusEnum.PAID.getType().equals(transportOrderDTO.getGatheringStatus())) {
            if (isUpdatePayment || transportOrderLogDTO.getPayType() != null) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "运单已付款，请刷新页面");
            }
        }

        // 修改费用不能为空
        if (transportOrderLogDTO.getFreight() != null && transportOrderLogDTO.getFreight().compareTo(BigDecimal.ZERO) < 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "运费不能小于0");
        }
        if (transportOrderLogDTO.getDispatchPayment() != null && transportOrderLogDTO.getDispatchPayment().compareTo(BigDecimal.ZERO) < 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "配送费不能小于0");
        }
        if (transportOrderLogDTO.getNailBoxPayment() != null && transportOrderLogDTO.getNailBoxPayment().compareTo(BigDecimal.ZERO) < 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "钉箱费不能小于0");
        }
        if (transportOrderLogDTO.getSupportValuePayment() != null && transportOrderLogDTO.getSupportValuePayment().compareTo(BigDecimal.ZERO) < 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "保价费不能小于0");
        }
        if (transportOrderLogDTO.getCollectPayment() != null && transportOrderLogDTO.getCollectPayment().compareTo(BigDecimal.ZERO) < 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "代收费用不能小于0");
        }
        if (transportOrderLogDTO.getNowPayment() != null && transportOrderLogDTO.getNowPayment().compareTo(BigDecimal.ZERO) < 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "现收费用不能小于0");
        }

        return resultDTO;
    }
}
