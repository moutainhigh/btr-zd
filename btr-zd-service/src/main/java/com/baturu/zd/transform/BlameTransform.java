package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.BlameDTO;
import com.baturu.zd.dto.ExceptionFollowRecordDTO;
import com.baturu.zd.entity.BlameEntity;
import com.baturu.zd.entity.ExceptionFollowRecordEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 异常跟踪记录transform
 * @author liuduanyang
 * @since 2019/5/31
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlameTransform extends BaseTransformer {

    public static final SafeFunction<BlameEntity, BlameDTO> ENTITY_TO_DTO = input -> convert(input,new BlameDTO());

    public static final SafeFunction<BlameDTO, BlameEntity> DTO_TO_ENTITY = input -> convert(input,new BlameEntity());
}
