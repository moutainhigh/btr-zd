package com.baturu.zd.transform.common;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.TownDTO;
import com.baturu.zd.entity.common.TownEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * create by pengdi in 2019/3/29
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TownTransform extends BaseTransformer {

    public static final SafeFunction<TownEntity, TownDTO> ENTITY_TO_DTO = input -> convert(input,new TownDTO());

    public static final SafeFunction<TownDTO, TownEntity> DTO_TO_ENTITY = input -> convert(input,new TownEntity());
}
