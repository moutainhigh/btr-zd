package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.WxAddressSnapshotConstant;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.dto.wx.WxAddressSnapshotDTO;
import com.baturu.zd.dto.wx.WxSignDTO;
import com.baturu.zd.entity.wx.WxAddressSnapshotEntity;
import com.baturu.zd.mapper.wx.WxAddressSnapshotMapper;
import com.baturu.zd.request.server.WxAddressSnapshotBaseQueryRequest;
import com.baturu.zd.service.common.AuthenticationService;
import com.baturu.zd.transform.wx.WxAddressSnapshotTransform;
import com.baturu.zd.util.ObjectValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * create by pengdi in 2019/3/28
 * 地址簿快照  biz服务
 */
@Service("wxAddressSnapshotService")
@Slf4j
public class WxAddressSnapshotServiceImpl implements WxAddressSnapshotService {
    @Autowired
    private WxAddressSnapshotMapper wxAddressSnapshotMapper;
    @Autowired
    private WxAddressService wxAddressService;
    @Override
    public ResultDTO<List<WxAddressSnapshotDTO>> queryByParam(WxAddressSnapshotBaseQueryRequest request) {
        if(ObjectValidateUtil.isAllFieldNull(request)){
            return ResultDTO.failed("地址簿快照::参数为空");
        }
        QueryWrapper wrapper = this.buildWrapper(request);
        List<WxAddressSnapshotEntity> list = wxAddressSnapshotMapper.selectList(wrapper);
        if(CollectionUtils.isEmpty(list)){
            return ResultDTO.successfy(Collections.emptyList());
        }
        return ResultDTO.successfy(Lists2.transform(list, WxAddressSnapshotTransform.ENTITY_TO_DTO));
    }

    @Override
    public ResultDTO<WxAddressSnapshotDTO> queryById(Integer id) {
        if(id == null || id <= 0){
            return ResultDTO.failed("地址簿快照::id为空");
        }
        WxAddressSnapshotEntity entity = wxAddressSnapshotMapper.selectById(id);
        return ResultDTO.successfy(WxAddressSnapshotTransform.ENTITY_TO_DTO.apply(entity));
    }

    @Override
    public ResultDTO<WxAddressSnapshotDTO> save(WxAddressDTO wxAddressDTO) {
        WxAddressSnapshotDTO wxAddressSnapshotDTO = this.buildAddressSnapshot(wxAddressDTO);
        return this.save(wxAddressSnapshotDTO);
    }

