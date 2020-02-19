package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.ExceptionFollowRecordDTO;
import com.baturu.zd.dto.OrderExceptionDTO;
import com.baturu.zd.entity.ExceptionFollowRecordEntity;
import com.baturu.zd.entity.OrderExceptionEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 异常跟踪记录transform
 * @author liuduanyang
 * @since 2019/5/31
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionFollowRecordTransform extends BaseTransformer {

    public static final SafeFunction<ExceptionFollowRecordEntity, ExceptionFollowRecordDTO> ENTITY_TO_DTO = input -> convert(input,new ExceptionFollowRecordDTO());

    public static final SafeFunction<ExceptionFollowRecordDTO, ExceptionFollowRecordEntity> DTO_TO_ENTITY = input -> convert(input,new ExceptionFollowRecordEntity());
}
