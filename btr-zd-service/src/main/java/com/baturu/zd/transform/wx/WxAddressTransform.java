package com.baturu.zd.transform.wx;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.entity.wx.WxAddressEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * create by pengdi in 2019/3/6
 * 地址簿transform
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WxAddressTransform extends BaseTransformer {

    public static final SafeFunction<WxAddressEntity, WxAddressDTO> ENTITY_TO_DTO = input -> convert(input,new WxAddressDTO());

    public static final SafeFunction<WxAddressDTO,WxAddressEntity> DTO_TO_ENTITY = input -> convert(input,new WxAddressEntity());
}
