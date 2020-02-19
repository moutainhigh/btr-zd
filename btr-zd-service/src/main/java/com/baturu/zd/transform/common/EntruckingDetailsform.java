package com.baturu.zd.transform.common;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.EntruckingDetailsDTO;
import com.baturu.zd.entity.common.EntruckingDetailsEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * created by laijinjie by 2019/03/21
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntruckingDetailsform extends BaseTransformer {

    public static final SafeFunction<EntruckingDetailsEntity, EntruckingDetailsDTO>
            ENTITY_TO_DTO = input -> convert(input, new EntruckingDetailsDTO());

    public static final SafeFunction<EntruckingDetailsDTO, EntruckingDetailsEntity>
            DTO_TO_ENTITY = input -> convert(input, new EntruckingDetailsEntity());
}
