package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * created by laijinjie by 2019/03/22
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicePointImportDTO extends AbstractBaseDTO implements Serializable {


    /**
     * 仓库id
     */
    private Integer warehouseId;

    /**
     * 区域id
     */
    private Integer regionId;
    /**
     * 联系人
     */
    private String contact;

    /**
     * 网点地址
     */
    private String address;

    /**
     * 合伙人选择的覆盖范围
     */
    private List<ServiceAreaDTO> areas;

    /**
     * 合伙人团队名称（导入的配送网点）
     */
    private String teamName;




}
