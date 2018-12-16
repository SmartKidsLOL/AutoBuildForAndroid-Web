package com.wjr.auto_build.utils;

import java.util.Calendar;

/**
 * Created by 王金瑞
 * 2018/12/12 0012
 * 15:44
 * com.wjr.auto_build.utils
 */
public class TimeUtils {

    /**
     * 获取当前时间至明天凌晨的时间
     */
    public static long getCurrentToTomorrowTime() {
        long currTime = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis() - currTime;
    }

}
