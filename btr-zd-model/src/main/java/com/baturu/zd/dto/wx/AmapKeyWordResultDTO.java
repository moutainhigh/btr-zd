package com.baturu.zd.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 高德关键字获取地址列表接口返回对象
 * created by ketao by 2019/05/28
 **/
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AmapKeyWordResultDTO {

    /**
     * 若数据为POI类型，则返回POI ID;若数据为bus类型，则返回bus id;若数据为busline类型，则返回busline id。
     */
    private String id;

    /**
     * tip名称
     */
    private String name;

    /**
     * 所属区域
     * 省+市+区（直辖市为“市+区”）
     */
    private String district;

    /**
     * 区域编码
     * 六位区县编码
     */
    private String adcode;

    /**
     * tip中心点坐标(纬度,经度)
     * 当搜索数据为busline类型时，此字段不返回
     */
    private String location;

    /**
     * 详细地址
     */
    private String address;

}
