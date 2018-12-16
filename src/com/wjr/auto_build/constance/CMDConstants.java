package com.wjr.auto_build.constance;


/**
 * Created by 王金瑞
 * 2018/12/9
 * 19:48
 * com.wjr.auto_build.constance
 * 项目中用到的CMD命令常量
 */
public class CMDConstants {
    public static final String CMD_CONCAT_SYMBOL = " && ";
    private static final String CMD_START_DIR = "cmd.exe /c ";
    /**
     * Gradle命令配置
     */
    // clean命令
    public static final String CMD_GRADLE_CLEAN = "gradle clean";
    // 打包命令
    public static final String CMD_GRADLE_BUILD = "gradle assembleRelease";
    // 完整Gradle命令：clean + 打包
    public static final String CMD_GRADLE_FINAL = CMD_GRADLE_CLEAN + CMD_CONCAT_SYMBOL + CMD_GRADLE_BUILD;

    /**
     * 360加固命令配置
     */
    private static final String CMD_360_JIAGU = "java -jar jiagu.jar -jiagu ";

    // 进入到指定目录，执行多条命令
    public static final String CMD_ENTER_DIR = CMD_START_DIR
            + CustomConstants.CMD_DISC_D
            + CMD_CONCAT_SYMBOL
            + "cd ";

    // 进入到360目录，执行加固打渠道包，要加入apk目录和输出目录
    public static final String CMD_360JIAGU = CMD_START_DIR
            + CustomConstants.CMD_DISC_H
            + CMD_CONCAT_SYMBOL
            + "cd "
            + CustomConstants.JIAGU_360_PATH
            + CMD_CONCAT_SYMBOL
            + CMD_360_JIAGU;

    // 360加固末尾命令
    public static final String CMD_360JIAGU_END = " "
            + "-autosign"
            + " "
            + "-automulpkg";
}
