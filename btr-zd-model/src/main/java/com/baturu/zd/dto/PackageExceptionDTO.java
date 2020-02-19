package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 包裹异常DTO
 * @author liuduanyang
 * @since 2019/5/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageExceptionDTO implements Serializable {

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 运单号
     */
    private List<OrderExceptionDTO> orderExceptionDTOList;
}
