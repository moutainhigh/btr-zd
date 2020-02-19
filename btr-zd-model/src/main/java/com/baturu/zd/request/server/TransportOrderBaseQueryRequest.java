package com.baturu.zd.request.server;

import com.baturu.zd.request.business.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * created by ketao by 2019/03/12
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportOrderBaseQueryRequest extends BaseRequest implements Serializable {

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 包裹号
     */
    private String packageNo;
}
