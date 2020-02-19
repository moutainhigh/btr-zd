package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.OrderImprintDTO;
import com.baturu.zd.entity.OrderImprintEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * create by pengdi in 2019/3/21
 * 运单轨迹信息transform
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderImprintTransform extends BaseTransformer {

    public final static SafeFunction<OrderImprintEntity, OrderImprintDTO> ENTITY_TO_DTO = input -> convert(input,new OrderImprintDTO());

    public final static SafeFunction<OrderImprintDTO, OrderImprintEntity> DTO_TO_ENTITY = input -> convert(input,new OrderImprintEntity());
}
