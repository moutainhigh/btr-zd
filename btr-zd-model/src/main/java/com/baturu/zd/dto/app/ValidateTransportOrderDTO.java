package com.baturu.zd.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 运单验收接口请求参数
 * @author CaiZhuliang
 * @since 2019-4-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateTransportOrderDTO implements Serializable {

    /** 包裹数 */
    int qty;

    /** 包裹号 */
    String packageNos;

}
