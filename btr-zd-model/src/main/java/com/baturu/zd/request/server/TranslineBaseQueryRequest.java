package com.baturu.zd.request.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * create by pengdi in 2019/4/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TranslineBaseQueryRequest implements Serializable {
    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 是否有效
     */
    private Boolean active;
}
