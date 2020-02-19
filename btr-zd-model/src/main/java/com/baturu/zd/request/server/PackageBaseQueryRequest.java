package com.baturu.zd.request.server;

import com.baturu.zd.request.business.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author LDy
 * @since
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageBaseQueryRequest extends BaseRequest implements Serializable {

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 运单id
     */
    private Integer transportOrderId;

    /**
     * 包裹状态（0:已开单；10：已装车；20：已收货；30：已发货；40：已装运；50：已验收；60：已配送；70：已取消）
     *
     * @see com.baturu.zd.enums.PackageStateEnum
     */
    private Integer state;
    private List<Integer> states;

    /**
     * 服务网点id
     */
    private Integer servicePointId;

    /**
     * 更新开始时间
     */
    private Date updateTimeStart;

    /**
     * 更新结束时间
     */
    private Date updateTimeEnd;

    /**
     * 起始仓库id
     */
    private Integer warehouseId;

    /**
     * 起始仓库编码
     */
    private String warehouseCode;

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 当前位置
     */
    private String location;

    /**
     * 当前仓位
     */
    private String position;

    /**
     * 当前仓位id
     */
    private String positionId;

    /**
     * 收货点id
     */
    private Integer bizId;

    /**
     * 收货点
     */
    private String bizName;

    /**
     * 合伙人名称
     */
    private String partner;

}
