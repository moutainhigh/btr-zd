package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.entity.PackageEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/02/27
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PackageTransform extends BaseTransformer {

    public static final SafeFunction<PackageEntity, PackageDTO>
            ENTITY_TO_DTO = input -> convert(input, new PackageDTO());

    public static final SafeFunction<PackageDTO, PackageEntity>
            DTO_TO_ENTITY = input -> convert(input, new PackageEntity());
}
