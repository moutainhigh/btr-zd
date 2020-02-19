package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.config.datasource.DataSourceType;
import com.baturu.zd.config.datasource.Datasource;
import com.baturu.zd.constant.BaseConstant;
import com.baturu.zd.constant.WxAddressConstant;
import com.baturu.zd.dto.common.CityDTO;
import com.baturu.zd.dto.common.CountyDTO;
import com.baturu.zd.dto.common.ProvinceDTO;
import com.baturu.zd.dto.common.TownDTO;
import com.baturu.zd.dto.web.excel.WxAddressExcelDTO;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.dto.wx.WxSignDTO;
import com.baturu.zd.entity.wx.WxAddressEntity;
import com.baturu.zd.mapper.wx.WxAddressMapper;
import com.baturu.zd.request.business.WxAddressQueryRequest;
import com.baturu.zd.request.server.WxAddressBaseQueryRequest;
import com.baturu.zd.request.wx.WxSignQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.common.AuthenticationService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.service.server.WxAddressQueryService;
import com.baturu.zd.transform.wx.WxAddressTransform;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * create by pengdi in 2019/3/6
 * 微信地址簿业务服务
 */
@Service("wxAddressService")
@Slf4j
public class WxAddressServiceImpl extends AbstractServiceImpl implements WxAddressService {
    @Autowired
    WxAddressQueryService wxAddressQueryService;
    @Autowired
    WxAddressMapper wxAddressMapper;
    @Autowired
    private LevelAddressService levelAddressService;
    @Autowired
    private WxSignService wxSignService;
    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public ResultDTO<PageDTO> selectWxAddressDTOForPage(WxAddressQueryRequest request) {
        //分页查询
        QueryWrapper wrapper = this.buildWrapper(request);
        IPage page = getPage(request.getCurrent(), request.getSize());
        page = wxAddressMapper.selectPage(page,wrapper);
        List<WxAddressDTO> wxAddressDTOS = Lists2.transform(page.getRecords(), WxAddressTransform.ENTITY_TO_DTO);
        //根据id填充四级地址名称
        wxAddressDTOS = this.fillLevelAddressName(wxAddressDTOS);
        if(BaseConstant.SOURCE_TYPE_WECHAT.equals(request.getSource())){
            //微信公众号查询需要填充用户创建标识
            wxAddressDTOS = this.fillFlag(wxAddressDTOS,request.getCreateUserIds());
        }
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(wxAddressDTOS);
        pageDTO.setTotal(page.getTotal());
        return ResultDTO.successfy(pageDTO);

    }

    @Override
    public ResultDTO<PageDTO> selectWithSignForPage(WxAddressQueryRequest request) {
        ResultDTO<PageDTO> resultDTO = this.selectWxAddressDTOForPage(request);
        if(resultDTO.isUnSuccess()){
            return resultDTO;
        }
        //填充注册信息
        this.fillWxSignInfo(resultDTO.getModel().getRecords());
        return resultDTO;
    }

    private void fillWxSignInfo(List<WxAddressDTO> wxAddressDTOS) {
        Set<Integer> userIds = wxAddressDTOS.stream().map(WxAddressDTO::getCreateUserId).collect(Collectors.toSet());
        ResultDTO<List<WxSignDTO>> resultDTO = wxSignService.selectByParam(WxSignQueryRequest.builder()
                .ids(userIds)
                .build());
        if(resultDTO.isUnSuccess()){
            log.info("WxAddressServiceImpl#selectWithSignForPage#fillWxSignInfo#errorMsg:获取注册信息失败，{}",resultDTO.getMsg());
            return;
        }else if(CollectionUtils.isEmpty(resultDTO.getModel())){
            log.info("地址簿发现脏数据，createUserId无效,wxAddressDTOS:[{}]",wxAddressDTOS);
            return;
        }
        Map<Integer,WxSignDTO> wxSignId2DTOMap = resultDTO.getModel().stream().collect(Collectors.toMap(o->o.getId(),o->o,(o1,o2)-> o1));
        wxAddressDTOS.stream().forEach(o->{
            if(wxSignId2DTOMap.containsKey(o.getCreateUserId())){
                WxSignDTO wxSignDTO = wxSignId2DTOMap.get(o.getCreateUserId());
                o.setSignId(wxSignDTO.getId());
                o.setSignPhone(wxSignDTO.getSignPhone());
            }else {
                log.info("地址簿发现脏数据，createUserId无效,wxAddressDTO:{}",o);
            }
        });
    }

