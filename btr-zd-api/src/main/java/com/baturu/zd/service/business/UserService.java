package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.UserDTO;
import com.baturu.zd.request.business.UserQueryRequest;

/**
 * 对zd_user操作的API
 * @author CaiZhuliang
 * @since 2019-3-20
 */
public interface UserService {

    /**
     * 根据账号查询用户信息
     * @param username 账号
     */
    ResultDTO<UserDTO> queryUserByUsername(String username);

    /**
     * 用户保存
     * @param userDTO
     * @return
     */
    ResultDTO<UserDTO> saveUser(UserDTO userDTO);

    /**
     * 分页查询用户
     * @param userQueryRequest
     * @return
     */
    ResultDTO queryUsersInPage(UserQueryRequest userQueryRequest);

    /**
     * 根据id获取用户（包含角色id）
     * @param id
     * @return
     */
    ResultDTO<UserDTO> queryUserById(Integer id);
}
