package com.wjr.auto_build.constance;

import java.io.File;

/**
 * Created by 王金瑞
 * 2018/12/9
 * 19:05
 * com.wjr.auto_build.utils
 * 项目配置目录常量
 */
public class FileConstants {

    // Android文件中自定义的配置文件名
    public static final String ANDROID_SIGN_FILENAME = "keyStore.properties";
    public static final String ANDROID_AUTO_BUILD_FILENAME = "AndroidToZip.properties";

    // DiskFileItemFactory的缓存区
    private static final String PRO_FACTORY_PATH_NAME = "factory_buffer";
    // 上传文件后放置的缓存区目录文件名
    private static final String PRO_BUFFER_PATH_NAME = "buffer";
    // 存放所有解压后的根目录文件名：
    private static final String PRO_SOURCE_PATH_NAME = "source";
    // 存放Release包和签名后的包的目录文件名：
    private static final String PRO_PACK_PATH_NAME = "packs";
    // 任务完成后打包至下载目录文件名：
    private static final String PRO_DOWN_LOAD_PATH_NAME = "downLoad";
    // 事件log目录文件名
    private static final String PRO_LOG_PATH_NAME = "log";
    // cmd命令正常输出流log文件名
    public static final String LOG_BUILD_FILE_NAME = "buildLog.txt";
    // cmd命令错误输出流log文件名
    public static final String LOG_ERROR_FILE_NAME = "errorLog.txt";
    // 抛出异常后错误的log
    public static final String LOG_CATCH_FILE_NAME = "catchLog.txt";

    /**
     * 自动化构建目录路径
     */
    // DiskFileItemFactory的缓存区
    public static final String PRO_FACTORY_PATH = CustomConstants.AUTO_BUILD_ROOT_PATH + File.separator + PRO_FACTORY_PATH_NAME;
    // 上传文件后的缓存区
    public static final String PRO_BUFFER_PATH = CustomConstants.AUTO_BUILD_ROOT_PATH + File.separator + PRO_BUFFER_PATH_NAME;
    // 存放所有解压后的项目目录
    public static final String PRO_SOURCE_PATH = CustomConstants.AUTO_BUILD_ROOT_PATH + File.separator + PRO_SOURCE_PATH_NAME;
    // 将Release包和签名包要放至的目录
    public static final String PRO_PACK_PATH = CustomConstants.AUTO_BUILD_ROOT_PATH + File.separator + PRO_PACK_PATH_NAME;
    // 任务完成后打包至下载目录
    public static final String PRO_DOWN_LOAD_PATH = CustomConstants.AUTO_BUILD_ROOT_PATH + File.separator + PRO_DOWN_LOAD_PATH_NAME;
    // log目录
    public static final String PRO_LOG_PATH = CustomConstants.AUTO_BUILD_ROOT_PATH + File.separator + PRO_LOG_PATH_NAME;

}
