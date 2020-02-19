package com.baturu.zd.service.business;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.ServiceAreaDTO;
import com.baturu.zd.dto.wx.AmapKeyWordResultDTO;
import com.baturu.zd.mapper.common.ServiceAreaMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * created by ketao by 2019/05/28
 **/
@Service
@Slf4j
public class AddressAutoMatchServiceImpl implements AddressAutoMatchService {

    @Value("${zd.weChat.addKeyWordUrl}")
    private String addKeyWordUrl;

    @Value("${zd.weChat.getTownShipUrl}")
    private String getTownShipUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    private final String tipsKey = "tips";//建议提示列表key
    private final String regeocodeKey = "regeocode";//逆地理编码列表key
    private final String addressComponentKey = "addressComponent";//地址元素列表key
    private final String townshipKey = "township";//坐标点所在乡镇/街道key
    private final String cityKey = "city";//坐标点所在城市key

    @Override
    public ResultDTO<List<AmapKeyWordResultDTO>> addListByKeyWord(String keyWord) {
        if (StringUtils.isBlank(keyWord)) {
            return ResultDTO.failed("关键字为空");
        }
        String url = this.addKeyWordUrl + keyWord;
        JSONObject jsonObject = restTemplate.getForObject(url, JSONObject.class);
        if (jsonObject != null && jsonObject.get(this.tipsKey) != null) {
            List<AmapKeyWordResultDTO> amapKeyWordResultDTOS = JSONArray.parseArray(JSON.toJSONString(jsonObject.get(this.tipsKey)), AmapKeyWordResultDTO.class);
            List<AmapKeyWordResultDTO> filterList = amapKeyWordResultDTOS.stream()
                    .filter(amapKeyWordResultDTO -> StringUtils.isNotBlank(amapKeyWordResultDTO.getLocation()) && !"[ ]".equals(amapKeyWordResultDTO.getLocation()))
                    .collect(Collectors.toList());
            return ResultDTO.succeedWith(filterList);
        }
        log.info("高德关键字【{}】获取内容为空：{}", keyWord, jsonObject);
        return ResultDTO.failed("获取列表为空");
    }

    @Override
    public ResultDTO<ServiceAreaDTO> matchAreaByLocation(String location, Integer type) {
        if (StringUtils.isBlank(location)) {
            return ResultDTO.failed("经纬度参数为空");
        }
        if (type == null || type == 0) {
            return ResultDTO.failed("地址类型为空");
        }
        String url = this.getTownShipUrl + location;
        JSONObject jsonObject = restTemplate.getForObject(url, JSONObject.class);
        if (jsonObject != null && jsonObject.get(this.regeocodeKey) != null) {
            JSONObject regeocode = JSONObject.parseObject(JSON.toJSONString(jsonObject.get(this.regeocodeKey)), JSONObject.class);
            if (regeocode.get(this.addressComponentKey) != null) {
                JSONObject addressComponent = JSONObject.parseObject(JSON.toJSONString(regeocode.get(this.addressComponentKey)), JSONObject.class);
                String township = addressComponent.getString(this.townshipKey);
                String city = addressComponent.getString(this.cityKey);
                if (StringUtils.isNotBlank(township)) {
                    if (township.contains("街道")) {
                        township = township.replace("街道", "");
                    }
                    if (city != null && city.contains("市")) {
                        city = city.replace("市", "");
                    }
                    ServiceAreaDTO serviceAreaDTO = null;
                    if (type == 1) {
                        serviceAreaDTO = serviceAreaMapper.queryByCityAndTownForSender(city, township);
                    } else {
                        serviceAreaDTO=serviceAreaMapper.queryByCityAndTownName(city, township);
                    }
                    return ResultDTO.succeedWith(serviceAreaDTO);
                }
            }
        }
        log.info("高德经纬度【{}】获取内容为空：{}", location, jsonObject);
        return ResultDTO.failed("经纬度内容获取为空");
    }

}
