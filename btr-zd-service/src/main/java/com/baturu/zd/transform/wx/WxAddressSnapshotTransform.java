package com.baturu.zd.transform.wx;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.dto.wx.WxAddressSnapshotDTO;
import com.baturu.zd.entity.wx.WxAddressEntity;
import com.baturu.zd.entity.wx.WxAddressSnapshotEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * create by pengdi in 2019/3/28
 * 地址簿快照 transform
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WxAddressSnapshotTransform extends BaseTransformer {

    public static final SafeFunction<WxAddressSnapshotEntity, WxAddressSnapshotDTO> ENTITY_TO_DTO = input -> convert(input,new WxAddressSnapshotDTO());

    public static final SafeFunction<WxAddressSnapshotDTO,WxAddressSnapshotEntity> DTO_TO_ENTITY = input -> convert(input,new WxAddressSnapshotEntity());
}
