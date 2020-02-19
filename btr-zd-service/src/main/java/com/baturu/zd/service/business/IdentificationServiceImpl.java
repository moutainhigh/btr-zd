package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.config.datasource.DataSourceType;
import com.baturu.zd.config.datasource.Datasource;
import com.baturu.zd.constant.IdentificationConstant;
import com.baturu.zd.constant.LevelAddressConstant;
import com.baturu.zd.constant.ReservationOrderConstant;
import com.baturu.zd.dto.web.excel.IdentificationExcelDTO;
import com.baturu.zd.dto.wx.IdentificationDTO;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.dto.wx.WxSignDTO;
import com.baturu.zd.entity.wx.IdentificationEntity;
import com.baturu.zd.enums.WxAddressTypeEnum;
import com.baturu.zd.mapper.wx.IdentificationMapper;
import com.baturu.zd.request.business.IdentificationQueryRequest;
import com.baturu.zd.request.server.WxAddressBaseQueryRequest;
import com.baturu.zd.request.wx.WxSignQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.common.AuthenticationService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.service.server.WxAddressQueryService;
import com.baturu.zd.transform.wx.IdentificationTransform;
import com.baturu.zd.util.ObjectValidateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * created by ketao by 2019/03/06
 **/
@Service("identificationService")
@Slf4j
public class IdentificationServiceImpl extends AbstractServiceImpl implements IdentificationService{

    @Autowired
    private WxSignService wxSignService;
    @Autowired
    private IdentificationMapper identificationMapper;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private LevelAddressService levelAddressService;
    @Autowired
    private WxAddressService wxAddressService;
    @Autowired
    private WxAddressQueryService wxAddressQueryService;


    @Transactional(rollbackFor = Throwable.class)
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    @Override
    public ResultDTO<IdentificationDTO> saveIdentification(IdentificationDTO identificationDTO){
        ResultDTO resultDTO = this.checkIdentification(identificationDTO);
        if(resultDTO.isUnSuccess()){
            return resultDTO;
        }
        WxSignDTO wxSgin = authenticationService.getWxSign();
        ResultDTO<List<WxSignDTO>> signResult = wxSignService.selectByParam(WxSignQueryRequest.builder().openId(wxSgin.getOpenId()).build());
        if(signResult.isUnSuccess()||signResult.getModel().size()==0){
            return ResultDTO.failed("注册信息查询为空");
        }
        WxSignDTO wxSignDTO = signResult.getModel().get(0);
        IdentificationEntity identificationEntity = IdentificationTransform.DTO_TO_ENTITY.apply(identificationDTO);
        try {
            if(identificationDTO.getId()==null) {
                identificationEntity.setCreateUserId(wxSignDTO.getId());
                int num = identificationMapper.insert(identificationEntity);
                if (num == 0) {
                    return ResultDTO.failed("认证失败");
                }
                identificationDTO.setId(identificationEntity.getId());
                //实名认证后 创建默认发货地址簿
                this.createDefaultSenderAddress(identificationDTO);
            }else{
                identificationEntity.setUpdateUserId(wxSignDTO.getId());
                int num = identificationMapper.updateById(identificationEntity);
                if (num == 0) {
                    return ResultDTO.failed("修改失败");
                }
            }
            return ResultDTO.succeedWith(identificationDTO);
        } catch (Exception e) {
            log.error("实名认证异常",e);
            return ResultDTO.failed("实名认证异常："+e.getMessage());
        }

    }


    @Override
    public ResultDTO<List<IdentificationDTO>> queryIdentifications(IdentificationQueryRequest identificationQueryRequest){
        if(ObjectValidateUtil.isAllFieldNull(identificationQueryRequest)){
            return ResultDTO.failed("参数为空");
        }
        QueryWrapper wrapper=this.initWrapper(identificationQueryRequest);
        List<IdentificationEntity> list = identificationMapper.selectList(wrapper);
        List<IdentificationDTO> identificationDTOS = Lists2.transform(list, IdentificationTransform.ENTITY_TO_DTO);
        this.fillSignPhone(identificationDTOS);
        identificationDTOS=this.fillAddressLevel(identificationDTOS);
        return ResultDTO.succeedWith(identificationDTOS);
    }



