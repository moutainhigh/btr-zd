package com.baturu.zd.transform.common;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.RoleDTO;
import com.baturu.zd.entity.common.RoleEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Created by caizhuliang on 2019/3/20.
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleTransform extends BaseTransformer {

    public static final SafeFunction<RoleEntity, RoleDTO>
            ENTITY_TO_DTO = input -> convert(input, new RoleDTO());

    public static final SafeFunction<RoleDTO, RoleEntity>
            DTO_TO_ENTITY = input -> convert(input, new RoleEntity());
}
