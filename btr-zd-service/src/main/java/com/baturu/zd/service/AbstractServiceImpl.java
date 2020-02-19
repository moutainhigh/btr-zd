package com.baturu.zd.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * created by ketao by 2019/04/16
 **/
public abstract class AbstractServiceImpl {

    protected Page page;

    protected Page getPage(Integer current,Integer size){
        this.page = new Page();
        this.page.setCurrent(current==null?1:current);
        this.page.setSize(size==null?10:size);
        return page;
    }
}
