package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.AbstractLogDTO;

import java.util.Map;

/**
 * 更新日志service
 * @author liuduanyang
 * @since 2019/5/17
 */
public interface EditLogService {

    /**
     * 生成更新日志
     * @param target 目标对象
     * @return type 字段类型
     * @throws Exception
     */
    <T extends AbstractLogDTO> ResultDTO create(T target, Map<String, Object> fieldMap) throws Exception;

    /**
     * 获取更新日志
     * @param orderNo
     * @param type
     * @return ResultDTO
     * @throws Exception
     */
    ResultDTO getEditLogs(String orderNo, String type, Integer current, Integer size) throws Exception;
}
