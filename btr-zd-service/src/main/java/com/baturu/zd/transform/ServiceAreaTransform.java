package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.ServiceAreaDTO;
import com.baturu.zd.dto.common.ServicePointDTO;
import com.baturu.zd.entity.common.ServiceAreaEntity;
import com.baturu.zd.entity.common.ServicePointEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * create by pengdi in 2019/3/25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceAreaTransform extends BaseTransformer {

    public static final SafeFunction<ServiceAreaEntity, ServiceAreaDTO> ENTITY_TO_DTO = input -> convert(input,new ServiceAreaDTO());

    public static final SafeFunction<ServiceAreaDTO, ServiceAreaEntity> DTO_TO_ENTITY = input -> convert(input,new ServiceAreaEntity());}
