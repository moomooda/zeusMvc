package group.zeus.ioc.utils;

/**
 * @Author: maodazhan
 * @Date: 2020/10/15 10:59
 */
public class StringUtils {

    public static String lowFirst(String oldStr){
        char [] chars = oldStr.toCharArray();
        chars[0] +=32;
        return String.valueOf(chars);
    }

}
