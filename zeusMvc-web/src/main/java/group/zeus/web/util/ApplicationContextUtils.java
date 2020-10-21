package group.zeus.web.util;

import group.zeus.ioc.ApplicationContext;

/**
 * @Author: maodazhan
 * @Date: 2020/10/17 19:59
 */
public class ApplicationContextUtils {

    private static ApplicationContext applicationContext;

    public static void refresh(){
        applicationContext = new ApplicationContext();
    }

    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }
}
