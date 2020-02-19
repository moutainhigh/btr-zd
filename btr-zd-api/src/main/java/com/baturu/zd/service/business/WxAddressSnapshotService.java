package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.dto.wx.WxAddressSnapshotDTO;
import com.baturu.zd.request.server.WxAddressSnapshotBaseQueryRequest;

import java.util.List;

/**
 * create by pengdi in 2019/3/28
 * 微信地址簿快照表 单表查询服务
 */
public interface WxAddressSnapshotService {
    /**
     * 单表基础查询
     * @param request
     * @return
     */
    ResultDTO<List<WxAddressSnapshotDTO>> queryByParam(WxAddressSnapshotBaseQueryRequest request);

    /**
     * id查询一条快照
     * @param id
     * @return
     */
    ResultDTO<WxAddressSnapshotDTO> queryById(Integer id);

    /**
     * 根据地址簿保存地址簿快照
     * @param wxAddressDTO
     * @return
     */
    ResultDTO<WxAddressSnapshotDTO> save(WxAddressDTO wxAddressDTO);

    /**
     * 保存地址簿快照（参数需齐全，包括createUserId）
     * @param wxAddressSnapshotDTO
     * @return
     */
    ResultDTO<WxAddressSnapshotDTO> save(WxAddressSnapshotDTO wxAddressSnapshotDTO);

    /**
     * 修改地址簿快照、同时更新快照来源地址簿
     * 入口：微信预约单中修改地址簿
     * 废弃
     * @param wxAddressSnapshotDTO
     * @return
     */
    ResultDTO<WxAddressSnapshotDTO> updateInReservation(WxAddressSnapshotDTO wxAddressSnapshotDTO);

    /**
     * 修改地址快照
     */
    ResultDTO update(WxAddressSnapshotDTO wxAddressSnapshotDTO);
}
