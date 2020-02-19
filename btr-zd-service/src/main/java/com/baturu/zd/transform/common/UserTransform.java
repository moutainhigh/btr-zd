package com.baturu.zd.transform.common;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.common.UserDTO;
import com.baturu.zd.entity.common.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Created by caizhuliang on 2019/3/20.
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTransform extends BaseTransformer {

    public static final SafeFunction<UserEntity, UserDTO>
            ENTITY_TO_DTO = input -> convert(input, new UserDTO());

    public static final SafeFunction<UserDTO, UserEntity>
            DTO_TO_ENTITY = input -> convert(input, new UserEntity());
}
