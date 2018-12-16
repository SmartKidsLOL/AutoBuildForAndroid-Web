package com.wjr.auto_build.utils;

import com.wjr.auto_build.constance.VarConstants;

/**
 * Created by WangJinRui on 2018/3/13.
 */

public class StringUtils {

    public static boolean isEmptys(String... strs) {

        for (String str : strs) {
            if (str == null || str.trim().equals("")) {
                return true;
            }
        }

        return false;

    }

    // 去除.zip
    public static String getMissionFileName(String zipName) throws Exception {
        return zipName.substring(0, zipName.length() - 4);
    }

    // 获得项目真实名字
    public static String getMissionRealName(String zipName) throws Exception {
        String[] strs = zipName.split(VarConstants.PACK_SPLIT_REGEX);
        return strs[0];
    }

}
