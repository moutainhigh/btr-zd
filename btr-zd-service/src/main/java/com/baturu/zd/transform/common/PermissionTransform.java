package com.baturu.zd.transform.common;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.PermissionDTO;
import com.baturu.zd.entity.common.PermissionEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Created by caizhuliang on 2019/3/21.
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermissionTransform extends BaseTransformer {

    public static final SafeFunction<PermissionEntity, PermissionDTO>
            ENTITY_TO_DTO = input -> convert(input, new PermissionDTO());

    public static final SafeFunction<PermissionDTO, PermissionEntity>
            DTO_TO_ENTITY = input -> convert(input, new PermissionEntity());
}
