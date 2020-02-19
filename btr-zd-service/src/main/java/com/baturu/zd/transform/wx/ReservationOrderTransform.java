package com.baturu.zd.transform.wx;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.wx.ReservationOrderDTO;
import com.baturu.zd.entity.wx.ReservationOrderEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/02/27
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationOrderTransform extends BaseTransformer {

    public static final SafeFunction<ReservationOrderEntity, ReservationOrderDTO>
            ENTITY_TO_DTO = input -> convert(input, new ReservationOrderDTO());

    public static final SafeFunction<ReservationOrderDTO, ReservationOrderEntity>
            DTO_TO_ENTITY = input -> convert(input, new ReservationOrderEntity());
}
