package com.baturu.zd.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * create by pengdi in 2019/5/8
 * 微信地址簿VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxAddressVO implements Serializable {

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机
     */
    private String phone;

    /**
     * 省份id
     */
    private Integer provinceId;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 城市id
     */
    private Integer cityId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 区县id
     */
    private Integer countyId;

    /**
     * 区县名称
     */
    private String countyName;

    /**
     * 城镇id
     */
    private Integer townId;

    /**
     * 城镇名称
     */
    private String townName;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 是否默认地址
     */
    private Boolean isDefault;


    /**
     * 地址类型 1：发货地址，2：收货地址
     * @see com.baturu.zd.enums.WxAddressTypeEnum
     */
    private Integer type;

    /**
     * DTO => VO
     * @param wxAddressDTO
     * @return
     */
    public static WxAddressVO copy(WxAddressDTO wxAddressDTO){
        WxAddressVO wxAddressVO = new WxAddressVO();
        BeanUtils.copyProperties(wxAddressDTO,wxAddressVO);
        return wxAddressVO;
    }

    /**
     * VO => DTO
     * @param wxAddressVO
     * @return
     */
    public static WxAddressDTO toDTO(WxAddressVO wxAddressVO){
        WxAddressDTO wxAddressDTO = new WxAddressDTO();
        BeanUtils.copyProperties(wxAddressVO,wxAddressDTO);
        return wxAddressDTO;
    }

}
