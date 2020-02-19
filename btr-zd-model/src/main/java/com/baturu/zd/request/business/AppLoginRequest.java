package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by caizhuliang on 2019/3/22.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppLoginRequest implements Serializable {

    private String username;
    private String password;

}
