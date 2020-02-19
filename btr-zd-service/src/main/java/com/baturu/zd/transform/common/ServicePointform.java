package com.baturu.zd.transform.common;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.ServicePointDTO;
import com.baturu.zd.entity.common.ServicePointEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * created by laijinjie by 2019/03/22
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServicePointform extends BaseTransformer {

    public static final SafeFunction<ServicePointEntity, ServicePointDTO>
            ENTITY_TO_DTO = input -> convert(input, new ServicePointDTO());

    public static final SafeFunction<ServicePointDTO, ServicePointEntity>
            DTO_TO_ENTITY = input -> convert(input, new ServicePointEntity());
}
