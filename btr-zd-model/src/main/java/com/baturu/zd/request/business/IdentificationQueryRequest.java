package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * created by ketao by 2019/03/14
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdentificationQueryRequest extends BaseRequest implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 实名认证手机号
     */
    private String phone;

    /**
     * 省份id
     */
    private Integer provinceId;

    /**
     * 城市id
     */
    private Integer cityId;

    /**
     * 区/街道id
     */
    private Integer countyId;

    /**
     *实名认证名称
     */
    private String name;

    /**
     * 创建人id
     */
    protected Integer createUserId;

    /**
     * 注册时间  开始
     */
    private Date startTime;

    /**
     * 注册时间  结束
     */
    private Date endTime;


}