    private QueryWrapper buildWrapper(WxAddressQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if(request.getType() != null) {
            //不设置默认地址类型原因是，公众号需要获取用户默认地址，一次性获取收货和发货
            wrapper.eq(WxAddressConstant.TYPE, request.getType());
        }
        if(StringUtils.isNotBlank(request.getPhone())){
            wrapper.eq(WxAddressConstant.PHONE, request.getPhone());
        }
        if(request.getId() != null && request.getId() > 0){
            wrapper.eq(WxAddressConstant.ID,request.getId());
        }
        if(request.getIds() != null && request.getIds().size() > 0){
            wrapper.in(WxAddressConstant.ID,request.getIds());
        }
        if(request.getProvinceId() != null && request.getProvinceId() > 0){
            wrapper.eq(WxAddressConstant.PROVINCE_ID,request.getProvinceId());
        }
        if(request.getCityId() != null && request.getCityId() > 0){
            wrapper.eq(WxAddressConstant.CITY_ID,request.getCityId());
        }
        if(request.getCountyId() != null && request.getCountyId() > 0){
            wrapper.eq(WxAddressConstant.COUNTY_ID,request.getCountyId());
        }
        if(request.getTownId() != null && request.getTownId() > 0){
            wrapper.eq(WxAddressConstant.TOWN_ID,request.getTownId());
        }
        if(request.getCreateUserId() != null && request.getCreateUserId() > 0){
            wrapper.eq(WxAddressConstant.CREATE_USER_ID,request.getCreateUserId());
        }
        if(CollectionUtils.isNotEmpty(request.getCreateUserIds())){
            wrapper.in(WxAddressConstant.CREATE_USER_ID,request.getCreateUserIds());
        }
        if(request.getIsDefault() != null){
            wrapper.eq(WxAddressConstant.IS_DEFAULT,request.getIsDefault());
        }
        if(StringUtils.isNotBlank(request.getName())) {
            wrapper.like(WxAddressConstant.NAME,request.getName());
        }
        if(StringUtils.isNotBlank(request.getCompany())){
            wrapper.like(WxAddressConstant.COMPANY,request.getCompany());
        }
        if(StringUtils.isNotBlank(request.getAddress())){
            wrapper.eq(WxAddressConstant.ADDRESS,request.getAddress());
        }
        if(request.getActive() != null){
            wrapper.eq(WxAddressConstant.ACTIVE,request.getActive());
        } else {
            wrapper.eq(WxAddressConstant.ACTIVE,Boolean.TRUE);
        }
        if (!CollectionUtils.isEmpty(request.getCityIds())) {
            wrapper.in(WxAddressConstant.CITY_ID, request.getCityIds());
        }
        if (!CollectionUtils.isEmpty(request.getProvinceIds())) {
            wrapper.in(WxAddressConstant.PROVINCE_ID, request.getProvinceIds());
        }
        if (!CollectionUtils.isEmpty(request.getTownIds())) {
            wrapper.in(WxAddressConstant.TOWN_ID, request.getTownIds());
        }
        if (!CollectionUtils.isEmpty(request.getCountyIds())) {
            wrapper.in(WxAddressConstant.COUNTY_ID, request.getCountyIds());
        }
        wrapper.orderByDesc(WxAddressConstant.CREATE_TIME);
        return wrapper;
    }

