package utils;

import java.util.UUID;

/**
 * @author by wyl
 * @date 2021/9/19.10点23分
 */
public class IDUtils {

    /**
     * 随机生成ID串
     */
    public static String getID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
