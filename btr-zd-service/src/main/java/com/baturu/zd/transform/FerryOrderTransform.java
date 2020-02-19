package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.FerryOrderDTO;
import com.baturu.zd.entity.FerryOrderEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 摆渡单transform
 * @author liuduanyang
 * @since 2019/3/22
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FerryOrderTransform extends BaseTransformer {

    public static final SafeFunction<FerryOrderEntity, FerryOrderDTO> ENTITY_TO_DTO = input -> convert(input,new FerryOrderDTO());

    public static final SafeFunction<FerryOrderDTO, FerryOrderEntity> DTO_TO_ENTITY = input -> convert(input,new FerryOrderEntity());
}
