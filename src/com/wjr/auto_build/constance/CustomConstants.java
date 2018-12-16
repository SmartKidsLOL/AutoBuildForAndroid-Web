package com.wjr.auto_build.constance;

import java.io.File;

/**
 * Created by 王金瑞
 * 2018/12/12 0012
 * 10:10
 * com.wjr.auto_build.constance
 * <p>
 * 此类为使用者必须重写的常量类，常量值必须为自己服务器的路径地址：
 */
public class CustomConstants {
    /**
     * 自己的项目和360加固包盘符路径
     */
    // 服务器文件存放的盘符
    public static final String CMD_DISC_D = "d:";
    // 360加固宝所在的盘符
    public static final String CMD_DISC_H = "d:";

    /**
     * 需要修改的服务器Base路径
     * 自动化构建根目录，与上面的{@link #CMD_DISC_D}盘符相对应
     */
    public static final String AUTO_BUILD_ROOT_PATH = "D:\\AutoBuild";
    /**
     * 360加固目录，与上面的{@link #CMD_DISC_H}盘符相对应
     * 例：public static final String JIAGU_360_PATH = "D:\\360JiaGu\\jiagu";
     */
    public static final String JIAGU_360_PATH = "";

    /**
     * 定时任务每隔多长时间清除一次服务端文件，默认24小时，如无其他需求，则不需要改
     */
    public static final long DEFAULT_CLEAR_TASK_INTERVAL = 1000 * 60 * 60 * 24L;

    /**
     * 普通Android常量自定义配置
     */
    // 修改为服务器端AndroidSDK目录路径
    // 例：private static final String ANDROID_SDK_DIR_STR = "D\\:\\\\Develop\\\\AndroidSDK";
    private static final String ANDROID_SDK_DIR_STR = "";
    public static final String ANDROID_SDK_DIR = "sdk.dir=" + ANDROID_SDK_DIR_STR;
    /**
     * Android不同构建包目录：
     * {@link #PACK_REALEASE_PATH}：如未自定义路径，则无需修改
     * {@link #PACK_TINKER_PATH}：需要自定义Tinker包路径，此路径是相对于Android Project目录下的!
     */
    // 普通release包目录：
    public static final String PACK_REALEASE_PATH = File.separator + "app\\build\\outputs\\apk\\release";
    // Tinker包目录：
    public static final String PACK_TINKER_PATH = File.separator + "infoBak";
    // 打出来的Apk名字，默认为app-release.apk，如修改过则改为自定义的Apk名
    public static final String APP_APK_NAME = "app-release.apk";


    /**
     * 对Android端的签名配置
     */
    // 签名文件1Name
    public static final String TEST1_KEY = "test1Key";
    // 签名文件2Name
    public static final String TEST2_KEY = "test2Key";


    /**
     * 签名文件配置String，注意要改为自己的签名路径和配置信息
     * 与上面的 {@link #TEST1_KEY} 和 {@link #TEST2_KEY}相对应
     */
    // test1签名配置信息
    public static final String ANDROID_SIGN_TEST1_CONFIG =
            "KEY_PATH=D\\:/Develop/Android_key/test1Key.jks\n" +
                    "KEY_PASS=test1\n" +
                    "ALIAS_NAME=key\n" +
                    "ALIAS_PASS=test1";

    // test2签名配置信息
    public static final String ANDROID_SIGN_TEST2_CONFIG =
            "KEY_PATH=D\\:/Develop/Android_key/test2Key.jks\n" +
                    "KEY_PASS=test2\n" +
                    "ALIAS_NAME=key\n" +
                    "ALIAS_PASS=test2";
}
