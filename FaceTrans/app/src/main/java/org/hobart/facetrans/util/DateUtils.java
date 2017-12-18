package org.hobart.facetrans.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by huzeyin on 2017/11/30.
 */

public class DateUtils {


    public static String timestamp2Date(String str_num) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (str_num.length() == 13) {
            String date = sdf.format(new Date(toLong(str_num)));
            return date;
        } else {
            String date = sdf.format(new Date(toInt(str_num) * 1000L));
            return date;
        }
    }

    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    public static int toInt(String obj) {
        try {
            return Integer.parseInt(obj);
        } catch (Exception e) {
        }
        return 0;
    }
}
