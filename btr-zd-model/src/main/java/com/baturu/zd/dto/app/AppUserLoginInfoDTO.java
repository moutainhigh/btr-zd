package com.baturu.zd.dto.app;

import com.baturu.zd.dto.common.ServicePointDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * APP用户成功登录后的返回信息
 * @author CaiZhuliang
 * @since 2019-3-20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUserLoginInfoDTO implements Serializable {

    /** id */
    private Integer userId;
    /** 账号 */
    private String username;
    /** 用户姓名 */
    private String name;
    /** 权限 */
    private List<AppMenuPermissionDTO> appMenuPermissionDTO;
    /** 令牌 */
    private String token;
    /** 服务网点id */
    private Integer servicePointId;
    /** 网点所绑定的仓库id */
    private Integer warehouseId;
    /** 仓库编码 */
    private String warehouseCode;
    /**网点对应合伙人团队id*/
    private Integer partnerId;
    /** 是否admin；0：否，1：是 */
    private Integer root;

    /**
     * 是否代收  这个字段作用是代收金额是走线上还是走线下（逐道代收费用收取可配置需求）
     */
    private Boolean needCollect;
}
