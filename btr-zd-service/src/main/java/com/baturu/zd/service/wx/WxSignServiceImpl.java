package com.baturu.zd.service.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.kit.kit.guava2.Lists2;
import com.baturu.message.constant.SmsType;
import com.baturu.message.request.SmsMessageRequest;
import com.baturu.message.service.MessageService;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.config.datasource.DataSourceType;
import com.baturu.zd.config.datasource.Datasource;
import com.baturu.zd.constant.WxSignConstant;
import com.baturu.zd.dto.wx.WxSignDTO;
import com.baturu.zd.entity.wx.WxSignEntity;
import com.baturu.zd.mapper.wx.WxSignMapper;
import com.baturu.zd.request.wx.WxSignQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.business.WxSignService;
import com.baturu.zd.service.common.AuthenticationService;
import com.baturu.zd.transform.wx.WxSignTransform;
import com.baturu.zd.util.CheckCodeUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * created by ketao by 2019/02/27
 **/
@Service(interfaceClass = WxSignService.class)
@Component("wxSignService")
@Slf4j
public class WxSignServiceImpl extends AbstractServiceImpl implements WxSignService {

    @Autowired
    private WxSignMapper wxSignMapper;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Reference(check = false)
    private MessageService messageService;


    @Override
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    public ResultDTO<List<WxSignDTO>> selectByParam(WxSignQueryRequest wxSignQueryRequest) {
        QueryWrapper wrapper = this.initQueryWrapper(wxSignQueryRequest);
        List<WxSignEntity> wxSignEntities = wxSignMapper.selectList(wrapper);
        List<WxSignDTO> list = Lists2.transform(wxSignEntities, WxSignTransform.ENTITY_TO_DTO);
        return ResultDTO.succeedWith(list);
    }

    @Override
    public ResultDTO<WxSignDTO> selectById(Integer id) {
        if (id == null) {
            return ResultDTO.failed("id为空");
        }
        WxSignEntity wxSignEntity = wxSignMapper.selectById(id);
        if (wxSignEntity == null) {
            return ResultDTO.failed("微信注册对象不存在：" + id);
        }
        return ResultDTO.succeedWith(WxSignTransform.ENTITY_TO_DTO.apply(wxSignEntity));
    }

    @Override
    public ResultDTO<List<WxSignDTO>> selectInPage(WxSignQueryRequest wxSignQueryRequest) {
        Page page = getPage(wxSignQueryRequest.getCurrent(), wxSignQueryRequest.getSize());
        QueryWrapper wrapper = this.initQueryWrapper(wxSignQueryRequest);
        IPage iPage = wxSignMapper.selectPage(page, wrapper);
        List<WxSignDTO> list = Lists2.transform(iPage.getRecords(), WxSignTransform.ENTITY_TO_DTO);
        return ResultDTO.succeedWith(list);
    }

    @Override
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    public ResultDTO updateWxSign(WxSignDTO wxSignDTO) {
        if (wxSignDTO.getId() == null) {
            return ResultDTO.failed("注册信息id为空");
        }
        WxSignEntity wxSignEntity = WxSignTransform.DTO_TO_ENTITY.apply(wxSignDTO);
        int num = wxSignMapper.updateById(wxSignEntity);
        if (num == 0) {
            return ResultDTO.failed("注册信息更新失败");
        }
        return ResultDTO.succeedWith("注册信息更新成功");
    }


    private QueryWrapper initQueryWrapper(WxSignQueryRequest wxSignQueryRequest) {
        QueryWrapper wrapper = new QueryWrapper();
        if (wxSignQueryRequest.getIds() != null && wxSignQueryRequest.getIds().size() > 0) {
            wrapper.in(WxSignConstant.ID, wxSignQueryRequest.getIds());
        }
        if (StringUtils.isNotBlank(wxSignQueryRequest.getPassword())) {
            wrapper.eq(WxSignConstant.PASSWORD, wxSignQueryRequest.getPassword());
        }
        if (StringUtils.isNotBlank(wxSignQueryRequest.getSignPhone())) {
            wrapper.eq(WxSignConstant.SIGNPHONE, wxSignQueryRequest.getSignPhone());
        }
        if (StringUtils.isNotBlank(wxSignQueryRequest.getOpenId())) {
            wrapper.eq(WxSignConstant.OPEN_ID, wxSignQueryRequest.getOpenId());
        }
        if (wxSignQueryRequest.getOwnerId() != null) {
            wrapper.eq(WxSignConstant.OWNER_ID, wxSignQueryRequest.getOwnerId());
        }
        if (wxSignQueryRequest.getActive() != null) {
            wrapper.eq(WxSignConstant.ACTIVE, wxSignQueryRequest.getActive());
        } else {
            wrapper.eq(WxSignConstant.ACTIVE, Boolean.TRUE);
        }

        return wrapper;
    }


