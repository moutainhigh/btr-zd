package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.OrderExceptionDTO;
import com.baturu.zd.entity.OrderExceptionEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 运单异常transform
 * @author liuduanyang
 * @since 2019/5/7
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderExceptionTransform extends BaseTransformer {

    public static final SafeFunction<OrderExceptionEntity, OrderExceptionDTO> ENTITY_TO_DTO = input -> convert(input,new OrderExceptionDTO());

    public static final SafeFunction<OrderExceptionDTO, OrderExceptionEntity> DTO_TO_ENTITY = input -> convert(input,new OrderExceptionEntity());
}
