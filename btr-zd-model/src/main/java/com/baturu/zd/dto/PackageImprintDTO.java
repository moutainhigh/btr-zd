package com.baturu.zd.dto;

import com.baturu.zd.dto.common.AbstractBaseDTO;
import lombok.*;

import java.io.Serializable;

/**
 * create by pengdi in 2019/3/22
 * 包裹轨迹信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class PackageImprintDTO extends AbstractBaseDTO implements Serializable {
    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 逐道包裹号
     */
    private String packageNo;

    /**
     * 操作类型.0:预约下单 1:开单 2:收货 3:发货 4:验收（合伙人收货）5：配送 6：收款 7:拒收
     * @see com.baturu.zd.enums.PackageOperateTypeEnum
     */
    private Integer operateType;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作说明
     */
    private String remark;

    /**
     * 当前位置(收单网点/仓库/合伙人)
     */
    private String location;

    /**
     * 当前仓位(当前位置在仓库时有值)
     */
    private String position;

    /**
     * 当前仓位ID(当前位置在仓库时有值)
     */
    private Integer positionId;

    /**
     * 当前位置id
     */
    private Integer locationId;

}
