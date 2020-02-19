package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * created by ketao by 2019/03/22
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserQueryRequest extends  BaseRequest{

    /**服务网点id*/
    private Integer servicePointId;

    /**员工名称*/
    private String name;
}
