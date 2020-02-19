package com.baturu.zd.mapper.wx;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baturu.zd.entity.wx.WxAddressEntity;
import com.baturu.zd.entity.wx.WxAddressSnapshotEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * create by pengdi in 2019/3/28
 * 地址簿快照表mapper
 */
@Mapper
public interface WxAddressSnapshotMapper extends BaseMapper<WxAddressSnapshotEntity> {
}
