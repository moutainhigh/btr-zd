package com.baturu.zd.transform.common;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.CityDTO;
import com.baturu.zd.dto.common.EntruckingDTO;
import com.baturu.zd.entity.common.CityEntity;
import com.baturu.zd.entity.common.EntruckingEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * created by laijinjie by 2019/03/21
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Entruckingform extends BaseTransformer {

    public static final SafeFunction<EntruckingEntity, EntruckingDTO>
            ENTITY_TO_DTO = input -> convert(input, new EntruckingDTO());

    public static final SafeFunction<EntruckingDTO, EntruckingEntity>
            DTO_TO_ENTITY = input -> convert(input, new EntruckingEntity());
}
