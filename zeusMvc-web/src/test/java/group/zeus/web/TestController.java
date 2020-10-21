package group.zeus.web;

import group.zeus.ioc.annotation.Controller;
import group.zeus.web.annotation.RequestBody;
import group.zeus.web.annotation.RequestMapping;
import group.zeus.web.annotation.RequestMethod;
import group.zeus.web.annotation.RequestParam;

/**
 * @Author: maodazhan
 * @Date: 2020/10/21 20:25
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public String testPost(@RequestBody TestPeople people){
        String returnValue = String.format("Controller (%s) Get方法调用成功, people = %s", getClass().getName(), people);
        System.out.println(returnValue);
        return returnValue;
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String testGet(@RequestParam(value = "name", required = false, defaultValue = "lkqqqqqqqqq") String name){
        String returnValue = String.format("Controller (%s) Get方法调用成功, name = %s", getClass().getName(), name);
        System.out.println(returnValue);
        return returnValue;
    }
}