    @Override
    public ResultDTO<String> getCheckCode(String phone, Integer type) {
        if (StringUtils.isBlank(phone) || type == null) {
            return ResultDTO.failed("电话号码为空或验证码类型为空");
        }
        Map<String, String> map = CheckCodeUtil.matchCheckCodeType(type);
        if (map == null) {
            return ResultDTO.failed("验证码类型错误");
        }
        Map<String, String> parameters = Maps.newHashMap();
        ResultDTO<String> resultDTO = this.initCheckCode(phone, map.get(CheckCodeUtil.CHECK_CODE_SUFFIX));
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        String checkCode = resultDTO.getModel();
        parameters.put("content", checkCode);
        try {
            String template = map.get(CheckCodeUtil.CHECK_CODE_MODEL);
            SmsMessageRequest request = SmsMessageRequest.builder()
                    .templateName(template)
                    .parameters(parameters)
                    .build();
            request.setAppKey("btr-zd-publish");
            request.setSmsType(SmsType.SMS_BUSINESS.getValue());
            request.setReceivers(Lists.newArrayList(phone));
            com.baturu.kit.dto.ResultDTO<Void> result = messageService.sendSms(request);
            if(!result.isSuccess()){
                log.error("微信验证码短信发送接口调用失败：{}",result.getMsg());
                return ResultDTO.failed(result.getMsg());
            }
        } catch (Exception e) {
            log.error("微信验证码短信发送异常：",e);
            return ResultDTO.failed("微信验证码短信发送异常："+e.getMessage());
        }
        return ResultDTO.succeedWith(checkCode);
    }

    /**
     * 生成验证码
     *
     * @param phone
     * @param suffix redis key 后缀
     * @return
     */
    private ResultDTO<String> initCheckCode(String phone, String suffix) {
        StringBuilder checkCode = new StringBuilder();
        String checkCodeKey = phone + "_" + suffix;
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            int num = (int) (Math.random() * 9000) + 1000;//随机四位数
            checkCode.append(num);
            String old = operations.get(checkCodeKey);
            if (old != null) {
                redisTemplate.delete(checkCodeKey);
            }
            operations.set(checkCodeKey, checkCode.toString(), 2, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("逐道微信公众号验证码处理异常", e);
            return ResultDTO.failed("逐道微信公众号验证码处理异常");
        }
        return ResultDTO.succeedWith(checkCode.toString());
    }


