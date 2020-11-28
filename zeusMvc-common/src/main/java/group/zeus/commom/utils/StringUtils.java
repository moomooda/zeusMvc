package group.zeus.commom.utils;

/**
 * @Author: maodazhan
 * @Date: 2020/11/24 21:34
 */
public class StringUtils {

    public static String lowFirst(String oldStr){
        char [] chars = oldStr.toCharArray();
        chars[0] +=32;
        return String.valueOf(chars);
    }
}
