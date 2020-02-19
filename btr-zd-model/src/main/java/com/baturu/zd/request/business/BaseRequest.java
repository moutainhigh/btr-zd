package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * created by ketao by 2019/03/15
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseRequest implements Serializable {

    /**
     * 是否有效（1：是；0：否）
     */
    private Boolean active;

    /**
     * 创建人id
     */
    private Integer createUserId;
    private Set<Integer> createUserIds;


    /**
     * 数据来源 1：微信公众号；2：后台；3：手机app
     */
    private Integer source;


    private Integer id;

    /**
     * 当前页
     */
    private Integer current;

    /**
     * 页行数
     */
    private Integer size;

}