    @Override
    public ResultDTO<PageDTO> queryIdentificationsInPage(IdentificationQueryRequest identificationQueryRequest){
        QueryWrapper wrapper = this.initWrapper(identificationQueryRequest);
        IPage iPage = identificationMapper.selectPage(getPage(identificationQueryRequest.getCurrent(), identificationQueryRequest.getSize()), wrapper);
        List<IdentificationDTO> identifications = Lists2.transform(iPage.getRecords(), IdentificationTransform.ENTITY_TO_DTO);
        this.fillSignPhone(identifications);
        this.fillAddressLevel(identifications);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(identifications);
        pageDTO.setTotal(iPage.getTotal());
        return ResultDTO.succeedWith(pageDTO);
    }

    @Override
    public ResultDTO<PageDTO> querySignWithIdentificationsInPage(IdentificationQueryRequest request) {
        if(request.getStartTime() != null && request.getEndTime() != null && request.getStartTime().after(request.getEndTime())){
            return ResultDTO.failed("客户资料信息::时间参数不合法");
        }
        Page page = getPage(request.getCurrent(), request.getSize());
        List<IdentificationDTO> identificationDTOS = identificationMapper.selectSignWithIdentificationsInPage(page,request);
        this.fillAddressLevel(identificationDTOS);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(identificationDTOS);
        pageDTO.setTotal(page.getTotal());
        return ResultDTO.successfy(pageDTO);
    }

    @Override
    public ResultDTO<IdentificationDTO> updateCustomerType(IdentificationDTO identificationDTO) {
        if(identificationDTO == null || identificationDTO.getId() == null || identificationDTO.getId() <= 0){
            return ResultDTO.failed("客户资料修改::id为空");
        }
        if(identificationDTO.getMonthlyKnots() == null && identificationDTO.getBlack() == null){
            return ResultDTO.failed("客户资料修改::参数为空");
        }
        IdentificationEntity entity = IdentificationEntity.builder()
                .black(identificationDTO.getBlack())
                .monthlyKnots(identificationDTO.getMonthlyKnots())
                .build();
        entity.setId(identificationDTO.getId());
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(identificationDTO.getUpdateUserId());
        Integer result = identificationMapper.updateById(entity);
        if(result <= 0){
            return ResultDTO.failed("客户资料修改::修改失败");
        }
        return ResultDTO.successfy(identificationDTO);
    }

    @Override
    public List<IdentificationExcelDTO> exportIdentificationExcel(IdentificationQueryRequest request) {
        ResultDTO validate = this.validateExportRequest(request);
        if(validate.isUnSuccess()){
            log.info("IdentificationServiceImpl#exportIdentificationExcel#客户资料导出失败,errorMsg:{}",validate.getMsg());
            return Collections.emptyList();
        }
        //通过分页参数限制导出大小
        request.setCurrent(1);
        request.setSize(10000);
        ResultDTO<PageDTO> resultDTO = this.querySignWithIdentificationsInPage(request);
        if(resultDTO.isUnSuccess()){
            log.info("IdentificationServiceImpl#exportIdentificationExcel#客户资料导出失败,errorMsg:{}",validate.getMsg());
            return Collections.emptyList();
        }
        //转换成ExcelDTO
        List<IdentificationDTO> identificationDTOS = resultDTO.getModel().getRecords();
        List<IdentificationExcelDTO> excelDTOS = Lists.newArrayList();
        for(IdentificationDTO identificationDTO : identificationDTOS){
            IdentificationExcelDTO excelDTO = new IdentificationExcelDTO();
            BeanUtils.copyProperties(identificationDTO,excelDTO);
            //设置四级地址 "XX省 XX市 XX区 XX镇"
            if(StringUtils.isNotBlank(identificationDTO.getProvinceName()) && StringUtils.isNotBlank(identificationDTO.getCityName()) && StringUtils.isNotBlank(identificationDTO.getCountyName())){
                excelDTO.setLevelAddress(new StringBuilder()
                        .append(identificationDTO.getProvinceName())
                        .append(" ")
                        .append(identificationDTO.getCityName())
                        .append(" ")
                        .append(identificationDTO.getCountyName())
                        .append(" ")
                        .append(identificationDTO.getTownName())
                        .toString());
            }
            excelDTOS.add(excelDTO);
        }
        return excelDTOS;
    }

