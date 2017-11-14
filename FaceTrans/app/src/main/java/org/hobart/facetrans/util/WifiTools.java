package org.hobart.facetrans.util;

import java.util.Random;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class WifiTools {

    /**
     * 产生随机数8位密码
     */
    public static String getRandomPwd() {
        String pwd = "";
        Random rd = new Random();
        int num;
        do {
            num = Math.abs(rd.nextInt()) % 10 + 48;//产生数字0-9的随机数
            char numChar = (char) num;
            String strNum = Character.toString(numChar);
            pwd += strNum;
        } while (pwd.length() < 8);

        return pwd;
    }
}
