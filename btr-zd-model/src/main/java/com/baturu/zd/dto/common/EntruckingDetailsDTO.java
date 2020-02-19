package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * created by laijinjie by 2019/03/21
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntruckingDetailsDTO implements Serializable {

    /**
     * id
     */
    private Integer id;
    /**
     * 装车单号
     */
    private String entruckingNo;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 运单号
     */
    private String transportOrderNo;
    /**
     * 包裹号
     */
    private String packageNo;
    /**
     * 收货人
     */
    private String receivingGuy;
    /**
     * 收货时间
     */
    private Date receivingTime;
    /**
     * 是否有效
     */
    private Boolean active;

    /**
     * 创建人名称（定义为装车人名称）
     */
    private String createUserName;

    /**
     * 创建时间（定义为装车时间）
     */
    private Date createTime;

    /**
     * 收货仓库（目的地） 关联装车表
     */
    private String receivingWarehouse;

    /**
     * 状态1已发车2已收货
     */
    private Integer state;
}
