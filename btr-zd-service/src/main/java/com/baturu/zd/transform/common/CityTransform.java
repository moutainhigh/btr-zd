package com.baturu.zd.transform.common;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.CityDTO;
import com.baturu.zd.entity.common.CityEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/02/27
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CityTransform extends BaseTransformer {

    public static final SafeFunction<CityEntity, CityDTO>
            ENTITY_TO_DTO = input -> convert(input, new CityDTO());

    public static final SafeFunction<CityDTO, CityEntity>
            DTO_TO_ENTITY = input -> convert(input, new CityEntity());
}
