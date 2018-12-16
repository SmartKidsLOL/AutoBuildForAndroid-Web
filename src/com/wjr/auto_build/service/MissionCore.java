package com.wjr.auto_build.service;

import com.wjr.auto_build.constance.CMDConstants;
import com.wjr.auto_build.constance.CustomConstants;
import com.wjr.auto_build.constance.FileConstants;
import com.wjr.auto_build.constance.VarConstants;
import com.wjr.auto_build.domain.Mission;
import com.wjr.auto_build.utils.CoreUtils;
import com.wjr.auto_build.utils.FileUtils;
import com.wjr.auto_build.utils.IZippingListener;
import com.wjr.auto_build.utils.StringUtils;
import com.wjr.auto_build.utils.ZipUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by 王金瑞
 * 2018/12/10 0010
 * 9:32
 * com.wjr.auto_build.service
 */
public class MissionCore implements Runnable {
    private Mission mMission;
    // md5路径
    private String mMissionMD5Path;
    // 项目真实目录名
    private String mRealName;
    // 源码根目录路径
    private String mProRootPath;
    // 不同格式打出包的不同路径
    private String mPackSourcePath;
    // apk所在目录：
    private String mReleaseApkDir;

    private int mBuildPackType;
    private String mSignFileName;

    public MissionCore(Mission mission) {
        mMission = mission;
    }

