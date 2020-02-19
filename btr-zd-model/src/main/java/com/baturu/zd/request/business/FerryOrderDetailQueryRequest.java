package com.baturu.zd.request.business;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * create by pengdi in 2019/3/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class FerryOrderDetailQueryRequest extends BaseRequest implements Serializable {

    /**
     * 摆渡单id
     */
    Integer ferryId;
    /**
     * 摆渡单号
     */
    String ferryNo;

    /**
     * 逐道包裹号
     */
    String packageNo;

    /**
     * 创建人/揽收人
     */
    String createUserName;

    /**
     * 揽收时间 起始
     */
    Date startTime;

    /**
     * 揽收时间 结束
     */
    Date endTime;
}
