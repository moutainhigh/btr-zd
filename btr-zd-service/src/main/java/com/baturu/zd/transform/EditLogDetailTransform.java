package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.EditLogDetailDTO;
import com.baturu.zd.entity.EditLogDetailEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 运单异常transform
 * @author liuduanyang
 * @since 2019/5/7
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EditLogDetailTransform extends BaseTransformer {

    public static final SafeFunction<EditLogDetailEntity, EditLogDetailDTO> ENTITY_TO_DTO = input -> convert(input,new EditLogDetailDTO());

    public static final SafeFunction<EditLogDetailDTO, EditLogDetailEntity> DTO_TO_ENTITY = input -> convert(input,new EditLogDetailEntity());
}
