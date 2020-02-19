package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.entity.TransportOrderEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/02/27
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransportOrderTransform extends BaseTransformer {

    public static final SafeFunction<TransportOrderEntity, TransportOrderDTO>
            ENTITY_TO_DTO = input -> convert(input, new TransportOrderDTO());

    public static final SafeFunction<TransportOrderDTO, TransportOrderEntity>
            DTO_TO_ENTITY = input -> convert(input, new TransportOrderEntity());
}
