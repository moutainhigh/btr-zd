package com.baturu.zd.schedule;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author liuduanyang
 * @since 2019/3/26
 */
@Component
@Data
public class OrderNoOffset {

    private int ferryOrderOffset = 1;
    private int transportOrderOffset = 1;
    private int entruckingOffset = 1;

}
