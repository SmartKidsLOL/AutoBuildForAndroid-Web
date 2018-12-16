package com.wjr.auto_build.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by 王金瑞
 * 2018/12/6 0006
 * 15:04
 * com.wjr.andToZip.utils
 */
public class ZipUtils {
    private int sourceFileLen;
    private int zippingLen;

    private IZippingListener mListener;

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

    public void setListener(IZippingListener listener) {
        mListener = listener;
    }

    public static ZipUtils getInstance() {
        return ZipUtilsHolder.UTILS;
    }

    private static class ZipUtilsHolder {
        private static final ZipUtils UTILS = new ZipUtils();
    }

    // 递归计算文件数量
    public void calculateFilesLen(File file) throws IOException {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();

            for (File fi : listFiles) {
                if (fi.isDirectory()) {
                    calculateFilesLen(fi);
                } else {
                    sourceFileLen++;
                }
            }
        } else {
            sourceFileLen++;
        }
    }

    // 开始压缩文件
    public String startZipping(File sourceFile, String targetPath) throws IOException {
        sourceFileLen = 0;
        zippingLen = 0;

        String zipFileName = sourceFile.getName() + "-" + "final_release" + ".zip";
        String zipFilePath = targetPath + File.separator + zipFileName;

        File targetFile = new File(targetPath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        // 先计算所有文件数量
        calculateFilesLen(sourceFile);

        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFilePath)));
        compressZip(zos, sourceFile, sourceFile.getName());
        zos.closeEntry();
        zos.close();

        return zipFileName;
    }

    // 递归
    public void compressZip(ZipOutputStream zos, File file, String zipFileName) throws IOException {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();

            for (File fi : listFiles) {
                if (fi.isDirectory()) {
                    compressZip(zos, fi, zipFileName + File.separator + fi.getName());
                } else {
                    zipping(zos, fi, zipFileName);
                }
            }
        } else {
            zipping(zos, file, zipFileName);
        }
    }

    // 压缩具体操作
    public void zipping(ZipOutputStream zos, File file, String zipFileName) throws IOException {
        ZipEntry entry = new ZipEntry(zipFileName + File.separator + file.getName());
        zos.putNextEntry(entry);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        FileUtils.copyLen(bis, zos);
        bis.close();

        zippingLen++;
        int progress = (int) ((zippingLen * 1.0 / sourceFileLen) * 100);
        if (mListener != null) {
            mListener.updateProgress(progress);
        }
    }

    // 解压文件
    public void unZipping(String zipPath, String destPath) throws IOException {
        File zipFile = new File(zipPath);
        if (!zipFile.exists() || StringUtils.isEmptys(zipPath) || StringUtils.isEmptys(destPath)) {
            throw new IOException("目标文件不存在：" + zipPath + "---" + destPath);
        }

        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));

        File destFile;
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null && !entry.isDirectory()) {
            destFile = new File(destPath, entry.getName());
            if (!destFile.exists()) {
                (new File(destFile.getParent())).mkdirs();
            }

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));

            int b;
            byte[] buffer = new byte[1024];
            while ((b = zis.read(buffer)) != -1) {
                bos.write(buffer, 0, b);
            }
            bos.close();
        }

        zis.close();
    }
}
