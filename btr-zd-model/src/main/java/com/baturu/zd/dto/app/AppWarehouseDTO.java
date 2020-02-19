package com.baturu.zd.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author CaiZhuliang
 * @since 2019-3-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppWarehouseDTO implements Serializable {

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 仓库名称
     */
    private String warehouseName;

}
