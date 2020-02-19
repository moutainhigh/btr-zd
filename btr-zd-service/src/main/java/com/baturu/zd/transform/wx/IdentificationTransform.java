package com.baturu.zd.transform.wx;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.wx.IdentificationDTO;
import com.baturu.zd.entity.wx.IdentificationEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/02/27
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentificationTransform extends BaseTransformer {

    public static final SafeFunction<IdentificationEntity, IdentificationDTO>
            ENTITY_TO_DTO = input -> convert(input, new IdentificationDTO());

    public static final SafeFunction<IdentificationDTO, IdentificationEntity>
            DTO_TO_ENTITY = input -> convert(input, new IdentificationEntity());
}
