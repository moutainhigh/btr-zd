package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.PackageImprintDTO;
import com.baturu.zd.entity.PackageImprintEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * create by pengdi in 2019/3/22
 * 包裹轨迹信息transform
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PackageImprintTransform extends BaseTransformer {

    public static final SafeFunction<PackageImprintEntity, PackageImprintDTO> ENTITY_TO_DTO = input -> convert(input,new PackageImprintDTO());

    public static final SafeFunction<PackageImprintDTO,PackageImprintEntity> DTO_TO_ENTITY = input -> convert(input,new PackageImprintEntity());
}
