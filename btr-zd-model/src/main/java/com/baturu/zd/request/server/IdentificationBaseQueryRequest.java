package com.baturu.zd.request.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * created by ketao by 2019/03/14
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdentificationBaseQueryRequest implements Serializable {

    private Integer createUserId;


}
