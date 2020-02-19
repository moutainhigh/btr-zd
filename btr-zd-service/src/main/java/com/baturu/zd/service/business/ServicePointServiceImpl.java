package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.common.guava2.Lists2;
import com.baturu.crm.api.dto.team.PrincipalUser;
import com.baturu.crm.api.dto.team.Team;
import com.baturu.crm.api.param.team.PrincipalUserQuery;
import com.baturu.crm.api.param.team.TeamQuery;
import com.baturu.crm.api.service.PrincipalUserRpcService;
import com.baturu.crm.api.service.TeamRpcService;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.tms.api.dto.outside.PartnerServiceAreaDTO;
import com.baturu.tms.api.dto.sorting.warehouse.WarehouseDTO;
import com.baturu.tms.api.request.sorting.warehouse.WarehouseQueryRequest;
import com.baturu.tms.api.service.business.outer.PartnerServiceAreaService;
import com.baturu.tms.api.service.common.sorting.warehouse.WarehouseService;
import com.baturu.zd.config.datasource.DataSourceType;
import com.baturu.zd.config.datasource.Datasource;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.ServiceAreaConstant;
import com.baturu.zd.constant.ServicePointConstant;
import com.baturu.zd.dto.common.*;
import com.baturu.zd.entity.common.ServiceAreaEntity;
import com.baturu.zd.entity.common.ServicePointEntity;
import com.baturu.zd.enums.RegionEnum;
import com.baturu.zd.enums.ServicePointTypeEnum;
import com.baturu.zd.mapper.common.ServiceAreaMapper;
import com.baturu.zd.mapper.common.ServicePointMapper;
import com.baturu.zd.request.business.ServicePointQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.ServiceAreaTransform;
import com.baturu.zd.transform.ServicePointTransform;
import com.baturu.zd.util.ZDStringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.stream.Collectors;

/**
 * create by laijinjie in 2019/3/22
 * 业务网点实现类 对前端
 */
@Service("servicePointService")
@Slf4j
public class ServicePointServiceImpl extends AbstractServiceImpl implements ServicePointService {

    @Autowired
    private ServicePointMapper servicePointMapper;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;
    @Reference(check = false)
    private WarehouseService warehouseService;
    @Reference(check = false)
    private TeamRpcService teamRpcService;
    @Reference(check = false)
    private PrincipalUserRpcService principalUserRpcService;
    @Reference(check = false)
    private PartnerServiceAreaService partnerServiceAreaService;

    /**
     * 查询地址类型
     */
    private final Integer PROVINCE = 1;
    private final Integer CITY = 2;
    private final Integer COUNTY = 3;
    private final Integer TOWN = 4;

