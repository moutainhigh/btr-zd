package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.TransLineDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.entity.TransLineEntity;
import com.baturu.zd.entity.TransportOrderEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/02/27
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransLineTransform extends BaseTransformer {

    public static final SafeFunction<TransLineEntity, TransLineDTO>
            ENTITY_TO_DTO = input -> convert(input, new TransLineDTO());

    public static final SafeFunction<TransLineDTO, TransLineEntity>
            DTO_TO_ENTITY = input -> convert(input, new TransLineEntity());
}