    @Transactional(rollbackFor = Exception.class)
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    @Override
    public ResultDTO sign(WxSignDTO wxSignDTO) {
        ResultDTO result = this.checkSign(wxSignDTO);
        if (result.isUnSuccess()) {
            return result;
        }

        WxSignEntity wxSignEntity = WxSignTransform.DTO_TO_ENTITY.apply(wxSignDTO);
        try {
            int num = wxSignMapper.insert(wxSignEntity);
            if (num == 0) {
                return ResultDTO.failed("用户注册失败");
            }
            //注册成功，保存登录信息
            wxSignDTO.setId(wxSignEntity.getId());
            wxSignDTO.setOwnerId(wxSignEntity.getOwnerId());
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            operations.set(wxSignDTO.getOpenId(), JSONObject.toJSONString(wxSignDTO));
            return ResultDTO.succeedWith("用户注册成功");

        } catch (Exception e) {
            log.error("微信用户注册异常", e);
            return ResultDTO.failed("微信用户注册异常:" + e.getMessage());
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    @Override
    public ResultDTO<WxSignDTO> saveChildAccount(WxSignDTO wxSignDTO) {
        ResultDTO resultDTO = this.checkChildAccount(wxSignDTO);
        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed(resultDTO.getErrorMsg());
        }
        ResultDTO signResult = this.isSigned(wxSignDTO.getSignPhone());
        if (signResult.isUnSuccess()) {
            //子账号为注册过的账号
            if (WxSignConstant.DUPLICATE_SIGN_PHONE.equals(signResult.getCode())) {
                WxSignDTO temp = (WxSignDTO) signResult.getModel();
                if (!wxSignDTO.getPassword().equals(temp.getPassword())) {
                    return  ResultDTO.failed(502,"该子账号已注册，请提供个人密码");
                }
                if(temp.getOwnerId() != 0){
                    return ResultDTO.failed("子账号已被绑定，如需添加，子账号需先解绑");
                }
                temp.setOwnerId(wxSignDTO.getOwnerId());
//                redisTemplate.delete(temp.getOpenId());//删除登录信息
                ResultDTO updateResult = this.updateWxSign(temp);
                if (updateResult.isUnSuccess()) {
                    log.info("子账号添加失败{}", temp);
                    return ResultDTO.failed("子账号添加失败");
                }
                return ResultDTO.succeedWith(temp);
            }
            return ResultDTO.failed(signResult.getErrorMsg());
        }
        WxSignEntity wxSignEntity = WxSignTransform.DTO_TO_ENTITY.apply(wxSignDTO);
        try {
            int num = wxSignMapper.insert(wxSignEntity);
            if (num == 0) {
                return ResultDTO.failed("子账号添加失败");
            }
            wxSignDTO.setId(wxSignEntity.getId());
            return ResultDTO.succeedWith(wxSignDTO);
        } catch (Exception e) {
            log.error("微信用户添加子账号异常", e);
            return ResultDTO.failed("微信用户添加子账号异常:" + e.getMessage());
        }
    }

    /**
     * 检验账号是否被注册
     *
     * @param signPhone
     * @return
     */
    private ResultDTO isSigned(String signPhone) {
        if (StringUtils.isBlank(signPhone)) {
            return ResultDTO.failed("注册账号为空");
        }
        ResultDTO<List<WxSignDTO>> resultDTO = this.selectByParam(WxSignQueryRequest.builder().signPhone(signPhone).build());
        if (resultDTO.isSuccess() && resultDTO.getModel().size() > 0) {
            return ResultDTO.failedWith(resultDTO.getModel().get(0), WxSignConstant.DUPLICATE_SIGN_PHONE, "该账号已被注册");
        }
        return ResultDTO.succeed();
    }

    @Transactional(rollbackFor = Exception.class)
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    @Override
    public ResultDTO wxLogin(WxSignDTO wxSignDTO) {
        ResultDTO resultDTO = this.checkLogin(wxSignDTO);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        ResultDTO childResult = this.updateOpenIdOfChildAccount(wxSignDTO);
        if (childResult.isUnSuccess()) {
            return childResult;
        }
        //查询该账号是否注册
        ResultDTO<List<WxSignDTO>> signResult = this.selectByParam(WxSignQueryRequest.builder().openId(wxSignDTO.getOpenId())
                .signPhone(wxSignDTO.getSignPhone()).build());
        if (signResult.isUnSuccess()) {
            log.error("注册信息查询异常，{}，{}", signResult.getMsg(), wxSignDTO.getSignPhone());
            return ResultDTO.failed("注册信息查询异常");
        }
        List<WxSignDTO> signs = signResult.getModel();
        if (signs.size() == 0) {
            log.info("微信公众号登录参数：{}", wxSignDTO);
            return ResultDTO.failed("账号未注册，请先注册");
        } else if (!signs.get(0).getPassword().equals(wxSignDTO.getPassword()) && WxSignConstant.LOGIN_BY_PW.equals(wxSignDTO.getLoginType())) {
            return ResultDTO.failed("用户名或密码错误");
        }
        //登录成功，保存登录信息
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(wxSignDTO.getOpenId(), JSONObject.toJSONString(signs.get(0)));
        return ResultDTO.succeedWith("登录成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    @Override
    public ResultDTO updatePassword(WxSignDTO wxSignDTO) {
        ResultDTO resultDTO = this.checkUpdatePass(wxSignDTO);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        WxSignEntity wxSignEntity = WxSignEntity.builder().id(wxSignDTO.getId()).password(wxSignDTO.getPassword()).build();
        int num = wxSignMapper.updateById(wxSignEntity);
        if (num == 0) {
            return ResultDTO.failed("密码修改失败");
        }
        //删除登录信息
        redisTemplate.delete(wxSignDTO.getOpenId());
        return ResultDTO.succeedWith("密码修改成功，请重新登录");

    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public ResultDTO deleteChildAccount(Integer id) {
        if (id == null) {
            return ResultDTO.failed("子账号id为空");
        }
        WxSignEntity wxSignEntity = wxSignMapper.selectById(id);
        if (wxSignEntity == null) {
            log.info("子账号不存在:{}", id);
            return ResultDTO.failed("子账号不存在");
        }
        //重置母账号
        wxSignEntity.setOwnerId(0);
        try {
            int num = wxSignMapper.updateById(wxSignEntity);
            if (num == 0) {
                return ResultDTO.failed("子账号删除失败：" + id);
            }
            return ResultDTO.succeedWith("子账号删除成功");
        } catch (Exception e) {
            log.error("子账号删除异常", e);
            return ResultDTO.failed("子账号删除异常");
        }
    }

    /**
     * 子账号登录时更新openId
     *
     * @return
     */
    private ResultDTO updateOpenIdOfChildAccount(WxSignDTO wxSignDTO) {
        ResultDTO<List<WxSignDTO>> resultDTO = this.selectByParam(WxSignQueryRequest.builder().openId(wxSignDTO.getSignPhone())
                .signPhone(wxSignDTO.getSignPhone()).build());
        if (resultDTO.isUnSuccess() || resultDTO.getModel().size() == 0) {
            log.info("子账号查询为空:{}", wxSignDTO.getSignPhone());
            //非子账号第一次登录
            return ResultDTO.succeed();
        }
        WxSignDTO wxSign = resultDTO.getModel().get(0);
        WxSignEntity wxSignEntity = wxSignMapper.selectById(wxSign.getId());
        if (wxSignEntity.getOpenId().equals(wxSignEntity.getSignPhone())) {
            try {
                wxSignEntity.setOpenId(wxSignDTO.getOpenId());
                int num = wxSignMapper.updateById(wxSignEntity);
                if (num == 0) {
                    return ResultDTO.failed("子账号openId更新失败");
                }
            } catch (Exception e) {
                log.error("子账号openId更新异常：", e);
                return ResultDTO.failed("子账号openId更新异常");
            }
        }
        return ResultDTO.succeed();
    }

    /**
     * 密码修改参数校验
     *
     * @param wxSignDTO
     * @return
     */
    private ResultDTO checkUpdatePass(WxSignDTO wxSignDTO) {
        if (wxSignDTO == null) {
            return ResultDTO.failed("密码修改参数为空");
        }
        if (wxSignDTO.getPasswordType() == null) {
            return ResultDTO.failed("密码修改类型为空");
        }
        WxSignDTO wxSign = authenticationService.getWxSign();
        if (WxSignConstant.UPDATE_PASSWORDE_BY_ORIGIN.equals(wxSignDTO.getPasswordType())) {
            if (StringUtils.isBlank(wxSignDTO.getPassword()) || StringUtils.isBlank(wxSignDTO.getOriginPassword())) {
                return ResultDTO.failed("新密码或原密码为空");
            }
            if (!wxSignDTO.getOriginPassword().equals(wxSign.getPassword())) {
                log.warn("原密码{}匹配错误，{}", wxSign.getPassword(), wxSignDTO.getOriginPassword());
                return ResultDTO.failed("原密码错误");
            }
        }
        if (WxSignConstant.UPDATE_PASSWORDE_BY_CHECK_CODE.equals(wxSignDTO.getPasswordType())) {
            if (StringUtils.isBlank(wxSignDTO.getSignPhone())) {
                return ResultDTO.failed("注册手机号为空");
            }
            if (StringUtils.isBlank(wxSignDTO.getCheckCode())) {
                return ResultDTO.failed("验证码为空");
            }
            if (StringUtils.isBlank(wxSignDTO.getPassword())) {
                return ResultDTO.failed("新密码为空");
            }
            if (!wxSignDTO.getSignPhone().equals(wxSign.getSignPhone())) {
                log.warn("非注册手机号{}：系统注册手机号：{}", wxSignDTO.getSignPhone(), wxSign.getSignPhone());
                return ResultDTO.failed("该手机号非账号注册时手机号");
            }
            ResultDTO resultDTO = authenticationService.checkCode(wxSignDTO.getSignPhone(), wxSignDTO.getCheckCode(), WxSignConstant.MODIFY_CHECK_CODE);
            if (resultDTO.isUnSuccess()) {
                return resultDTO;
            }
        }
        //设置登录账号信息
        wxSignDTO.setId(wxSign.getId());
        wxSignDTO.setOpenId(wxSign.getOpenId());
        return ResultDTO.succeed();
    }


    private ResultDTO checkLogin(WxSignDTO wxSignDTO) {
        if (wxSignDTO == null) {
            return ResultDTO.failed("参数为空");
        }
        if (StringUtils.isBlank(wxSignDTO.getOpenId())) {
            return ResultDTO.failed("微信账号主键为空");
        }
        if (wxSignDTO.getLoginType() == null) {
            return ResultDTO.failed("登录方式为空");
        }
        if (StringUtils.isBlank(wxSignDTO.getSignPhone())) {
            return ResultDTO.failed("手机号为空");
        }
        if (WxSignConstant.LOGIN_BY_PW.equals(wxSignDTO.getLoginType())) {
            if (StringUtils.isBlank(wxSignDTO.getPassword())) {
                return ResultDTO.failed("密码为空");
            }
        }
        if (WxSignConstant.LOGIN_BY_CHECK_CODE.equals(wxSignDTO.getLoginType())) {
            if (StringUtils.isBlank(wxSignDTO.getCheckCode())) {
                return ResultDTO.failed("验证码为空");
            }
            ResultDTO resultDTO = authenticationService.checkCode(wxSignDTO.getSignPhone(), wxSignDTO.getCheckCode(), WxSignConstant.LOGIN_CHECK_CODE);
            if (resultDTO.isUnSuccess()) {
                return resultDTO;
            }
        }
        return ResultDTO.succeed();
    }

    private ResultDTO checkSign(WxSignDTO wxSignDTO) {
        if (wxSignDTO == null) {
            return ResultDTO.failed("参数为空");
        }
        if (StringUtils.isBlank(wxSignDTO.getSignPhone())) {
            return ResultDTO.failed("手机号为空");
        } else {
            ResultDTO signed = this.isSigned(wxSignDTO.getSignPhone());
            if(signed.isUnSuccess()){
                return signed;
            }
        }
        if (StringUtils.isBlank(wxSignDTO.getPassword())) {
            return ResultDTO.failed("密码为空");
        }
        if (StringUtils.isBlank(wxSignDTO.getCheckCode())) {
            return ResultDTO.failed("验证码为空");
        }
   /*     if (StringUtils.isBlank(wxSignDTO.getNickname())) {
            return ResultDTO.failed("微信账号名称为空");
        }*/
        if (StringUtils.isBlank(wxSignDTO.getOpenId())) {
            return ResultDTO.failed("微信账号openId为空");
        } else {
            ResultDTO<List<WxSignDTO>> resultDTO = this.selectByParam(WxSignQueryRequest.builder().openId(wxSignDTO.getOpenId()).build());
            if (resultDTO.isSuccess() && resultDTO.getModel().size() > 0) {
                return ResultDTO.failed("该微信账号已经注册过");
            }
        }
        ResultDTO resultDTO = authenticationService.checkCode(wxSignDTO.getSignPhone(), wxSignDTO.getCheckCode(), WxSignConstant.REGISTER_CHECK_CODE);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        return ResultDTO.succeed();
    }


    private ResultDTO checkChildAccount(WxSignDTO wxSignDTO) {
        if (wxSignDTO == null) {
            return ResultDTO.failed("添加子账号参数为空");
        }
        if (wxSignDTO.getId() == null) {
            if (StringUtils.isBlank(wxSignDTO.getSignPhone())) {
                return ResultDTO.failed("子账号注册手机号为空");
            }
            if (StringUtils.isBlank(wxSignDTO.getPassword())) {
                return ResultDTO.failed("子账号注册密码为空");
            }
            if (wxSignDTO.getOwnerId() == null || wxSignDTO.getOwnerId() == 0) {
                return ResultDTO.failed("母账号为空");
            }
        }
        return ResultDTO.succeed();
    }

}
