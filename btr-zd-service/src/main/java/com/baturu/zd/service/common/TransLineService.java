package com.baturu.zd.service.common;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.logistics.api.dtos.sociTms.SocialDeliverDatasDTO;
import com.baturu.logistics.api.requestParam.SocialTmsQueryRequest;
import com.baturu.logistics.api.service.socialTms.SocialLogisticService;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.TransLineDTO;
import com.baturu.zd.entity.TransLineEntity;
import com.baturu.zd.mapper.TransLineMapper;
import com.baturu.zd.transform.TransLineTransform;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 运单路线获取
 * created by ketao by 2019/03/26
 **/
@Service("transLineService")
@Slf4j
public class TransLineService {

    private String COLUMN_TRANSPORT_ORDER_NO="transport_order_no";

    private String COLUMN_FIRST_WAREHOUSE_CODE="first_warehouse_code";

    @Reference(check = false)
    private SocialLogisticService socialLogisticService;

    @Autowired
    private TransLineMapper transLineMapper;

    /**
     *
     * @param socialTmsQueryRequest
     * @return
     * @see SocialDeliverDatasDTO 返回对象
     */
    public ResultDTO<SocialDeliverDatasDTO> queryTransLine(SocialTmsQueryRequest socialTmsQueryRequest){
//        socialTmsQueryRequest.setTownId(0);
        ResultDTO resultDTO = socialLogisticService.deliverDatas(socialTmsQueryRequest);
        return resultDTO;
    }

    /**
     * 根据运单号查询路线信息/面单信息
     * @param transportOrderNo
     * @return
     */
    public ResultDTO<TransLineDTO> queryByOrderNo(String transportOrderNo){
        QueryWrapper wrapper=new QueryWrapper();
        wrapper.eq(this.COLUMN_TRANSPORT_ORDER_NO,transportOrderNo);
        TransLineEntity transLineEntity = transLineMapper.selectOne(wrapper);
        if(transLineEntity==null){
            log.warn("运单号查询路线信息为空：{}",transportOrderNo);
            return ResultDTO.failed("运单号查询路线信息为空");
        }
        return ResultDTO.succeedWith(TransLineTransform.ENTITY_TO_DTO.apply(transLineEntity));
    }

    /**
     * 根据仓库编码查询路线信息
     * @param warehousecode
     * @return
     */
    public ResultDTO<List<TransLineDTO>> queryByWarehouseCode(String warehousecode){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq(this.COLUMN_FIRST_WAREHOUSE_CODE, warehousecode);
        List<TransLineEntity> transLineEntities = transLineMapper.selectList(wrapper);
        if(transLineEntities == null || transLineEntities.size() <= 0){
            log.info("仓库编码查询路线信息为空：{}", warehousecode);
            return ResultDTO.failed("该仓库暂无运单包裹!");
        }
        return ResultDTO.succeedWith(Lists2.transform(transLineEntities, TransLineTransform.ENTITY_TO_DTO));
    }

    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<TransLineDTO> saveTransLine (TransLineDTO transLineDTO){
        ResultDTO resultDTO = this.checkTransLineSave(transLineDTO);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        TransLineEntity transLineEntity = TransLineTransform.DTO_TO_ENTITY.apply(transLineDTO);
        try {
            int num = transLineMapper.insert(transLineEntity);
            if (num > 0) {
                transLineDTO.setId(transLineEntity.getId());
                return ResultDTO.succeedWith(transLineDTO);
            }
            return ResultDTO.failed("路线信息保存失败");
        } catch (Exception e) {
            log.error("路线信息保存异常",e);
            return ResultDTO.failed("路线信息保存异常");
        }
    }

    private ResultDTO checkTransLineSave(TransLineDTO transLineDTO){
        if (transLineDTO == null) {
            return ResultDTO.failed("路线保存参数为空");
        }
        if(StringUtils.isBlank(transLineDTO.getTransportOrderNo())){
            return ResultDTO.failed("运单号为空");
        }
        if (StringUtils.isBlank(transLineDTO.getFirstWarehouse())) {
            return ResultDTO.failed("发货仓库为空");
        }
        if (StringUtils.isBlank(transLineDTO.getFirstPositionCode())) {
            return ResultDTO.failed("发货仓位编码为空");
        }
        if (StringUtils.isBlank(transLineDTO.getPartnerName())) {
            return ResultDTO.failed("合伙人名称为空");
        }
        if (StringUtils.isBlank(transLineDTO.getBizName())) {
            return ResultDTO.failed("收货点名称为空");
        }
        if (transLineDTO.getBizId() == null) {
            return ResultDTO.failed("收货点id为空");
        }
        if (transLineDTO.getPartnerId() == null){
            return ResultDTO.failed("合伙人id为空");
        }
        return ResultDTO.succeed();
    }

}
