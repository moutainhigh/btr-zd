package com.baturu.zd.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 装车单明细
 * @author CaiZhuliang
 * @since 2019-3-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppEntruckingDetailsDTO implements Serializable {

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 包裹号
     */
    private String packageNo;

}
