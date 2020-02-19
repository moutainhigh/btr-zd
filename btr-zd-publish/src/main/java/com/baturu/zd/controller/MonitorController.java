package com.baturu.zd.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/monitor")
public class MonitorController {


    @RequestMapping(value = "/healthcheck",method = RequestMethod.GET)
    public Object healthCheck(){
        return "ok";
    }

}