    /**
     * 填充四级地址名称
     * @param wxAddressDTOS
     */
    @Override
    public List<WxAddressDTO> fillLevelAddressName(List<WxAddressDTO> wxAddressDTOS) {
        if(CollectionUtils.isEmpty(wxAddressDTOS)){
            return wxAddressDTOS;
        }
        Set<Integer> provinceIds = wxAddressDTOS.stream().map(WxAddressDTO::getProvinceId).collect(Collectors.toSet());
        Set<Integer> cityIds = wxAddressDTOS.stream().map(WxAddressDTO::getCityId).collect(Collectors.toSet());
        Set<Integer> countyIds = wxAddressDTOS.stream().map(WxAddressDTO::getCountyId).collect(Collectors.toSet());
        Set<Integer> townIds = wxAddressDTOS.stream().map(WxAddressDTO::getTownId).collect(Collectors.toSet());

        //填充省份名称
        ResultDTO<List<ProvinceDTO>> provinceResult =  levelAddressService.selectProvincesById(provinceIds);
        if(provinceResult.isSuccess()){
            List<ProvinceDTO> provinceDTOS = provinceResult.getModel();
            Map<Integer,String> provinceId2NameMap = provinceDTOS.stream().collect(Collectors.toMap(ProvinceDTO::getId,ProvinceDTO::getName));
            wxAddressDTOS.stream().forEach(o-> o.setProvinceName(provinceId2NameMap.getOrDefault(o.getProvinceId(), "")));
        }
        //填充城市名称
        ResultDTO<List<CityDTO>> cityResult = levelAddressService.selectCitiesById(cityIds);
        if(cityResult.isSuccess()){
            List<CityDTO> cityDTOS = cityResult.getModel();
            Map<Integer,String> cityId2NameMap = cityDTOS.stream().collect(Collectors.toMap(CityDTO::getId,CityDTO::getName));
            wxAddressDTOS.stream().forEach(o->o.setCityName(cityId2NameMap.getOrDefault(o.getCityId(), "")));
        }
        //填充区县名称
        ResultDTO<List<CountyDTO>> countyResult = levelAddressService.selectCountiesById(countyIds);
        if(countyResult.isSuccess()){
            List<CountyDTO> countyDTOS = countyResult.getModel();
            Map<Integer,String> countyId2NameMap = countyDTOS.stream().collect(Collectors.toMap(CountyDTO::getId,CountyDTO::getName));
            wxAddressDTOS.stream().forEach(o->o.setCountyName(countyId2NameMap.getOrDefault(o.getCountyId(), "")));
        }
        //填充城镇名称
        ResultDTO<List<TownDTO>> townResult = levelAddressService.selectTownsById(townIds);
        if(townResult.isSuccess()){
            Map<Integer,String> townId2NameMap = townResult.getModel().stream().collect(Collectors.toMap(TownDTO::getId,TownDTO::getName));
            wxAddressDTOS.stream().forEach(o -> o.setTownName(townId2NameMap.getOrDefault(o.getTownId(),"")));
        }
        return wxAddressDTOS;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    public ResultDTO<WxAddressDTO> saveOrUpdate(WxAddressDTO wxAddressDTO) {
        ResultDTO validateResult = this.validateParam(wxAddressDTO);
        if(validateResult.isUnSuccess()){
            return validateResult;
        }

        //如果新增地址为默认地址，需要更新之前的默认地址
        if(wxAddressDTO.getIsDefault() != null && wxAddressDTO.getIsDefault()){
            ResultDTO resultDTO = this.updateDefaultAddress(wxAddressDTO);
            if(resultDTO.isUnSuccess()){
                return resultDTO;
            }
        }

        WxAddressEntity wxAddressEntity = WxAddressTransform.DTO_TO_ENTITY.apply(wxAddressDTO);
        Integer result;
        if(wxAddressDTO.getId() == null){
            result = wxAddressMapper.insert(wxAddressEntity);
        }else {
            result = wxAddressMapper.updateById(wxAddressEntity);
        }
        if(result <= 0){
            return ResultDTO.failed("微信地址簿信息::新增/更新失败");
        }
        return ResultDTO.successfy(WxAddressTransform.ENTITY_TO_DTO.apply(wxAddressEntity));
    }

    private ResultDTO<WxAddressDTO> validateParam(WxAddressDTO wxAddressDTO) {
        if(wxAddressDTO.getId() == null){
            if(wxAddressDTO == null){
                return ResultDTO.failed("地址簿信息：参数不能为空！");
            }
            if(wxAddressDTO.getName() == null) {
                return ResultDTO.failed("地址簿信息：姓名不能为空！");
            }
            if(wxAddressDTO.getPhone() == null) {
                return ResultDTO.failed("地址簿信息：电话不能为空！");
            }
            if(wxAddressDTO.getProvinceId() == null) {
                return ResultDTO.failed("地址簿信息：省份不能为空！");
            }
            if(wxAddressDTO.getCityId() == null) {
                return ResultDTO.failed("地址簿信息：市区不能为空！");
            }
            if(wxAddressDTO.getCountyId() == null) {
                return ResultDTO.failed("地址簿信息：区县不能为空！");
            }
            if(wxAddressDTO.getTownId() == null){
                return ResultDTO.failed("地址簿信息：城镇不能为空！");
            }
            if(wxAddressDTO.getAddress() == null) {
                return ResultDTO.failed("地址簿信息：详细地址不能为空！");
            }
            if(wxAddressDTO.getIsDefault() == null) {
                return ResultDTO.failed("地址簿信息：是否默认收货地址不能为空！");
            }
            if(wxAddressDTO.getType() == null) {
                return ResultDTO.failed("地址簿信息：地址类型不能为空！");
            }
        }
        return ResultDTO.succeed();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO deleteById(WxAddressDTO wxAddressDTO) {
        if(wxAddressDTO == null){
            return ResultDTO.failed("微信地址簿删除::参数为空");
        }
        wxAddressDTO.setActive(false);
        wxAddressDTO.setIsDefault(false);
        return this.saveOrUpdate(wxAddressDTO);
    }

    @Override
    public List<WxAddressExcelDTO> exportWxAddressExcel(WxAddressQueryRequest request) {
        //TODO 参数校验
        //设置分页限定导出数量
        request.setCurrent(1);
        request.setSize(25000);
        ResultDTO<PageDTO> resultDTO = this.selectWithSignForPage(request);
        if(resultDTO.isUnSuccess()){
            log.info("WxAddressServiceImpl#exportWxAddressExcel#errorMsg:{}",resultDTO.getMsg());
            return Collections.emptyList();
        }
        List<WxAddressDTO> wxAddressDTOS = resultDTO.getModel().getRecords();
        List<WxAddressExcelDTO> excelDTOS = Lists.newArrayList();
        //转换成ExcelDTO
        for(WxAddressDTO wxAddressDTO : wxAddressDTOS){
            WxAddressExcelDTO excelDTO = new WxAddressExcelDTO();
            BeanUtils.copyProperties(wxAddressDTO,excelDTO);
            //合并四级地址
            if(StringUtils.isNotBlank(wxAddressDTO.getProvinceName()) && StringUtils.isNotBlank(wxAddressDTO.getCityName()) && StringUtils.isNotBlank(wxAddressDTO.getCountyName())){
                excelDTO.setLevelAddress(new StringBuilder()
                        .append(wxAddressDTO.getProvinceName())
                        .append("省 ")
                        .append(wxAddressDTO.getCityName())
                        .append(" ")
                        .append(wxAddressDTO.getCountyName())
                        .append(" ")
                        .append(wxAddressDTO.getTownName())
                        .toString());
            }
            excelDTOS.add(excelDTO);
        }
        return excelDTOS;
    }

    public ResultDTO<WxAddressDTO> updateById(WxAddressDTO wxAddressDTO) {
        if(wxAddressDTO.getId() == null || wxAddressDTO.getId() <= 0){
            return ResultDTO.failed("微信地址簿更新::id为空");
        }
        WxAddressEntity wxAddressEntity = WxAddressTransform.DTO_TO_ENTITY.apply(wxAddressDTO);
        Integer result = wxAddressMapper.updateById(wxAddressEntity);
        if(result <= 0){
            return ResultDTO.failed("微信地址簿更新::更新失败");
        }
        return ResultDTO.successfy(wxAddressDTO);
    }

    /**
     * 修改默认地址时，将上一个默认地址置为非默认
     * @param wxAddressDTO
     * @return
     */
    private ResultDTO updateDefaultAddress(WxAddressDTO wxAddressDTO) {
        WxAddressBaseQueryRequest request = WxAddressBaseQueryRequest.builder()
                .createUserIds(Sets.newHashSet(wxAddressDTO.getUpdateUserId()))
                .type(wxAddressDTO.getType())
                .isDefault(true)
                .build();
        ResultDTO<List<WxAddressDTO>> resultDTO = wxAddressQueryService.queryByParam(request);
        if(resultDTO.isUnSuccess()){
            return resultDTO;
        }
        List<WxAddressDTO> wxAddressDTOS = resultDTO.getModel();
        if(CollectionUtils.isEmpty(resultDTO.getModel())){
            //没有默认地址
            return ResultDTO.succeed();
        }
        WxAddressDTO defaultWxAddressDTO = wxAddressDTOS.get(0);
        //如果是同一条
        if(wxAddressDTO.getId() != null && defaultWxAddressDTO.getId().equals(wxAddressDTO.getId())){
            return ResultDTO.succeed();
        }
        //不同则将之前的默认地址置为非默认
        defaultWxAddressDTO.setIsDefault(false);
        return this.saveOrUpdate(defaultWxAddressDTO);
    }

    /**
     * 填充是否用户创建地址簿标识，微信公众号使用
     * 修改子账号的默认地址为非默认
     * @param wxAddressDTOS
     * @return
     */
    private List<WxAddressDTO> fillFlag(List<WxAddressDTO> wxAddressDTOS, Set<Integer> childIds) {
        WxSignDTO wxSignDTO = authenticationService.getWxSign();
        for(WxAddressDTO wxAddressDTO : wxAddressDTOS){
            //填充用户创建地址簿标识（子账号的也为true）
            if(CollectionUtils.isNotEmpty(childIds)){
                wxAddressDTO.setIsSelf(childIds.contains(wxAddressDTO.getCreateUserId()));
            }else {
                wxAddressDTO.setIsSelf(wxAddressDTO.getCreateUserId().equals(wxSignDTO.getId()));
            }
            //修改来源子账号的地址簿默认标志位false
            if(wxAddressDTO.getIsDefault() && !wxAddressDTO.getCreateUserId().equals(wxSignDTO.getId())){
                wxAddressDTO.setIsDefault(Boolean.FALSE);
            }
        }

        return wxAddressDTOS;
    }
}
