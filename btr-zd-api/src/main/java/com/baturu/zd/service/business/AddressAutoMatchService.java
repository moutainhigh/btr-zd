package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.ServiceAreaDTO;
import com.baturu.zd.dto.wx.AmapKeyWordResultDTO;

import java.util.List;

/**
 * created by ketao by 2019/05/29
 **/
public interface AddressAutoMatchService {

    /**
     * 根据关键字通过高德api获取相关地域内容列表
     * @param keyWord
     * @return
     */
    ResultDTO<List<AmapKeyWordResultDTO>> addListByKeyWord(String keyWord);

    /**
     * 通过经纬度获取指定地点的城镇，并匹配系统服务区域表获取服务区域
     * @param location 经纬度 格式：纬度,经度
     * @param type  1:寄件地址，2：收件地址
     * @return
     */
    ResultDTO<ServiceAreaDTO> matchAreaByLocation(String location,Integer type);
}
