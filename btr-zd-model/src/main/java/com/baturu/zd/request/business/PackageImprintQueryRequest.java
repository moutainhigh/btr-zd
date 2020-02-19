package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create by pengdi in 2019/3/22
 * 包裹轨迹信息  biz服务request
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackageImprintQueryRequest extends BaseRequest {
    /**
     * id
     */
    private Integer id;

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 逐道包裹号
     */
    private String packageNo;

}
