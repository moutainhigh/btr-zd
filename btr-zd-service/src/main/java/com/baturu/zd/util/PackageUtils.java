package com.baturu.zd.util;

import com.baturu.zd.enums.PackageOperateTypeEnum;
import com.baturu.zd.enums.PackageStateEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理包裹相关操作的工具类
 * @author CaiZhuliang
 * @since 2018-4-18
 */
@Slf4j
public class PackageUtils {

    private PackageUtils() {}

    /**
     * 获取需要被校验的包裹状态。比如：验收操作-已开单、已装车、已收货、已发货、已装运
     * @param operateType 操作类型 0:预约下单 1:开单 2:收货 3:发货 4:验收（合伙人收货）5：配送 6：收款 7:拒收
     * @return
     */
    public static List<Integer> getHavetoCheckPackageState(Integer operateType) {
        List<Integer> packageStates = Lists.newArrayList();
        if (PackageOperateTypeEnum.RECEIVE.getType().equals(operateType)) {
            packageStates.addAll(doReceive());
        } else if (PackageOperateTypeEnum.DELIVER.getType().equals(operateType)) {
            packageStates.addAll(doDeliver());
        } else if (PackageOperateTypeEnum.CHECK.getType().equals(operateType)) {
            packageStates.addAll(doCheck());
        } else if (PackageOperateTypeEnum.EXPRESS.getType().equals(operateType)) {
            packageStates.addAll(doExpress());
        }
        return packageStates;
    }

    private static List<Integer> doExpress() {
        List<Integer> packageStates = new ArrayList<>(6);
        packageStates.addAll(doCheck());
        packageStates.add(PackageStateEnum.CHECKED.getType());
        return packageStates;
    }

    private static List<Integer> doCheck() {
        List<Integer> packageStates = new ArrayList<>(5);
        packageStates.addAll(doDeliver());
        packageStates.add(PackageStateEnum.DELIVERED.getType());
        packageStates.add(PackageStateEnum.SHIPMENT.getType());
        return packageStates;
    }

    private static List<Integer> doDeliver() {
        List<Integer> packageStates = new ArrayList<>(3);
        packageStates.addAll(doReceive());
        packageStates.add(PackageStateEnum.RECEIVED.getType());
        return packageStates;
    }

    private static List<Integer> doReceive() {
        List<Integer> packageStates = new ArrayList<>(2);
        packageStates.add(PackageStateEnum.ORDERED.getType());
        packageStates.add(PackageStateEnum.LOADED.getType());
        return packageStates;
    }

}
