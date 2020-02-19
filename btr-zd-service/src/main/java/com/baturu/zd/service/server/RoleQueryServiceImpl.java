package com.baturu.zd.service.server;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.RoleConstant;
import com.baturu.zd.dto.common.RoleDTO;
import com.baturu.zd.entity.common.RoleEntity;
import com.baturu.zd.mapper.common.RoleMapper;
import com.baturu.zd.request.server.RoleBaseQueryRequest;
import com.baturu.zd.transform.common.RoleTransform;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * created by ketao by 2019/03/25
 **/
@Service(interfaceClass = RoleQueryService.class )
@Component("roleQueryService")
@Slf4j
public class RoleQueryServiceImpl implements RoleQueryService{

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public ResultDTO<List<RoleDTO>> queryByParam(RoleBaseQueryRequest roleBaseQueryRequest) {
        if(roleBaseQueryRequest==null){
            return ResultDTO.failed("参数为空");
        }
        QueryWrapper wrapper = this.initWrapper(roleBaseQueryRequest);
        List<RoleEntity> list = roleMapper.selectList(wrapper);
        return ResultDTO.succeedWith(Lists2.transform(list,RoleTransform.ENTITY_TO_DTO));
    }

    private QueryWrapper initWrapper(RoleBaseQueryRequest roleBaseQueryRequest){
      QueryWrapper wrapper=new QueryWrapper();
      if (roleBaseQueryRequest.getId()!=null) {
          wrapper.eq(RoleConstant.ID,roleBaseQueryRequest.getId());
      }
      if (roleBaseQueryRequest.getIds()!=null&&roleBaseQueryRequest.getIds().size()>0) {
         wrapper.in(RoleConstant.ID,roleBaseQueryRequest.getIds());
      }
      if(StringUtils.isNotBlank(roleBaseQueryRequest.getCode())){
          wrapper.eq(RoleConstant.CODE,roleBaseQueryRequest.getCode());
      }
      if(StringUtils.isNotBlank(roleBaseQueryRequest.getName())){
          wrapper.eq(RoleConstant.NAME,roleBaseQueryRequest.getName());
      }
      if (roleBaseQueryRequest.getValid() != null) {
          wrapper.eq(RoleConstant.VALID,roleBaseQueryRequest.getValid());
      }
      if(roleBaseQueryRequest.getActive()==null){
          wrapper.eq(RoleConstant.ACTIVE,Boolean.TRUE);
      }else{
          wrapper.eq(RoleConstant.ACTIVE,roleBaseQueryRequest.getActive());
      }
      return wrapper;
    }

    @Override
    public ResultDTO<RoleDTO> queryById(Integer id) {
        if (id==null) {
            return ResultDTO.failed("id为空");
        }
        QueryWrapper wrapper=new QueryWrapper();
        wrapper.eq(RoleConstant.ID,id);
        RoleEntity roleEntity = roleMapper.selectOne(wrapper);
        return ResultDTO.succeedWith(RoleTransform.ENTITY_TO_DTO.apply(roleEntity));
    }
}
