package com.ouj.bolo;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/11/30.
 */

public class Utils {

    public static String formatTime(int time) {
        String pattern;
        if (time >= 3600000) {
            pattern = "hh:mm:ss";
        } else {
            pattern = "mm:ss";
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(time);
    }
}
