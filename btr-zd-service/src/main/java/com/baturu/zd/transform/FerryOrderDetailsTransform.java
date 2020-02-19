package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.FerryOrderDetailsDTO;
import com.baturu.zd.entity.FerryOrderDetailsEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 摆渡单明细transform
 * @author liuduanyang
 * @since 2019/3/22
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FerryOrderDetailsTransform extends BaseTransformer {

    public static final SafeFunction<FerryOrderDetailsEntity, FerryOrderDetailsDTO> ENTITY_TO_DTO = input -> convert(input,new FerryOrderDetailsDTO());

    public static final SafeFunction<FerryOrderDetailsDTO, FerryOrderDetailsEntity> DTO_TO_ENTITY = input -> convert(input,new FerryOrderDetailsEntity());
}
