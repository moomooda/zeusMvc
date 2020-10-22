package group.zeus.web;

import group.zeus.ioc.annotation.Controller;
import group.zeus.web.annotation.RequestBody;
import group.zeus.web.annotation.RequestMapping;
import group.zeus.web.annotation.RequestMethod;
import group.zeus.web.annotation.RequestParam;
import group.zeus.web.response.JsonResponse;
import group.zeus.web.response.Response;

/**
 * @Author: maodazhan
 * @Date: 2020/10/21 20:25
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public Response testPost(@RequestBody TestPeople people){
        String data = String.format("Controller (%s) Get方法调用成功, people = %s", getClass().getName(), people);
        Response response = new JsonResponse();
        response.put("data",data);
        return response;
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Response testGet(@RequestParam(value = "name", required = false, defaultValue = "lkqqqqqqqqq") String name){
        String data = String.format("Controller (%s) Get方法调用成功, name = %s", getClass().getName(), name);
        Response response = new JsonResponse();
        response.put("data", data);
        return response;
    }
}
