package com.baturu.zd.transform;

import com.baturu.kit.converter.BaseTransformer;
import com.baturu.kit.kit.function.SafeFunction;
import com.baturu.zd.dto.web.FerryFreightDTO;
import com.baturu.zd.dto.web.excel.FerryFreightExcelDTO;
import com.baturu.zd.entity.FerryFreightEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * create by pengdi in 2019/3/14
 * 摆渡运费transform
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FerryFreightTransform extends BaseTransformer {

    public static final SafeFunction<FerryFreightEntity, FerryFreightDTO> ENTITY_TO_DTO = input -> convert(input,new FerryFreightDTO());

    public static final SafeFunction<FerryFreightDTO, FerryFreightEntity> DTO_TO_ENTITY = input -> convert(input,new FerryFreightEntity());

    public static final SafeFunction<FerryFreightDTO, FerryFreightExcelDTO> DTO_TO_EXCEL = input -> convert(input,new FerryFreightExcelDTO());
}
