package com.wjr.auto_build.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by 王金瑞
 * 2018/12/9
 * 18:33
 * com.wjr.auto_build.utils
 */
public class GsonUtils {
    private Gson mGson;

    {
        mGson = new GsonBuilder()
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }

    private GsonUtils() {
    }

    public static GsonUtils getInstance() {
        return GsonHolder.GSON_UTILS;
    }

    private static class GsonHolder {
        private static final GsonUtils GSON_UTILS = new GsonUtils();
    }

    public Gson getGson() {
        return mGson;
    }

}