    /**
     * 添加手机注册号
     * @param identifications
     */
    private void fillSignPhone(List<IdentificationDTO> identifications){
        if(CollectionUtils.isEmpty(identifications)){
            return;
        }
        Set<Integer> signIds = identifications.stream().map(IdentificationDTO::getCreateUserId).collect(Collectors.toSet());
        ResultDTO<List<WxSignDTO>> resultDTO = wxSignService.selectByParam(WxSignQueryRequest.builder().ids(signIds).build());
        if(resultDTO.isSuccess()){
            Map<Integer, WxSignDTO> signMap = resultDTO.getModel().stream().collect(Collectors.toMap(WxSignDTO::getId, w->w, (w1, w2) -> w1));
            identifications.stream().forEach(identificationDTO -> {
                WxSignDTO wxSignDTO = signMap.get(identificationDTO.getCreateUserId());
                if(wxSignDTO!=null) {
                    identificationDTO.setSignPhone(wxSignDTO.getSignPhone());
                    identificationDTO.setSignId(wxSignDTO.getId());
                    identificationDTO.setSignTime(wxSignDTO.getCreateTime());
                }
            });
        }
    }

    /**
     * 填充地址信息
     * @param identifications
     */
    private List<IdentificationDTO> fillAddressLevel(List<IdentificationDTO> identifications){
        if(CollectionUtils.isEmpty(identifications)){
            return identifications;
        }
        Set<Integer> provinceIds = identifications.stream().map(IdentificationDTO::getProvinceId).collect(Collectors.toSet());
        Set<Integer> cityIds = identifications.stream().map(IdentificationDTO::getCityId).collect(Collectors.toSet());
        Set<Integer> countyIds = identifications.stream().map(IdentificationDTO::getCountyId).collect(Collectors.toSet());
        Set<Integer> townIds = identifications.stream().map(IdentificationDTO::getTownId).collect(Collectors.toSet());
        ResultDTO<Map<String, Map<Integer, String>>> levelMapResult = levelAddressService.getLevelMap(provinceIds, cityIds, countyIds,townIds);
        List<IdentificationDTO> list=Lists.newArrayList();
        if(levelMapResult.isSuccess()) {
            Map<String, Map<Integer, String>> levelMap = levelMapResult.getModel();
            for (IdentificationDTO identificationDTO : identifications) {
                if (levelMap.get(LevelAddressConstant.PROVINCE_MAP_KEY)!=null) {
                    identificationDTO.setProvinceName(levelMap.get(LevelAddressConstant.PROVINCE_MAP_KEY).get(identificationDTO.getProvinceId()));
                }
                if (levelMap.get(LevelAddressConstant.CITY_MAP_KEY)!=null) {
                    identificationDTO.setCityName(levelMap.get(LevelAddressConstant.CITY_MAP_KEY).get(identificationDTO.getCityId()));
                }
                if (levelMap.get(LevelAddressConstant.COUNTY_MAP_KEY)!=null) {
                    identificationDTO.setCountyName(levelMap.get(LevelAddressConstant.COUNTY_MAP_KEY).get(identificationDTO.getCountyId()));
                }
                if(levelMap.get(LevelAddressConstant.TOWN_MAP_KEY) != null){
                    identificationDTO.setTownName(levelMap.get(LevelAddressConstant.TOWN_MAP_KEY).get(identificationDTO.getTownId()));
                }
                list.add(identificationDTO);
            }
            return list;
        }
        return identifications;
    }



