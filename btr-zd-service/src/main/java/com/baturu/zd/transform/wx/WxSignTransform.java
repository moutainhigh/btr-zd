package com.baturu.zd.transform.wx;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.wx.WxSignDTO;
import com.baturu.zd.entity.wx.WxSignEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/02/27
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WxSignTransform extends BaseTransformer {

    public static final SafeFunction<WxSignEntity, WxSignDTO>
            ENTITY_TO_DTO = input -> convert(input, new WxSignDTO());

    public static final SafeFunction<WxSignDTO, WxSignEntity>
            DTO_TO_ENTITY = input -> convert(input, new WxSignEntity());
}
