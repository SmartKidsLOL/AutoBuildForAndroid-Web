package com.wjr.auto_build.utils;

import com.wjr.auto_build.constance.CustomConstants;
import com.wjr.auto_build.constance.FileConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by 王金瑞
 * 2018/12/9
 * 22:17
 * com.wjr.auto_build.utils
 */
public class FileUtils {

    public static void copyFile(InputStream in, OutputStream os) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(in);
        BufferedOutputStream bos = new BufferedOutputStream(os);

        copyLen(bis, bos);

        bis.close();
        bos.close();
    }

    public static void copyLen(BufferedInputStream bis, OutputStream bos) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bis.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
            bos.flush();
        }
    }

    // 递归删除
    public synchronized static void delDir(File file) throws IOException {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            for (File subFile : subFiles) {
                delDir(subFile);
            }
        }
        file.delete();// 删除空目录或文件
    }

    // 清除整个应用的缓存
    public synchronized static void delAppAllCache() throws IOException {
        File appFile = new File(CustomConstants.AUTO_BUILD_ROOT_PATH);
        if (appFile.exists()) {
            delDir(appFile);
        }
    }

    // 创建工作区文件
    public synchronized static void createWorkDir() throws IOException {
        File factoryFile = new File(FileConstants.PRO_FACTORY_PATH);
        fileExist(factoryFile);

        File bufferFile = new File(FileConstants.PRO_BUFFER_PATH);
        fileExist(bufferFile);

        File sourceFile = new File(FileConstants.PRO_SOURCE_PATH);
        fileExist(sourceFile);

        File backFile = new File(FileConstants.PRO_PACK_PATH);
        fileExist(backFile);

        File dowLoadFile = new File(FileConstants.PRO_DOWN_LOAD_PATH);
        fileExist(dowLoadFile);

        File logFile = new File(FileConstants.PRO_LOG_PATH);
        fileExist(logFile);
    }

    // 清除所有缓存目录下的文件
    public synchronized static void delProAllCache(String proName) throws IOException {
        File bufferFile = new File(FileConstants.PRO_BUFFER_PATH, proName);
        if (bufferFile.exists()) {
            delDir(bufferFile);
        }

        File sourceFile = new File(FileConstants.PRO_SOURCE_PATH, proName);
        if (sourceFile.exists()) {
            delDir(sourceFile);
        }

        File backFile = new File(FileConstants.PRO_PACK_PATH, proName);
        if (backFile.exists()) {
            delDir(backFile);
        }

        File dowLoadFile = new File(FileConstants.PRO_DOWN_LOAD_PATH, proName);
        if (dowLoadFile.exists()) {
            delDir(dowLoadFile);
        }

        File logFile = new File(FileConstants.PRO_LOG_PATH, proName);
        if (logFile.exists()) {
            delDir(logFile);
        }
    }

    // 将内容写入指定文件
    public static void writeContent(BufferedReader br, BufferedWriter bw, boolean isShowConsole) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (isShowConsole) {
                System.out.println(line);
            }
            bw.write(line);
            bw.newLine();
            bw.flush();
        }
        br.close();
        bw.close();
    }

    // 异步将内容写入指定文件
    public static void writeAsyncContent(BufferedReader br, BufferedWriter bw, boolean isShowConsole) throws IOException {
        CoreUtils.getThreadPools().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    writeContent(br, bw, isShowConsole);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 创建文件
    public static File fileExist(File file) throws IOException {
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File fileExist(String path) throws IOException {
        return fileExist(new File(path));
    }

    // 递归查找指定单个文件
    public static List<File> findFiles(File filePath, String fileName, List<File> filePaths) {
        File[] files = filePath.listFiles();
        if (files == null) {
            return filePaths;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                findFiles(f, fileName, filePaths);
            } else {
                if (f.getName().equals(fileName)) {
                    filePaths.add(f);
                }
            }
        }
        return filePaths;
    }
}
