package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.dto.api.ApiPackageDTO;
import com.baturu.zd.dto.web.PackageWebExcelDTO;
import com.baturu.zd.request.business.PackageQueryRequest;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.request.server.InventoryPackageBaseQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 对zd_package操作的API
 *
 * @author CaiZhuliang
 * @since 2019-3-27
 */
public interface PackageService {

    /**
     * 根据运单号查询包裹信息
     * @param transportOrderNo 运单号
     * @param packageState 包裹状态
     * @return
     */
    ResultDTO<List<PackageDTO>> queryPackagesByTransportOrderNo(String transportOrderNo, Integer packageState);

    /**
     * 根据包裹号查询包裹信息
     *
     * @param packageNo 包裹号
     * @return
     */
    ResultDTO<PackageDTO> queryPackageByPackageNo(String packageNo);

    /**
     * pc端分页查看包裹信息
     *
     * @param request
     * @return
     */
    ResultDTO<PageDTO> queryPackagesInPage(PackageQueryRequest request);

    /**
     * pc端根据运单号分页查看包裹信息
     *
     * @param request
     * @return
     */
    ResultDTO<PageDTO> queryByTransportOrderNoInPage(PackageQueryRequest request);

    /**
     * 获取PC端运单查询页的包裹汇总
     *
     * @param request
     * @return
     */
    ResultDTO<Integer> queryPackageSumByTransportOrderRequest(TransportOrderQueryRequest request);

    /**
     * 根据ID，更新对应的信息
     *
     * @param packageDTO 对应entity的DTO
     * @return
     */
    ResultDTO<PackageDTO> updatePackageById(PackageDTO packageDTO);

    /**
     * PC端查询包裹列表信息导出excel
     *
     * @param request
     * @return
     */
    List<PackageWebExcelDTO> queryPackagesExcel(PackageQueryRequest request);
    /**
     * 运单作废，相应的包裹同时作废
     *
     * @param transportOrderDTO
     * @param updateUserId
     * @return
     */
    ResultDTO abolishByTransportOrder(TransportOrderDTO transportOrderDTO, Integer updateUserId);

    /**
     * 根据运单号更新state、updateUserId、updateUserName、updateTime
     * @param packageDTO 要更新的信息：state、updateUserId、updateUserName、updateTime
     * @param packageStates 需要被流转的包裹状态。比如：已收货、已装车的包裹要被验收，那么已收货、已装车的状态放在packageStates里。不传则运单号底下的包裹全部更新
     */
    ResultDTO<List<PackageDTO>> updatePackageStateByTransportOrderNo(PackageDTO packageDTO, List<Integer> packageStates);

    /**
     * 根据运单号查询包裹信息
     * @param transportOrderNo 运单号
     * @param packageStates 包裹状态 不传，则查所有状态的包裹
     * @return
     */
    ResultDTO<List<PackageDTO>> queryPackagesByTransportOrderNo(String transportOrderNo, List<Integer> packageStates);

    /**
     * 根据包裹号更新state、updateUserId、updateUserName、updateTime
     * @param packageNos 要更新的包裹号
     * @param packageState 需要流转到的状态
     * @param updateUserId 更新用户ID
     * @param updateTime 更新时间
     */
    ResultDTO<List<PackageDTO>> updatePackageStateByTransportOrderNo(Collection<String> packageNos, Integer packageState, Integer updateUserId, Date updateTime);

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
    ResultDTO<HashMap<String,Object>> queryPCPackageForInventoryInPage(InventoryPackageBaseQueryRequest request);

    /**
     * 更新包裹状态，会判断是否最后一个包裹状态更新，然后去连带更新运单状态。
     * @param apiPackageDTO 需要被更新的信息
     */
    ResultDTO packagesDeal(ApiPackageDTO apiPackageDTO);

    /**
     * 根据包裹号和包裹状态查询包裹
     * @param packageNos 包裹号
     * @param pacakgeStates 包裹状态
     * @return
     */
    ResultDTO<List<PackageDTO>> queryPackagesByPackageNos(Collection<String> packageNos, List<Integer> pacakgeStates);

    /**
     * 查询运单号下的包裹总数
     * @param transportOrderNo
     * @return
     */
    Long queryTotalAmout(String transportOrderNo);
}
