package com.baturu.zd.service.server;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.PackageForPartnerListDTO;
import com.baturu.zd.dto.web.PackageLogisticsWebExcelDTO;
import com.baturu.zd.request.server.InventoryPackageBaseQueryRequest;
import com.baturu.zd.request.server.PackageBaseQueryRequest;
import com.baturu.zd.request.server.PartnerListBaseQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface PackageQueryService extends BaseQueryService<PackageBaseQueryRequest, PackageDTO> {

    ResultDTO<List<PackageDTO>> queryPackagesByTransportOrderNo(String transportOrderNo, Integer packageState);


    /**
     * 分页查询逐道物流包裹表数据
     *
     * @param packageBaseQueryRequest
     * @return
     */
    ResultDTO<PageDTO> queryPackagesInPage(PackageBaseQueryRequest packageBaseQueryRequest);

    /**
     * 查询逐道物流包裹表数据导出
     *
     * @param packageBaseQueryRequest
     * @return
     */
    List<PackageLogisticsWebExcelDTO> queryPackagesExcel(PackageBaseQueryRequest packageBaseQueryRequest);


    /**
     * 获取待盘点包裹数据
     * @param request
     * @return
     */
    ResultDTO<PageDTO> queryPackageForInventoryInPage(InventoryPackageBaseQueryRequest request);
    /**
     * PC获取待盘点包裹数据
     * @param request
     * @return
     */
    ResultDTO<HashMap<String,Object>>  queryPCPackageForInventoryInPage(InventoryPackageBaseQueryRequest request);

    /**
     * 更新包裹盘点状态
     * @param inventorySate '盘点状态(与盘点信息表对映)：0-未盘点,2-已盘点'
     * @param packageNo
     */
    //todo tms2.0 收发货标准化上线后下架该服务
    ResultDTO<Boolean> updateInventorySateByPackageNo(Integer inventorySate,String packageNo);


    /**
     * 合伙人清单查询结果
     * @param request
     * @return
     */
    ResultDTO<List<PackageForPartnerListDTO>> queryForPartnerList(PartnerListBaseQueryRequest request);

    /**
     * 合伙人清单合伙人id查询结果
     * @param request
     * @return
     */
    ResultDTO<Set<Long>> queryPartnerIds(PartnerListBaseQueryRequest request);
}
