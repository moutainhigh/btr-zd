package com.baturu.zd.transform.common;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.BatchGatheringDetailDTO;
import com.baturu.zd.entity.common.BatchGatheringDetailEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CaiZhuliang
 * @since 2019-10-17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BatchGatheringDetailTransform extends BaseTransformer {

    public static final SafeFunction<BatchGatheringDetailEntity, BatchGatheringDetailDTO> ENTITY_TO_DTO = input -> convert(input, new BatchGatheringDetailDTO());

    public static final SafeFunction<BatchGatheringDetailDTO, BatchGatheringDetailEntity> DTO_TO_ENTITY = input -> convert(input, new BatchGatheringDetailEntity());

}