    @Override
    public void run() {
        try {
            // 1.解压文件
            zipFile();
            // 2.读取并写入配置文件
            readAndWriteProperties();
            // 3.进行cmd打包
            cmdReleasePack();
            // 4.进行复制，将文件复制到pack目录
            copyReleaseToPack();
            // 5.进行360加固打包
            cmd360JiaGuPack();
            // 6.将目录打包
            zipPackToDownLoad();
            // 7.结尾操作
            finishMission();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                // 将错误写入Log文件中
                String logPath = FileConstants.PRO_LOG_PATH + File.separator + mMissionMD5Path;
                File file = FileUtils.fileExist(logPath);
                File logFile = new File(file, FileConstants.LOG_CATCH_FILE_NAME);
                BufferedReader br = new BufferedReader(new StringReader(e.getMessage()));
                BufferedWriter bw = new BufferedWriter(new FileWriter(logFile));
                FileUtils.writeContent(br, bw, false);

            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("\n文件读写错误，请检查是否正在有其他程序读写!\n");
            } finally {
                mMission.setMissionStatus(VarConstants.MISSION_STATUS_FAIL);
                CoreUtils.getMissionMap().put(mMission.getMissionId(), mMission);
            }
        }
    }

    private void execCmdShell(String command, String missionName) throws Exception {
        System.out.println("\n任务：" + mMission.getZipName() + "开始执行" + missionName + "：" + command + "\n");

        Process process = Runtime.getRuntime().exec(command);
        // 写入Log
        String logPath = FileConstants.PRO_LOG_PATH + File.separator + mMissionMD5Path;
        File logFile = FileUtils.fileExist(logPath);
        // 写入build信息
        BufferedReader buildLogBr = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter buildLogBw = new BufferedWriter(new FileWriter(new File(logFile, FileConstants.LOG_BUILD_FILE_NAME), true));
        FileUtils.writeAsyncContent(buildLogBr, buildLogBw, true);
        // 写入error信息
        BufferedReader errorLogBr = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
        BufferedWriter errorLogBw = new BufferedWriter(new FileWriter(new File(logFile, FileConstants.LOG_ERROR_FILE_NAME)));
        FileUtils.writeAsyncContent(errorLogBr, errorLogBw, true);

        // 阻塞直至完成
        int result = process.waitFor();
        if (result != 0) {
            // 说明没有执行成功
            throw new Exception("任务：" + mMission.getZipName() + "服务端执行CMD" + missionName + "命令失败，请前往Log中查看具体错误！");
        }

        process.destroy();
    }

    /**
     * 7.完成所有操作，置为完成
     */
    private void finishMission() {
        mMission.setMissionStatus(VarConstants.MISSION_STATUS_SUCCESS);
        CoreUtils.getMissionMap().put(mMission.getMissionId(), mMission);
        System.out.println("任务：" + mMission.getMissionId() + "已完成，请耐心等待浏览器进行查询下载");
    }

    /**
     * 6.将所有渠道包 + 原包打包至下载目录
     */
    private void zipPackToDownLoad() throws Exception {
        File sourceFile = new File(FileConstants.PRO_PACK_PATH + File.separator + mMissionMD5Path);
        File targetFile = FileUtils.fileExist(FileConstants.PRO_DOWN_LOAD_PATH);

        ZipUtils.getInstance().setListener(new IZippingListener() {
            @Override
            public void updateProgress(int progress) {
                // 压缩进度
                System.out.println("\n任务：" + mMission.getMissionId() + "：正在压缩中：" + progress + "%");
            }
        });
        String dlName = ZipUtils.getInstance().startZipping(sourceFile, targetFile.getAbsolutePath());
        mMission.setDownloadName(dlName);
    }

    /**
     * 5.将打出来的release包进行加固
     */
    private void cmd360JiaGuPack() throws Exception {
        // 先找到release-apk路径
        List<File> tempList = new ArrayList<>();
        FileUtils.findFiles(new File(mReleaseApkDir), CustomConstants.APP_APK_NAME, tempList);
        if (tempList.size() == 0) {
            throw new Exception("未找到apk文件！请检查是否编译成功！");
        }
        File sourceApkFile = tempList.get(0);
        String targetPackPath = FileConstants.PRO_PACK_PATH + File.separator + mMissionMD5Path;
        // 进行cmd加固
        String jiaguCommand = CMDConstants.CMD_360JIAGU
                + sourceApkFile.getAbsolutePath()
                + " "
                + targetPackPath
                + CMDConstants.CMD_360JIAGU_END;
        execCmdShell(jiaguCommand, "加固脚本");
    }

    /**
     * 4. 将打包后的release目录复制到pack打包目录
     */
    private void copyReleaseToPack() throws Exception {
        File sourceApkFile = new File(mProRootPath + mPackSourcePath);
        String[] sources = mPackSourcePath.split("\\\\");
        String dirName = sources[sources.length - 1];
        mReleaseApkDir = FileConstants.PRO_PACK_PATH + File.separator + mMissionMD5Path + File.separator + dirName;
        File targetFile = FileUtils.fileExist(mReleaseApkDir);
        org.apache.commons.io.FileUtils.copyDirectory(sourceApkFile, targetFile);
    }

    /**
     * 3.调用CMD命令进行clean打包
     */
    private void cmdReleasePack() throws Exception {
        // 如果是打Tinker包，则先删除与Andorid端约定好的Tinker生成包
        File file = new File(mProRootPath + CustomConstants.PACK_TINKER_PATH);
        if (file.exists() && file.listFiles() != null && file.listFiles().length > 0) {
            FileUtils.delDir(file);
        }

        // 调用命令进行打包
        // 1.进入指定目录
        // 2.执行gradle clean
        // 3.执行release
        String command = CMDConstants.CMD_ENTER_DIR
                + mProRootPath
                + CMDConstants.CMD_CONCAT_SYMBOL
                + CMDConstants.CMD_GRADLE_FINAL;
        execCmdShell(command, "打包脚本");
    }

    /**
     * 2.读取并写入与Android端约定的配置文件
     */
    private void readAndWriteProperties() throws Exception {
        Properties properties = new Properties();
        String propertiesPath = mProRootPath + File.separator + FileConstants.ANDROID_AUTO_BUILD_FILENAME;
        properties.load(new BufferedReader(new FileReader(propertiesPath)));
        mBuildPackType = Integer.parseInt(properties.getProperty(VarConstants.BUILD_KEY_PAK));
        mSignFileName = properties.getProperty(VarConstants.BUILD_KEY_SIGN_FILE_NAME);
        if ((mBuildPackType != VarConstants.BUILD_PAK_RELEASE && mBuildPackType != VarConstants.BUILD_PAK_TINKER)
                || StringUtils.isEmptys(mSignFileName)) {
            throw new Exception("配置文件出错：" + mBuildPackType + "---" + mSignFileName);
        }
        properties.clear();

        // 配置打包后的路径
        switch (mBuildPackType) {
            case VarConstants.BUILD_PAK_RELEASE:
                mPackSourcePath = CustomConstants.PACK_REALEASE_PATH;
                break;
            case VarConstants.BUILD_PAK_TINKER:
                mPackSourcePath = CustomConstants.PACK_TINKER_PATH;
                break;
            default:
                throw new Exception("未找到适配与此app打包风格！");
        }

        // 配置签名文件
        File signPropFile = new File(mProRootPath, FileConstants.ANDROID_SIGN_FILENAME);
        if (signPropFile.exists()) {
            signPropFile.delete();
        }

        String signContent;
        switch (mSignFileName) {
            case CustomConstants.TEST1_KEY:
                signContent = CustomConstants.ANDROID_SIGN_TEST2_CONFIG;
                break;
            case CustomConstants.TEST2_KEY:
                signContent = CustomConstants.ANDROID_SIGN_TEST1_CONFIG;
                break;
            default:
                throw new Exception("未找到与此app适配的签名文件！");
        }

        // 进行重写
        BufferedReader br = new BufferedReader(new StringReader(signContent));
        BufferedWriter bw = new BufferedWriter(new FileWriter(signPropFile));
        FileUtils.writeContent(br, bw, false);

        // 重新写入AndroidSDK目录
        File localPropFile = new File(mProRootPath, "local.properties");
        if (localPropFile.exists()) {
            localPropFile.delete();
        }

        // 进行重写
        BufferedReader lpBr = new BufferedReader(new StringReader(CustomConstants.ANDROID_SDK_DIR));
        BufferedWriter lpBw = new BufferedWriter(new FileWriter(localPropFile));
        FileUtils.writeContent(lpBr, lpBw, false);
    }

    /**
     * 1.解压zip至源码目录
     */
    private void zipFile() throws Exception {
        mMissionMD5Path = StringUtils.getMissionFileName(mMission.getZipName());
        mRealName = StringUtils.getMissionRealName(mMissionMD5Path);
        String zipPath = FileConstants.PRO_BUFFER_PATH + File.separator + mMission.getZipName();
        String destPath = FileConstants.PRO_SOURCE_PATH + File.separator + mMissionMD5Path;
        ZipUtils.getInstance().unZipping(zipPath, destPath);
        mProRootPath = FileConstants.PRO_SOURCE_PATH + File.separator + mMissionMD5Path + File.separator + mRealName;
    }
}
