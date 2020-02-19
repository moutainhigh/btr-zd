package com.baturu.zd.service.server;

import com.baturu.parts.dtos.ResultDTO;

import java.util.List;

/**
 * created by ketao by 2019/03/15
 **/
public interface BaseQueryService<Q, R> {

    /**
     * 单表基础查询
     * @param queryParam
     * @return
     */
    ResultDTO<List<R>> queryByParam(Q queryParam);


    /**
     * 根据id查询
     * @param id
     * @return
     */
    ResultDTO<R> queryById(Integer id);


}
