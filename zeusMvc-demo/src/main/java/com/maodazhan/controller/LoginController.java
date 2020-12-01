package com.maodazhan;

import com.maodazhan.service.AService;
import com.maodazhan.service.BService;
import com.maodazhan.service.CService;
import group.zeus.ioc.annotation.Controller;
import group.zeus.ioc.annotation.Resource;

/**
 * @Author: maodazhan
 * @Date: 2020/11/30 22:01
 */
@Controller
public class UserController {

    @Resource
    private AService aService;

    @Resource
    private BService bService;

    @Resource
    private CService cService;



}
