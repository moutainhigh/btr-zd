package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author liuduanyang
 * @since 2019/3/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppFerryOrderRequest implements Serializable {

    private Integer qty;

    private BigDecimal bulk;

    private BigDecimal amount;

    private String packages;

}
