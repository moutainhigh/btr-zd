package com.baturu.zd.controller.app;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.app.AppMenuPermissionDTO;
import com.baturu.zd.dto.app.AppUserLoginInfoDTO;
import com.baturu.zd.dto.common.ServicePointDTO;
import com.baturu.zd.dto.common.UserDTO;
import com.baturu.zd.request.business.AppLoginRequest;
import com.baturu.zd.service.business.PermissionService;
import com.baturu.zd.service.business.ServicePointService;
import com.baturu.zd.service.business.UserService;
import com.baturu.zd.util.EncryptedUtil;
import com.baturu.zd.util.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * 逐道收单APP登录控制器
 * @author CaiZhuliang
 * @since 2019-3-20
 */
@RestController
@Slf4j
@RequestMapping("app")
public class AppLoginController extends AbstractAppBaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private ServicePointService servicePointService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO login(@RequestBody AppLoginRequest request){
        if (null == request || StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getPassword())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "账号，密码均不能为空");
        }
        String username = request.getUsername();
        String password = request.getPassword();
        // 查询该账号是否存在
        ResultDTO<UserDTO> userDTOResultDTO = userService.queryUserByUsername(username);
        if (userDTOResultDTO.isUnSuccess()) {
            return userDTOResultDTO;
        }
        // 匹配密码是否正确
        UserDTO userDTO = userDTOResultDTO.getModel();
        if (!EncryptedUtil.verify(password, userDTO.getPassword())) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "用户名或密码错误!");
        }
        // 获取当前用户所属网点ID和对应仓库的ID、code
        Integer servicePointId = userDTO.getServicePointId();
        ResultDTO<ServicePointDTO> servicePointResultDTO = servicePointService.queryServicePointById(servicePointId);
        if (servicePointResultDTO.isUnSuccess()) {
            log.info("login : 查询逐道业务网点信息失败。servicePointId = {}, servicePointResultDTO = {}", servicePointId, servicePointResultDTO);
            return ResultDTO.failed("查询逐道业务网点信息失败");
        }
        ServicePointDTO servicePointDTO = servicePointResultDTO.getModel();
        AppUserLoginInfoDTO appUserLoginInfoDTO = AppUserLoginInfoDTO.builder()
                                                        .userId(userDTO.getId())
                                                        .name(userDTO.getName())
                                                        .username(username)
                                                        .servicePointId(servicePointId)
                                                        .warehouseId(servicePointDTO.getWarehouseId())
                                                        .warehouseCode(servicePointDTO.getWarehouseCode())
                                                        .root(userDTO.getRoot())
                                                        .needCollect(servicePointDTO.getNeedCollect())
                                                        .partnerId(servicePointDTO.getPartnerId())
                                                        .build();
        // 获取角色权限
        ResultDTO<List<AppMenuPermissionDTO>> permissionResultDTO = permissionService.queryPermissionByUserId(userDTO.getId());
        if (permissionResultDTO.isUnSuccess()) {
            log.info("login : 调用接口失败。permissionResultDTO = {}， username = {}", permissionResultDTO, username);
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "请联系管理员分配权限");
        }
        appUserLoginInfoDTO.setAppMenuPermissionDTO(permissionResultDTO.getModel());
        // 生成唯一随机码,创建会话信息
        String randomCode = AppConstant.REDIS.PREFIX + UUID.randomUUID().toString();
        redisService.set(randomCode, SerializeUtil.serialize(appUserLoginInfoDTO), liveTime);
        String token = Base64.getEncoder().encodeToString((randomCode + AppConstant.PARAM_SEPARATOR + username).getBytes());
        appUserLoginInfoDTO.setToken(token);
        return ResultDTO.succeedWith(appUserLoginInfoDTO, AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

}
