package com.baturu.zd.transform.common;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.CountyDTO;
import com.baturu.zd.entity.common.CountyEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/02/27
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountyTransform extends BaseTransformer {

    public static final SafeFunction<CountyEntity, CountyDTO>
            ENTITY_TO_DTO = input -> convert(input, new CountyDTO());

    public static final SafeFunction<CountyDTO, CountyEntity>
            DTO_TO_ENTITY = input -> convert(input, new CountyEntity());
}