    /**
     * 查询所有网点信息
     *
     * @param request
     * @return
     */
    @Override
    public ResultDTO<List<Map<String, Object>>> queryAllServicePoint(ServicePointQueryRequest request) {
        QueryWrapper queryWrapper = this.buildWrapper(request);
        List<ServicePointEntity> list = servicePointMapper.selectList(queryWrapper);
        List<Map<String, Object>> objects = new ArrayList<>();
        
        list.forEach(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put(ServicePointConstant.ID, s.getId());
            map.put(ServicePointConstant.NAME, s.getName());
            objects.add(map);
        });
        return ResultDTO.succeedWith(objects);
    }

    @Override
    public ResultDTO<PageDTO> queryServicePointsInPage(ServicePointQueryRequest request) {
        Page page = getPage(request.getCurrent(), request.getSize());
        List<ServicePointDTO> servicePointDTOS = servicePointMapper.queryServicePointsInPage(page, request);
        this.fillWarehouseName(servicePointDTOS);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setTotal(page.getTotal());
        pageDTO.setRecords(servicePointDTOS);
        return ResultDTO.succeedWith(pageDTO);
    }

    public void fillWarehouseName(List<ServicePointDTO> servicePointDTOS) {
        if (servicePointDTOS.size() > 0) {
            try {
                Set<Integer> warehouseIds = servicePointDTOS.stream().map(ServicePointDTO::getWarehouseId).collect(Collectors.toSet());
                ResultDTO<List<WarehouseDTO>> resultDTO = warehouseService.queryByParam(WarehouseQueryRequest.builder().ids(warehouseIds).build());
                if (resultDTO.isSuccess() && resultDTO.getModel().size() > 0) {
                    List<WarehouseDTO> list = resultDTO.getModel();
                    Map<Integer, String> map = list.stream().collect(Collectors.toMap(WarehouseDTO::getId, WarehouseDTO::getName, (w1, w2) -> w1));
                    servicePointDTOS.stream().forEach(s -> s.setWarehouseName(map.get(s.getWarehouseId())));
                }
            } catch (Exception e) {
                log.warn("仓库名称填充异常", e);
            }
        }
    }

    /**
     * 查询当前用户所属网点信息
     *
     * @param servicePointId 网点ID
     * @return
     */
    @Override
    public ResultDTO<ServicePointDTO> queryServicePointById(Integer servicePointId) {
        if (null == servicePointId) {
            return ResultDTO.failed("请输入网点ID");
        }
        ServicePointEntity entity = servicePointMapper.selectById(servicePointId);
        if (null == entity) {
            log.info("ServicePointServiceImpl queryServicePointById 查询不到记录. servicePointId = {}", servicePointId);
            return ResultDTO.failed("查询不到网点记录");
        }
        ServicePointDTO servicePointDTO = ServicePointTransform.ENTITY_TO_DTO.apply(entity);
        List<ServicePointDTO> servicePointDTOS = Lists.newArrayList(servicePointDTO);
        this.fillWarehouseName(servicePointDTOS);
        this.fillServiceArea(servicePointDTO);
        return ResultDTO.successfy(servicePointDTO);
    }

    /**
     * 填充网点服务范围
     *
     * @param servicePointDTO
     */
    private void fillServiceArea(ServicePointDTO servicePointDTO) {
        QueryWrapper<ServiceAreaEntity> wrapper = new QueryWrapper();
        wrapper.eq(ServiceAreaConstant.SERVICE_POINT_ID, servicePointDTO.getId()).eq(ServiceAreaConstant.ACTIVE, Boolean.TRUE);
        List<ServiceAreaEntity> serviceAreaEntities = serviceAreaMapper.selectList(wrapper);
        if (serviceAreaEntities.size() > 0) {
            List<ServiceAreaDTO> list = Lists2.transform(serviceAreaEntities, ServiceAreaTransform.ENTITY_TO_DTO);
            Optional<ServiceAreaDTO> any = list.stream().filter(s -> s.getIsDefault() != null && s.getIsDefault() == 1).findAny();
            if (any.isPresent()) {
                servicePointDTO.setDefaultServiceArea(any.get());
            }
            servicePointDTO.setServiceAreas(list);
        }
    }

    @Override
    public ResultDTO<List<ServiceAreaDTO>> queryOtherServiceAreaWithoutServicePointId(Integer servicePointId, Integer type) {
        log.info("queryOtherServiceAreaWithoutServicePointId servicePointId = {}, type = {}", servicePointId, type);
        List<ServiceAreaDTO> serviceAreaDTOS = serviceAreaMapper.queryOtherServiceAreaWithoutServicePointId(servicePointId, type);
        return ResultDTO.succeedWith(serviceAreaDTOS);
    }


    @Transactional(rollbackFor = Throwable.class)
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    @Override
    public ResultDTO<ServicePointDTO> saveServicePoint(ServicePointDTO servicePointDTO) {
        ResultDTO resultDTO = this.checkServicePointSave(servicePointDTO);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        try {
            int num;
            ServicePointEntity servicePointEntity = ServicePointTransform.DTO_TO_ENTITY.apply(servicePointDTO);
            if (servicePointDTO.getId() == null) {
                Integer lastId = servicePointMapper.queryLatestServicePointId();
                String pointNum = ZDStringUtil.getNextFullZero(lastId == null ? 0 : lastId, 3);
                servicePointEntity.setNum(pointNum);
                num = servicePointMapper.insert(servicePointEntity);
                if (num > 0) {
                    servicePointDTO.setId(servicePointEntity.getId());
                }
            } else {
                num = servicePointMapper.updateById(servicePointEntity);
            }
            if (num == 0) {
                return ResultDTO.failed("网点保存失败");
            }
            this.batchSaveServiceAreas(servicePointDTO);
            //设置网点默认地址
            if (servicePointDTO.getDefaultServiceArea() != null) {
                serviceAreaMapper.resetServieArea(servicePointDTO.getId());
                int areaNum = serviceAreaMapper.setDefaultServiceArea(servicePointDTO.getId(), servicePointDTO.getDefaultServiceArea());
                if (areaNum == 0) {
                    log.info("默认地址设置失败：{}，{}", servicePointDTO.getId(), servicePointDTO.getDefaultServiceArea());
                    return ResultDTO.failed("默认地址设置失败");
                }
            }
            return ResultDTO.succeedWith(servicePointDTO);
        }
        catch (DuplicateKeyException d) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultDTO.failed(d.getMessage());
        }
        catch (Exception e) {
            log.error("ServicePointServiceImpl#saveServicePoint网点保存异常", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultDTO.failed("网点保存异常");
        }
    }

    @Override
    public ResultDTO<List<ProvinceDTO>> queryAllServiceArea(Integer type) {
        List<ServiceAreaDTO> serviceAreaDTOS = serviceAreaMapper.queryServiceAreaByType(type);
        //组装地址DTO
        List<ProvinceDTO> provinceDTOS = this.buildAddressAsChlidren(serviceAreaDTOS);
        return ResultDTO.successfy(provinceDTOS);
    }

    @Override
    public ResultDTO<List<LevelAddressVO>> queryChildrenArea(Integer servicePointType, Integer addressType, Integer parentId) {
        if (servicePointType == null) {
            return ResultDTO.failed("网点服务范围查询服务::网点类型不能为空");
        }
        if (addressType == null) {
            return ResultDTO.failed("网点服务范围查询服务::地址类型不能为空");
        }
        List<LevelAddressVO> levelAddressVOS = Lists.newArrayList();
        if (addressType.equals(PROVINCE)) {
            levelAddressVOS = serviceAreaMapper.queryProvinceServiceArea(servicePointType);
        } else if (addressType.equals(CITY)) {
            levelAddressVOS = serviceAreaMapper.queryCityServiceArea(servicePointType, parentId);
        } else if (addressType.equals(COUNTY)) {
            levelAddressVOS = serviceAreaMapper.queryCountyServiceArea(servicePointType, parentId);
        } else if (addressType.equals(TOWN)) {
            levelAddressVOS = serviceAreaMapper.queryTownServiceArea(servicePointType, parentId);
        } else {
            return ResultDTO.failed("type无效");
        }
        return ResultDTO.successfy(levelAddressVOS);
    }


    /**
     * 保存网点服务范围
     *
     * @param servicePointDTO
     */
    private void batchSaveServiceAreas(ServicePointDTO servicePointDTO) {
        List<ServiceAreaDTO> serviceAreas = servicePointDTO.getServiceAreas();
        if (serviceAreas != null && serviceAreas.size() > 0) {
            List<ServiceAreaEntity> areaEntities = Lists2.transform(serviceAreas, ServiceAreaTransform.DTO_TO_ENTITY);
            areaEntities.stream().forEach(s -> {
                s.setCreateUserId(servicePointDTO.getCreateUserId() == null ? servicePointDTO.getUpdateUserId() : servicePointDTO.getCreateUserId());
                s.setServicePointId(servicePointDTO.getId());
            });
            //查询该网点原服务范围id
            Set<Integer> areaIds = serviceAreaMapper.queryServiceAreaIdsByPointId(servicePointDTO.getId());
            try {
                //网点新增
                if (CollectionUtils.isEmpty(areaIds) && CollectionUtils.isNotEmpty(areaEntities)) {
                    serviceAreaMapper.batchSaveServiceAreas(areaEntities);
                }//用户更新
                else if (CollectionUtils.isNotEmpty(areaEntities)) {
                    serviceAreaMapper.deleteServiceAresByServicePoinId(servicePointDTO.getId());
                    serviceAreaMapper.batchSaveServiceAreas(areaEntities);
                }
            } catch (DuplicateKeyException d) {
                log.info("网点覆盖区域重复:",d);
                throw new DuplicateKeyException("网点覆盖区域存在已被选择的区域，请重新选择");
            }
        }
    }

    private ResultDTO checkServicePointSave(ServicePointDTO servicePointDTO) {
        if (servicePointDTO == null) {
            return ResultDTO.failed("服务网点保存参数为空");
        }
        if (servicePointDTO.getId() == null) {
            if (servicePointDTO.getType() == null) {
                return ResultDTO.failed("网点类型为空");
            }
            if (StringUtils.isBlank(servicePointDTO.getName())) {
                return ResultDTO.failed("网点名称为空");
            }
            if (servicePointDTO.getWarehouseId() == null) {
                return ResultDTO.failed("所属分拣中心id为空");
            } else {
                ResultDTO<List<WarehouseDTO>> warehouseResult =
                        warehouseService.queryByParam(WarehouseQueryRequest.builder().ids(Sets.newLinkedHashSet(servicePointDTO.getWarehouseId())).build());
                if (warehouseResult.isSuccess() && warehouseResult.getModel().size() > 0) {
                    servicePointDTO.setWarehouseCode(warehouseResult.getModel().get(0).getCode());
                } else {
                    log.warn("网点保存#无效仓库id：{}", servicePointDTO.getWarehouseId());
                    return ResultDTO.failed("无效仓库id");
                }
            }
            if (servicePointDTO.getRegionId() == null) {
                return ResultDTO.failed("网点区域为空");
            }
            if (StringUtils.isBlank(servicePointDTO.getContact())) {
                return ResultDTO.failed("网点联系人为空");
            }
            if (StringUtils.isBlank(servicePointDTO.getContactTel())) {
                return ResultDTO.failed("网点联系人联系电话为空");
            }
            if (StringUtils.isBlank(servicePointDTO.getAddress())) {
                return ResultDTO.failed("网点地址为空");
            }

        }
        if (servicePointDTO.getDefaultServiceArea() != null) {
            ServiceAreaDTO defaultServiceArea = servicePointDTO.getDefaultServiceArea();
            if (defaultServiceArea.getProvinceId() == null || defaultServiceArea.getCityId() == null || defaultServiceArea.getCountyId() == null || defaultServiceArea.getTownId()
                    == null) {
                return ResultDTO.failed("默认地址四级地址不能为空");
            }
        }
        return ResultDTO.succeed();
    }

    private QueryWrapper buildWrapper(ServicePointQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if (CollectionUtils.isNotEmpty(request.getIds())){
            wrapper.in(ServicePointConstant.ID,request.getIds());
        }
        if (request.getActive() != null) {
            wrapper.eq(ServicePointConstant.ACTIVE, request.getActive());
        } else {
            wrapper.eq(ServicePointConstant.ACTIVE, Boolean.TRUE);
        }
        if (request.getType() != null) {
            wrapper.eq(ServicePointConstant.TYPE, request.getType());
        }
        return wrapper;
    }

    /**
     * 数据组装格式
     * 省：{
     * 市：{
     * 区：{
     * 镇：{
     * <p>
     * }
     * }
     * }
     * }
     *
     * @param serviceAreaDTOS
     * @return
     */
    private List<ProvinceDTO> buildAddressAsChlidren(List<ServiceAreaDTO> serviceAreaDTOS) {
        //组装provinceDTO
        Map<Integer, ProvinceDTO> provinceId2DTOMap = serviceAreaDTOS.stream()
                .filter(o -> o.getProvinceId() != null)
                .collect(Collectors.toMap(ServiceAreaDTO::getProvinceId, o -> ProvinceDTO.builder()
                                .id(o.getProvinceId())
                                .name(o.getProvinceName())
                                .build()
                        , (o1, o2) -> o1));
        //组装cityDTO
        Map<Integer, CityDTO> cityId2DTOMap = serviceAreaDTOS.stream()
                .filter(o -> o.getCityId() != null)
                .collect(Collectors.toMap(ServiceAreaDTO::getCityId, o -> CityDTO.builder()
                                .id(o.getCityId())
                                .name(o.getCityName())
                                .provinceId(o.getProvinceId())
                                .build()
                        , (o1, o2) -> o1));
        //组装countyDTO
        Map<Integer, CountyDTO> countyId2DTOMap = serviceAreaDTOS.stream()
                .filter(o -> o.getCountyId() != null)
                .collect(Collectors.toMap(ServiceAreaDTO::getCountyId, o -> CountyDTO.builder()
                                .id(o.getCountyId())
                                .name(o.getCountyName())
                                .cityId(o.getCityId())
                                .build(),
                        (o1, o2) -> o1));
        //组装townDTO
        Map<Integer, TownDTO> townId2DTOMap = serviceAreaDTOS.stream()
                .filter(o -> o.getTownId() != null)
                .collect(Collectors.toMap(ServiceAreaDTO::getTownId, o -> TownDTO.builder()
                                .id(o.getTownId())
                                .name(o.getTownName())
                                .countyId(o.getCountyId())
                                .build(),
                        (o1, o2) -> o1));

        //DTO集合 hashMap#values => Collection
        List<ProvinceDTO> provinceDTOS = Lists.newArrayList(provinceId2DTOMap.values());
        List<CityDTO> cityDTOS = Lists.newArrayList(cityId2DTOMap.values());
        List<CountyDTO> countyDTOS = Lists.newArrayList(countyId2DTOMap.values());
        List<TownDTO> townDTOS = Lists.newArrayList(townId2DTOMap.values());
        //组装父->子map
        Map<Integer, List<CityDTO>> provinceId2CityDTOMap = cityDTOS.stream().filter(c -> c.getProvinceId() != null).collect(Collectors.groupingBy(CityDTO::getProvinceId));
        Map<Integer, List<CountyDTO>> cityId2CountyDTOMap = countyDTOS.stream().filter(c -> c.getCityId() != null).collect(Collectors.groupingBy(CountyDTO::getCityId));
        Map<Integer, List<TownDTO>> countyId2TownDTOMap = townDTOS.stream().filter(c -> c.getCountyId() != null).collect(Collectors.groupingBy(TownDTO::getCountyId));

        //将区下的镇装进countyDTO
        countyDTOS.stream().forEach(o -> {
            o.setChildren(countyId2TownDTOMap.getOrDefault(o.getId(), Lists.newArrayList()));
        });

        //将市下的县装进cityDTO
        cityDTOS.stream().forEach(o -> {
            o.setChildren(cityId2CountyDTOMap.getOrDefault(o.getId(), Lists.newArrayList()));
        });
        //将省下的市装进provinceDTO
        provinceDTOS.stream().forEach(o -> {
            o.setChildren(provinceId2CityDTOMap.getOrDefault(o.getId(), Lists.newArrayList()));
        });

        return Lists.newArrayList(provinceDTOS);
    }

    @Override
    public ResultDTO<Boolean> existByTeamName(String teamName) {
        if (StringUtils.isBlank(teamName)) {
            return ResultDTO.failed("合伙人团队名称为空");
        }
        // 获取合伙人团队
        ResultDTO<Team> teamResult = this.getTeamByTeamName(teamName);
        if (teamResult.isUnSuccess()) {
            return ResultDTO.failed(teamResult.getErrorMsg());
        }
        return ResultDTO.succeedWith(Boolean.TRUE);
    }

    @Override
    public ResultDTO<List<ProvinceDTO>> queryPartnerAreaByTeamName(String teamName) {
        if (StringUtils.isBlank(teamName)) {
            return ResultDTO.failed("合伙人团队名称为空");
        }
        // 获取合伙人团队
        ResultDTO<Team> teamResult = this.getTeamByTeamName(teamName);
        if (teamResult.isUnSuccess()) {
            return ResultDTO.failed(teamResult.getErrorMsg());
        }
        Team teamDTO = teamResult.getModel();
        ResultDTO<List<PartnerServiceAreaDTO>> originAreaResult = partnerServiceAreaService.queryPartnerServiceAreaByPartnerId(teamDTO.getId().intValue());
        if (originAreaResult.isUnSuccess()) {
            return ResultDTO.failed(originAreaResult.getErrorMsg());
        }
        List<PartnerServiceAreaDTO> partnerServiceAreaDTOS = originAreaResult.getModel();
        List<ServiceAreaDTO> areas = Lists.newArrayList();
        for (PartnerServiceAreaDTO p : partnerServiceAreaDTOS) {
            if (p != null) {
                ServiceAreaDTO areaDTO = ServiceAreaDTO.builder().provinceId(p.getProvinceId()).provinceName(p.getProvinceName()).cityId(p.getCityId()).cityName(p.getCityName())
                        .countyId(p.getCountyId()).countyName(p.getCountyName()).townId(p.getTownId()).townName(p.getTownName()).build();
                areas.add(areaDTO);
            }
        }
        List<ProvinceDTO> provinceDTOS = this.buildAddressAsChlidren(areas);
        return ResultDTO.succeedWith(provinceDTOS);
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO importExpressServicePoint(ServicePointImportDTO servicePointDTO) {
        if (StringUtils.isBlank(servicePointDTO.getTeamName())) {
            return ResultDTO.failed("合伙人团队名称为空");
        }
        if (CollectionUtils.isEmpty(servicePointDTO.getAreas())) {
            return ResultDTO.failed("合伙人覆盖区域未选择");
        }
        // 获取合伙人团队
        ResultDTO<Team> teamResult = this.getTeamByTeamName(servicePointDTO.getTeamName());
        if (teamResult.isUnSuccess()) {
            return ResultDTO.failed(teamResult.getErrorMsg());
        }
        Team teamDTO = teamResult.getModel();

        //获取团队联系人
        ResultDTO<PrincipalUser> principalUserResult = this.getPrincipalUserByTeamId(teamDTO.getId().intValue(), servicePointDTO.getContact());
        if (principalUserResult.isUnSuccess()) {
            return ResultDTO.failed(principalUserResult.getErrorMsg());
        }
        PrincipalUser principalUser = principalUserResult.getModel();
        //保存导入的配送网点数据
        ResultDTO<ServicePointDTO> resultDTO = this.saveExpressServicePoint(teamDTO, principalUser, servicePointDTO);
        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed(resultDTO.getErrorMsg());
        }
        return ResultDTO.succeed("配送网点导入成功");
    }

    @Override
    public ResultDTO<List<ServicePointDTO>> getDeliveryServicePointNames() {

        List<ServicePointDTO> servicePointNames = servicePointMapper.getAllDeliveryServicePoint();
        if (servicePointNames.size() == 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查询不到服务网点名称");
        }

        return ResultDTO.succeedWith(servicePointNames);
    }

    /**
     * 保存导入的网点
     *
     * @param teamDTO
     * @param principalUser
     * @param servicePointDTO
     * @return
     */
    private ResultDTO<ServicePointDTO> saveExpressServicePoint(Team teamDTO, PrincipalUser principalUser, ServicePointImportDTO servicePointDTO) {
        ServicePointDTO servicePoint = ServicePointDTO.builder().address(StringUtils.isBlank(servicePointDTO.getAddress()) ? "无" : servicePointDTO.getAddress())
                .contact(principalUser.getName()).contactTel(principalUser.getPhone())
                .name(teamDTO.getName()).type(ServicePointTypeEnum.EXPRESS.getType())
                .regionId(servicePointDTO.getRegionId() == null ? RegionEnum.SOUTH.getType() : servicePointDTO.getRegionId())
                .warehouseId(servicePointDTO.getWarehouseId() == null ? 3 : servicePointDTO.getWarehouseId())
                .needCollect(Boolean.TRUE).partnerId(teamDTO.getId().intValue()).build();
        servicePoint.setCreateUserId(0);
        servicePoint.setServiceAreas(servicePointDTO.getAreas());
        ResultDTO<ServicePointDTO> servicePointDTOResultDTO = this.saveServicePoint(servicePoint);
        if (servicePointDTOResultDTO.isUnSuccess()) {
            return ResultDTO.failed(servicePointDTOResultDTO.getErrorMsg());
        }
        return servicePointDTOResultDTO;
    }

    /**
     * 根据合伙人团队id，合伙人名称获取对应合伙人（联系人）
     *
     * @param teamId
     * @param contactName
     * @return
     */
    private ResultDTO<PrincipalUser> getPrincipalUserByTeamId(Integer teamId, String contactName) {
//        PrincipalQueryParam principalQueryParam = new PrincipalQueryParam();
//        principalQueryParam.setAccountStatus(1);
//        principalQueryParam.setCrmTeamId(teamId);
        com.baturu.crm.api.dto.ResultDTO<List<PrincipalUser>> crmResult = principalUserRpcService
                .findByParam(PrincipalUserQuery.builder().teamId(teamId.longValue()).accountStatus(1).build());
        if (!crmResult.isSuccess() || CollectionUtils.isEmpty(crmResult.getData())) {
            log.info("根据合伙人团队id查询合伙人信息为空：{}:{}", teamId,crmResult);
            return ResultDTO.failed("查询合伙人团队联系人为空");
        }
        if (StringUtils.isNotBlank(contactName)) {
            Optional<PrincipalUser> any = crmResult.getData().stream().filter(p -> p.getName().contains(contactName)).findAny();
            if (any.isPresent()) {
                return ResultDTO.succeedWith(any.get());
            }
        }
        return ResultDTO.succeedWith(crmResult.getData().get(0));
    }

    /**
     * 根据合伙人团队名称获取合伙人团队信息
     *
     * @param teamName
     * @return
     */
    private ResultDTO<Team> getTeamByTeamName(String teamName) {
//        TeamQueryParam teamQueryParam = new TeamQueryParam();
//        teamQueryParam.setName(teamName);
//        teamQueryParam.setStatuses(Sets.newLinkedHashSet(1));
        com.baturu.crm.api.dto.ResultDTO<List<Team>> crmResult = teamRpcService.list(TeamQuery.builder().name(teamName).status(1).build());
        if (!crmResult.isSuccess() || CollectionUtils.isEmpty(crmResult.getData())) {
            log.info("根据合伙人团队名称查询结果为空：{}:{}", teamName,crmResult);
            return ResultDTO.failed("查询结果为空，请核实对应合伙人团队名称是否正确");
        }
        Team team = crmResult.getData().get(0);
        return ResultDTO.succeedWith(team);
    }
}