    @Override
    public ResultDTO<WxAddressSnapshotDTO> save(WxAddressSnapshotDTO wxAddressSnapshotDTO) {
        ResultDTO validateResult = this.validateParam(wxAddressSnapshotDTO);
        if(validateResult.isUnSuccess()){
            return validateResult;
        }
        wxAddressSnapshotDTO.setCreateTime(new Date());
        WxAddressSnapshotEntity entity = WxAddressSnapshotTransform.DTO_TO_ENTITY.apply(wxAddressSnapshotDTO);
        entity.setActive(Boolean.TRUE);
        Integer result = wxAddressSnapshotMapper.insert(entity);
        if(result <= 0){
            return ResultDTO.failed("地址簿快照::新增失败");
        }
        wxAddressSnapshotDTO.setId(entity.getId());
        return ResultDTO.successfy(wxAddressSnapshotDTO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<WxAddressSnapshotDTO> updateInReservation(WxAddressSnapshotDTO wxAddressSnapshotDTO) {
        //更新地址簿快照
        if(wxAddressSnapshotDTO == null || wxAddressSnapshotDTO.getId() == null || wxAddressSnapshotDTO.getId() <= 0){
            return ResultDTO.failed("修改地址簿快照::参数为空");
        }
        WxAddressSnapshotEntity entity = WxAddressSnapshotTransform.DTO_TO_ENTITY.apply(wxAddressSnapshotDTO);
        Integer result = wxAddressSnapshotMapper.updateById(entity);
        if(result <= 0){
            log.warn("WxAddressSnapshotServiceImpl#updateInReservation#地址簿快照修改失败，wxAddressSnapshotDTO:{}",wxAddressSnapshotDTO);
            return ResultDTO.failed("地址簿快照::更新失败");
        }
        //更新快照来源地址簿
        WxAddressDTO wxAddressDTO = this.buildSourceAddress(wxAddressSnapshotDTO);
        ResultDTO<WxAddressDTO> updateSourceResult = wxAddressService.saveOrUpdate(wxAddressDTO);
        if(updateSourceResult.isUnSuccess()){
            log.warn("更新地址簿快照失败,wxAddressSnapshotDTO:[{}],errorMsg:{}",wxAddressSnapshotDTO,updateSourceResult.getMsg());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultDTO.failed("更新地址簿快照失败::[" + updateSourceResult.getMsg() + "]");
        }
        return ResultDTO.successfy(wxAddressSnapshotDTO);
    }

    /**
     * 构建(来源)地址簿
     * @param wxAddressSnapshotDTO
     * @return
     */
    private WxAddressDTO buildSourceAddress(WxAddressSnapshotDTO wxAddressSnapshotDTO) {
        return WxAddressDTO.builder()
                .id(wxAddressSnapshotDTO.getSourceId())
                .name(wxAddressSnapshotDTO.getName())
                .phone(wxAddressSnapshotDTO.getPhone())
                .provinceId(wxAddressSnapshotDTO.getProvinceId())
                .cityId(wxAddressSnapshotDTO.getCityId())
                .countyId(wxAddressSnapshotDTO.getCountyId())
                .address(wxAddressSnapshotDTO.getAddress())
                .company(wxAddressSnapshotDTO.getCompany())
                .isDefault(wxAddressSnapshotDTO.getIsDefault())
                .type(wxAddressSnapshotDTO.getType())
                .updateUserId(wxAddressSnapshotDTO.getUpdateUserId())
                .build();
    }


    /**
     * 构建地址簿快照
     * @param wxAddressDTO
     * @return
     */
    private WxAddressSnapshotDTO buildAddressSnapshot(WxAddressDTO wxAddressDTO) {
        return WxAddressSnapshotDTO.builder()
                .name(wxAddressDTO.getName())
                .phone(wxAddressDTO.getPhone())
                .provinceId(wxAddressDTO.getProvinceId())
                .provinceName(wxAddressDTO.getProvinceName())
                .cityId(wxAddressDTO.getCityId())
                .cityName(wxAddressDTO.getCityName())
                .countyId(wxAddressDTO.getCountyId())
                .countyName(wxAddressDTO.getCountyName())
                .townId(wxAddressDTO.getTownId())
                .townName(wxAddressDTO.getTownName())
                .address(wxAddressDTO.getAddress())
                .company(wxAddressDTO.getCompany())
                .isDefault(wxAddressDTO.getIsDefault())
                .type(wxAddressDTO.getType())
                .createUserId(wxAddressDTO.getCreateUserId())
                .sourceId(wxAddressDTO.getId())
                .build();
    }

    private QueryWrapper buildWrapper(WxAddressSnapshotBaseQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();

        if(CollectionUtils.isNotEmpty(request.getIds())){
            wrapper.in(WxAddressSnapshotConstant.ID,request.getIds());
        }
        if(CollectionUtils.isNotEmpty(request.getCreateUserIds())){
            wrapper.in(WxAddressSnapshotConstant.CREATE_USER_ID,request.getCreateUserIds());
        }
        if(request.getPhone() != null){
            wrapper.likeLeft(WxAddressSnapshotConstant.PHONE,request.getPhone());
        }
        if(CollectionUtils.isNotEmpty(request.getPhones())){
            wrapper.in(WxAddressSnapshotConstant.PHONE,request.getPhones());
        }
        if(request.getType() != null){
            wrapper.eq(WxAddressSnapshotConstant.TYPE,request.getType());
        }
        if(request.getIsDefault() != null){
            wrapper.eq(WxAddressSnapshotConstant.IS_DEFAULT,request.getIsDefault());
        }
        if(request.getName() != null) {
            wrapper.eq(WxAddressSnapshotConstant.NAME,request.getName());
        }
        if(request.getAddress() != null){
            wrapper.eq(WxAddressSnapshotConstant.ADDRESS,request.getAddress());
        }
        if(request.getActive() != null){
            wrapper.eq(WxAddressSnapshotConstant.ACTIVE,request.getActive());
        } else {
            wrapper.eq(WxAddressSnapshotConstant.ACTIVE,Boolean.TRUE);
        }
        return wrapper;
    }

    private ResultDTO validateParam(WxAddressSnapshotDTO wxAddressSnapshotDTO) {
        if(wxAddressSnapshotDTO == null){
            return ResultDTO.failed("地址簿快照信息：参数不能为空！");
        }
        if(wxAddressSnapshotDTO.getName() == null) {
            return ResultDTO.failed("地址簿快照信息：姓名不能为空！");
        }
        if(wxAddressSnapshotDTO.getPhone() == null) {
            return ResultDTO.failed("地址簿快照信息：电话不能为空！");
        }
        if(wxAddressSnapshotDTO.getProvinceId() == null) {
            return ResultDTO.failed("地址簿快照信息：省份不能为空！");
        }
        if(wxAddressSnapshotDTO.getCityId() == null) {
            return ResultDTO.failed("地址簿快照信息：市区不能为空！");
        }
        if(wxAddressSnapshotDTO.getCountyId() == null) {
            return ResultDTO.failed("地址簿快照信息：区县不能为空！");
        }
        if(wxAddressSnapshotDTO.getAddress() == null) {
            return ResultDTO.failed("地址簿快照信息：详细地址不能为空！");
        }
        if(wxAddressSnapshotDTO.getIsDefault() == null) {
            return ResultDTO.failed("地址簿快照信息：是否默认收货地址不能为空！");
        }
        if(wxAddressSnapshotDTO.getType() == null) {
            return ResultDTO.failed("地址簿快照信息：地址类型不能为空！");
        }
        return ResultDTO.succeed();
    }

    @Override
    public ResultDTO update(WxAddressSnapshotDTO wxAddressSnapshotDTO) {
        int count = wxAddressSnapshotMapper.updateById(WxAddressSnapshotTransform.DTO_TO_ENTITY.apply(wxAddressSnapshotDTO));
        if (count <= 0) {
            return ResultDTO.failed("更新地址快照信息失败");
        }
        return ResultDTO.succeed();
    }
}