    private QueryWrapper initWrapper(IdentificationQueryRequest identificationQueryRequest){
        QueryWrapper wrapper=new QueryWrapper();
        if(identificationQueryRequest.getCreateUserId()!=null){
            wrapper.eq(IdentificationConstant.CREATE_USER_ID,identificationQueryRequest.getCreateUserId());
        }
        if(identificationQueryRequest.getProvinceId() != null && identificationQueryRequest.getProvinceId() > 0){
            wrapper.eq(IdentificationConstant.PROVINCE_ID,identificationQueryRequest.getProvinceId());
        }
        if(identificationQueryRequest.getCityId() != null && identificationQueryRequest.getCityId() > 0){
            wrapper.eq(IdentificationConstant.CITY_ID,identificationQueryRequest.getCityId());
        }
        if(identificationQueryRequest.getCountyId() != null && identificationQueryRequest.getCountyId() > 0){
            wrapper.eq(IdentificationConstant.COUNTY_ID,identificationQueryRequest.getCountyId());
        }
        if(StringUtils.isNotBlank(identificationQueryRequest.getPhone())){
            wrapper.eq(IdentificationConstant.PHONE,identificationQueryRequest.getPhone());
        }
        if(StringUtils.isNotBlank(identificationQueryRequest.getName())){
            wrapper.like(IdentificationConstant.NAME,identificationQueryRequest.getName());
        }
        if(identificationQueryRequest.getActive()==null){
            wrapper.eq(ReservationOrderConstant.ACTIVE,Boolean.TRUE);
        }else {
            wrapper.eq(ReservationOrderConstant.ACTIVE,identificationQueryRequest.getActive());
        }
        return wrapper;
    }

    private ResultDTO checkIdentification(IdentificationDTO identificationDTO){
        if(identificationDTO==null){
            return ResultDTO.failed("参数为空");
        }
        if(StringUtils.isBlank(identificationDTO.getName())){
            return ResultDTO.failed("姓名为空");
        }
        if(StringUtils.isBlank(identificationDTO.getPhone())){
            return ResultDTO.failed("手机号码为空");
        }
        if(identificationDTO.getProvinceId()==null){
            return ResultDTO.failed("省份未选择");
        }
        if(identificationDTO.getCityId()==null){
            return ResultDTO.failed("城市未选择");
        }
        if(identificationDTO.getCountyId()==null){
            return ResultDTO.failed("区域未选择");
        }
        if(identificationDTO.getTownId()==null){
            return ResultDTO.failed("城镇未选择");
        }
        if(StringUtils.isBlank(identificationDTO.getAddress())){
            return ResultDTO.failed("详细地址为空");
        }
        return ResultDTO.succeed();
    }

    /**
     * 实名认证后，自动创建对应的发货地址簿
     * @param identificationDTO
     */
    private void createDefaultSenderAddress(IdentificationDTO identificationDTO) {
        //判断是否已存在默认发货地址
        ResultDTO<List<WxAddressDTO>> isExistResult = wxAddressQueryService.queryByParam(WxAddressBaseQueryRequest.builder()
                .type(WxAddressTypeEnum.SENDER.getType())
                .createUserIds(Sets.newHashSet(identificationDTO.getCreateUserId()))
                .build());
        if(isExistResult.isUnSuccess()){
            log.warn("IdentificationServiceImpl#saveIdentification#createDefaultSenderAddress#errorMsg:{}",isExistResult.getMsg());
            return;
        }
        //没有默认发货地址，设置为默认地址
        Boolean isDefault = CollectionUtils.isEmpty(isExistResult.getModel());
        WxAddressDTO defaultSenderAddress = WxAddressDTO.builder()
                .name(identificationDTO.getName())
                .phone(identificationDTO.getPhone())
                .address(identificationDTO.getAddress())
                .provinceId(identificationDTO.getProvinceId())
                .cityId(identificationDTO.getCityId())
                .countyId(identificationDTO.getCountyId())
                .townId(identificationDTO.getTownId())
                .createUserId(identificationDTO.getCreateUserId())
                .isDefault(isDefault)
                .type(WxAddressTypeEnum.SENDER.getType())
                .build();
        ResultDTO<WxAddressDTO> saveAddressResult = wxAddressService.saveOrUpdate(defaultSenderAddress);
        if(saveAddressResult.isUnSuccess()){
            log.error("用户id【{}】实名认证自动创建地址簿失败#errorMsg:{}",identificationDTO.getCreateUserId(),saveAddressResult.getMsg());
        }
    }

    /**
     * 导出请求校验
     * @param request
     * @return
     */
    private ResultDTO validateExportRequest(IdentificationQueryRequest request) {
        if(request == null){
            return ResultDTO.failed("导出客户资料::参数不能为空");
        }
        //TODO 导出日期限制 2019/4/8
        if(request.getStartTime() != null && request.getEndTime() != null && request.getStartTime().after(request.getEndTime())){
            return ResultDTO.failed("导出客户资料::时间参数无效");
        }
        return ResultDTO.succeed();
    }
}
