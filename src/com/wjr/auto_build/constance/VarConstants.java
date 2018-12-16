package com.wjr.auto_build.constance;

/**
 * Created by 王金瑞
 * 2018/12/9
 * 19:04
 * com.wjr.auto_build.utils
 * 项目中使用到的普通常量
 */
public class VarConstants {
    /**
     * 在Java GUI做的一些自定义配置
     */
    // 打包方式
    public static final String BUILD_KEY_PAK = "build_pak_type";
    // 签名文件名字KEY
    public static final String BUILD_KEY_SIGN_FILE_NAME = "sign_file_name";

    // 项目缓冲区文件大小：默认100MB
    public static final int DEFAULT_BUFFER_SIZE = 1024 * 1024 * 100;

    // 普通包
    public static final int BUILD_PAK_RELEASE = 0;
    // Tinker包
    public static final int BUILD_PAK_TINKER = 1;

    // android端与java端统一分割包名的字符串
    public static final String PACK_SPLIT_REGEX = "-AutoBuild-";

    /**
     * 任务相关
     */
    // 成功
    public static final int MISSION_STATUS_SUCCESS = 0;
    // 等待
    public static final int MISSION_STATUS_WAIT = 1;
    // 失败
    public static final int MISSION_STATUS_FAIL = 2;
}
