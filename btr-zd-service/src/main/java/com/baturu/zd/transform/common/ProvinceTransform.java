package com.baturu.zd.transform.common;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.ProvinceDTO;
import com.baturu.zd.entity.common.ProvinceEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/02/27
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProvinceTransform extends BaseTransformer {

    public static final SafeFunction<ProvinceEntity, ProvinceDTO>
            ENTITY_TO_DTO = input -> convert(input, new ProvinceDTO());

    public static final SafeFunction<ProvinceDTO, ProvinceEntity>
            DTO_TO_ENTITY = input -> convert(input, new ProvinceEntity());
}
