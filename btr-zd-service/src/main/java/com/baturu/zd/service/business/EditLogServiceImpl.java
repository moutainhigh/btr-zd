package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.EditLogConstant;
import com.baturu.zd.dto.AbstractLogDTO;
import com.baturu.zd.dto.EditLogDTO;
import com.baturu.zd.dto.EditLogDetailDTO;
import com.baturu.zd.entity.EditLogEntity;
import com.baturu.zd.entity.FieldDictionaryEntity;
import com.baturu.zd.mapper.EditLogDetailMapper;
import com.baturu.zd.mapper.EditLogMapper;
import com.baturu.zd.mapper.FieldDictionaryMapper;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.EditLogDetailTransform;
import com.baturu.zd.transform.EditLogTransform;
import com.baturu.zd.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 更新日志service
 * @author liuduanyang
 * @since 2019/5/7
 */
@Service("editLogService")
@Slf4j
public class EditLogServiceImpl extends AbstractServiceImpl implements EditLogService {

    @Autowired
    private EditLogMapper editLogMapper;

    @Autowired
    private EditLogDetailMapper editLogDetailMapper;

    @Autowired
    private FieldDictionaryMapper fieldDictionaryMapper;

    private ResultDTO doCreate(EditLogDTO editLogDTO) {
        editLogDTO.setCreateTime(DateUtil.getCurrentDate());
        editLogDTO.setUpdateTime(DateUtil.getCurrentDate());
        editLogDTO.setActive(Boolean.TRUE);

        EditLogEntity editLogEntity = EditLogTransform.DTO_TO_ENTITY.apply(editLogDTO);
        int count = editLogMapper.insert(editLogEntity);

        if (count <= 0) {
            return ResultDTO.failed("创建更新日志信息失败!");
        }

        for (EditLogDetailDTO editLogDetailDTO : editLogDTO.getEditLogDetailDTOList()) {
            editLogDetailDTO.setCreateTime(DateUtil.getCurrentDate());
            editLogDetailDTO.setUpdateTime(DateUtil.getCurrentDate());
            editLogDetailDTO.setCreateUserId(editLogDTO.getCreateUserId());
            editLogDetailDTO.setEditLogId(editLogEntity.getId());
            editLogDetailDTO.setActive(Boolean.TRUE);
            count = editLogDetailMapper.insert(EditLogDetailTransform.DTO_TO_ENTITY.apply(editLogDetailDTO));
            if (count <= 0) {
                return ResultDTO.failed("创建更新日志明细信息失败!");
            }
        }

        return ResultDTO.succeed();
    }

    @Override
    public ResultDTO getEditLogs(String orderNo, String type, Integer current, Integer size) throws Exception {

        Integer offset = (current - 1) * size;

        List<EditLogEntity> editLogEntityList = editLogMapper.getEditLogList(orderNo, offset, size);

        List<EditLogDTO> editLogDTOS = Lists2.transform(editLogEntityList, EditLogTransform.ENTITY_TO_DTO);

        List<EditLogDTO> resultLogDTOS = new ArrayList<>(editLogDTOS.size());
        for (EditLogDTO editLogDTO : editLogDTOS) {
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq(EditLogConstant.EDIT_LOG_ID, editLogDTO.getId());
            wrapper.eq(EditLogConstant.ACTIVE, Boolean.TRUE);
            List<EditLogDetailDTO> editLogDetailDTOS = Lists2.transform(editLogDetailMapper.selectList(wrapper), EditLogDetailTransform.ENTITY_TO_DTO);

            List<EditLogDetailDTO> editLogDetailDTOList = new ArrayList<>(editLogDetailDTOS.size());

            for (EditLogDetailDTO editLogDetailDTO : editLogDetailDTOS) {
                FieldDictionaryEntity fieldDictionaryEntity = fieldDictionaryMapper.getByCode(editLogDetailDTO.getCode(), type);
                if (fieldDictionaryEntity == null) {
                    continue;
                }
                editLogDetailDTO.setName(fieldDictionaryEntity.getName());
                editLogDetailDTOList.add(editLogDetailDTO);
            }

            editLogDTO.setEditLogDetailDTOList(editLogDetailDTOList);

            if (CollectionUtils.isNotEmpty(editLogDetailDTOS)) {
                resultLogDTOS.add(editLogDTO);
            }
        }

        Long total = editLogMapper.getEditLogCount(orderNo);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(resultLogDTOS);
        pageDTO.setTotal(total);
        return ResultDTO.succeedWith(pageDTO);
    }

    @Override
    public <T extends AbstractLogDTO> ResultDTO create(T target, Map<String, Object> fieldMap) throws Exception {
        Class clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();
        // 更新日志记录
        EditLogDTO editLogDTO = EditLogDTO.builder()
                .identification(target.getIdentification())
                .createUserId(target.getCreateUserId())
                .createUserName(target.getCreateUserName())
                .updateUserId(target.getUpdateUserId())
                .updateUserName(target.getUpdateUserName())
                .build();

        List<EditLogDetailDTO> editLogDetailDTOS = new ArrayList<>(fields.length);

        for (Field field : fields) {
            field.setAccessible(true);
            Object obj = field.get(target);
            if (obj != null && !"sync".equals(field.getName())) {
                String code = target.getType() + AppConstant.UNDERLINE_SEPARATOR + field.getName();
                EditLogDetailDTO editLogDetailDTO = EditLogDetailDTO.builder()
                        .beforeUpdateValue(fieldMap.get(field.getName()))
                        .afterUpdateValue(obj)
                        .code(code)
                        .build();

                editLogDetailDTOS.add(editLogDetailDTO);
            }
        }

        editLogDTO.setEditLogDetailDTOList(editLogDetailDTOS);

        return doCreate(editLogDTO);
    }
}
