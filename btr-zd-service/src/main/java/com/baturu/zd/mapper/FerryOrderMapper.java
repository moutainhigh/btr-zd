package com.baturu.zd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baturu.zd.entity.FerryOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 摆渡单mapper
 * @author liuduanyang
 * @since 2019/3/22
 */
@Mapper
public interface FerryOrderMapper extends BaseMapper<FerryOrderEntity> {

    /**
     * 查询zd_ferry_order下个自增长id是多少
     * @return
     */
    @Select("SELECT AUTO_INCREMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'zd_ferry_order'")
    Integer getNextId();

    /**
     * 查询当天最大的运单号
     * @return
     */
    @Select("select ferry_no from zd_ferry_order where id = (select MAX(id) from zd_ferry_order where TO_DAYS(create_time) = TO_DAYS(NOW()))")
    String getCurrentMaxFerryOrderNo();

    /**
     * 查询一共有多少摆渡单
     * @return
     */
    @Select("select COUNT(id) from zd_ferry_order")
    int count();
}
