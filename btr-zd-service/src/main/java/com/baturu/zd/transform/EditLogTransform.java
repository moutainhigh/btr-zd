package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.EditLogDTO;
import com.baturu.zd.entity.EditLogEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 运单异常transform
 * @author liuduanyang
 * @since 2019/5/7
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EditLogTransform extends BaseTransformer {

    public static final SafeFunction<EditLogEntity, EditLogDTO> ENTITY_TO_DTO = input -> convert(input,new EditLogDTO());

    public static final SafeFunction<EditLogDTO, EditLogEntity> DTO_TO_ENTITY = input -> convert(input,new EditLogEntity());
}
