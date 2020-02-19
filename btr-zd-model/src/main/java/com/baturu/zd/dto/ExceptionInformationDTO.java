package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 异常信息DTO
 * @author liuduanyang
 * @since 2019/5/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionInformationDTO {

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 运单状态
     */
    private Integer state;

    /**
     * 包裹异常list
     */
    private List<PackageExceptionDTO> packageExceptionDTOList;
}
