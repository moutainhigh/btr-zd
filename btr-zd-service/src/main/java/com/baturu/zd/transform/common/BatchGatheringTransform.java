package com.baturu.zd.transform.common;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.BatchGatheringDTO;
import com.baturu.zd.entity.common.BatchGatheringEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CaiZhuliang
 * @since 2019-10-17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BatchGatheringTransform extends BaseTransformer {

    public static final SafeFunction<BatchGatheringEntity, BatchGatheringDTO> ENTITY_TO_DTO = input -> convert(input, new BatchGatheringDTO());

    public static final SafeFunction<BatchGatheringDTO, BatchGatheringEntity> DTO_TO_ENTITY = input -> convert(input, new BatchGatheringEntity());

}
