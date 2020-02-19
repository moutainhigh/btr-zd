package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.common.UserDTO;
import com.baturu.zd.entity.common.UserEntity;
import com.baturu.zd.entity.common.UserRoleEntity;
import com.baturu.zd.mapper.common.UserMapper;
import com.baturu.zd.mapper.common.UserRoleMapper;
import com.baturu.zd.request.business.UserQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.common.UserTransform;
import com.baturu.zd.util.EncryptedUtil;
import com.baturu.zd.util.ObjectValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Set;

/**
 * 对zd_user操作的API的实现类
 * @author CaiZhuliang
 * @since 2019-3-20
 */
@Service("userService")
@Slf4j
public class UserServiceImpl extends AbstractServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public ResultDTO<UserDTO> queryUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return ResultDTO.failed("用户名不能为空");
        }
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(AppConstant.TABLE.ZD_USER.COLUMN_NAME.USERNAME, username).eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, AppConstant.TABLE.ACTIVE_VALID);
        UserEntity entity = userMapper.selectOne(wrapper);
        if (null == entity) {
            return ResultDTO.failed("用户名或密码错误!");
        }
        log.info("登录账号：{}",entity);
        return ResultDTO.succeedWith(UserTransform.ENTITY_TO_DTO.apply(entity), AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public ResultDTO<UserDTO> saveUser(UserDTO userDTO){
        if(ObjectValidateUtil.isAllFieldNull(userDTO)){
            return ResultDTO.failed("参数为空");
        }
        ResultDTO<UserDTO> resultDTO = this.checkUser(userDTO);
        if(resultDTO.isUnSuccess()){
            log.warn("用户保存参数：{}",userDTO);
            return resultDTO;
        }
        ResultDTO<UserDTO> userDTOResultDTO = this.queryUserByUsername(userDTO.getUsername());
        if(userDTOResultDTO.isSuccess()){
            if(!userDTOResultDTO.getModel().getId().equals(userDTO.getId())) {
                log.info("用户账号已存在:{}", userDTO.getUsername());
                return ResultDTO.failed("用户账号已存在");
            }
        }
        int num;
        try {
            UserEntity userEntity = UserTransform.DTO_TO_ENTITY.apply(userDTO);
            if (StringUtils.isNotBlank(userEntity.getPassword())) {
                userEntity.setPassword(EncryptedUtil.generate(userDTO.getPassword(),userDTO.getUsername()));
            }
            if(userDTO.getId()==null) {
                num = userMapper.insert(userEntity);
                if (num > 0) {
                    userDTO.setId(userEntity.getId());
                }
            }else{
                num = userMapper.updateById(userEntity);
                if (num > 0 && Boolean.FALSE.equals(userDTO.getActive())) {
                    //用户删除，删除对应用户关联角色记录
                    userRoleMapper.deleteUserRolesByUserId(userDTO.getId());
                }
            }
            if (num == 0) {
                return ResultDTO.failed("用户保存失败");
            }
            //保存对应角色记录
            this.batchSaveUserRoles(userDTO);
            return ResultDTO.succeedWith(userDTO);
        } catch (Exception e){
            log.error("userServiceImpl#saveUser用户保存异常",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
            return ResultDTO.failed("用户保存异常");
        }
    }

    /**
     * 保存用户角色关联记录
     * @param userDTO
     */
    private void batchSaveUserRoles(UserDTO userDTO){
        if(userDTO.getRoleIds()!=null) {
            Set<Integer> roleIds = userRoleMapper.queryRoleIdsByUserId(userDTO.getId());
            List<UserRoleEntity> entities = Lists.newArrayList();
            userDTO.getRoleIds().stream().forEach(u -> {
                UserRoleEntity userRoleEntity = UserRoleEntity.builder().roleId(u).userId(userDTO.getId()).build();
                entities.add(userRoleEntity);
            });
            //用户新增
            if (CollectionUtils.isEmpty(roleIds) && CollectionUtils.isNotEmpty(entities)) {
                userRoleMapper.batchSaveUserRoles(entities);
            }//用户更新（数量相等，原角色id集合与新角色id集合互相包括为不变）
            else if (!(roleIds.size() == userDTO.getRoleIds().size()
                    && roleIds.containsAll(userDTO.getRoleIds())
                    && userDTO.getRoleIds().containsAll(roleIds))) {
                userRoleMapper.deleteUserRolesByUserId(userDTO.getId());
                userRoleMapper.batchSaveUserRoles(entities);
            }
        }

    }

    private ResultDTO<UserDTO> checkUser(UserDTO userDTO){
        if (userDTO.getId() == null) {
            if (StringUtils.isBlank(userDTO.getUsername())) {
                return ResultDTO.failed("用户账号为空");
            }
            if (StringUtils.isBlank(userDTO.getPassword())) {
                return ResultDTO.failed("用户密码为空");
            }
            if (StringUtils.isBlank(userDTO.getName())) {
                return ResultDTO.failed("用户名称为空");
            }
            if (StringUtils.isBlank((userDTO.getPhone()))) {
                return ResultDTO.failed("联系电话为空");
            }
            if (userDTO.getServicePointId() == null) {
                return ResultDTO.failed("收单网点为空");
            }
        }
        return ResultDTO.succeed();
    }


    @Override
    public ResultDTO queryUsersInPage(UserQueryRequest userQueryRequest){
        Page page = getPage(userQueryRequest.getCurrent(), userQueryRequest.getSize());
        List<UserDTO> userDTOS = userMapper.queryUsersInPage(page, userQueryRequest);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(userDTOS);
        pageDTO.setTotal(page.getTotal());
        return ResultDTO.succeedWith(pageDTO);
    }

    @Override
    public ResultDTO<UserDTO> queryUserById(Integer id){
        UserEntity userEntity = userMapper.selectById(id);
        if(userEntity==null){
            log.warn("无效id:{}",id);
            return ResultDTO.failed("无效id");
        }
        UserDTO userDTO = UserTransform.ENTITY_TO_DTO.apply(userEntity);
        Set<Integer> roleIds = userRoleMapper.queryRoleIdsByUserId(id);
        userDTO.setRoleIds(roleIds);
        return ResultDTO.succeedWith(userDTO);
    }
}
