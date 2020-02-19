package com.baturu.zd.service.dto.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ketao
 * @since 2019-3-14
 */
@Data
public class PageDTO<T extends Object> implements Serializable {

    /**
     *总数
     */
    private Long total;

    /**
     * 数据记录
     */
    private List<T> records;

}
