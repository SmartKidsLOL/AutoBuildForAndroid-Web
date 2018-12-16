package com.wjr.auto_build.utils;

import com.wjr.auto_build.constance.FileConstants;
import com.wjr.auto_build.constance.VarConstants;
import com.wjr.auto_build.domain.Mission;

import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 王金瑞
 * 2018/12/7 0007
 * 16:57
 * com.wjr.auto_build.utils
 */
public class CoreUtils {
    // 维护一个缓存线程池
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    // 维护一个线程安全的HashMap
    private static final ConcurrentHashMap<String, Mission> SAFE_HASHMAP = new ConcurrentHashMap<>();

    public static ExecutorService getThreadPools() {
        return THREAD_POOL;
    }

    public static ConcurrentHashMap<String, Mission> getMissionMap() {
        return SAFE_HASHMAP;
    }

    // 同步锁获取任务
    public synchronized static Mission getMission(FileItem item) throws IOException {
        //文件上传项
        String zipName = item.getName();
        String md5Key = MD5Utils.encrypt16(zipName);
        boolean containsKey = CoreUtils.getMissionMap().containsKey(md5Key);
        if (containsKey) {
            // 如果包含，则返回值
            Mission missionValue = CoreUtils.getMissionMap().get(md5Key);
            int status = missionValue.getMissionStatus();
            if (status == VarConstants.MISSION_STATUS_SUCCESS ||
                    status == VarConstants.MISSION_STATUS_WAIT) {
                // 直接返回
                System.out.println("\n任务：" + missionValue.getMissionId() + "已经存在，请耐心等待完成\n");
                return missionValue;
            } else {
                // 任务已经失败，但用户依然上传，则清除任务，重新添加
                clearMission(md5Key, item.getName());
                return createMission(md5Key, item);
            }
        } else {
            return createMission(md5Key, item);
        }
    }

    /**
     * 清除项目目录文件
     */
    public synchronized static void clearMission(String key, String fileName) {
        // 清除各文件夹及Map的key
        try {
            FileUtils.delProAllCache(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("\n删除缓存文件失败：请检查文件是否被占用!");
            try {
                FileUtils.delProAllCache(fileName);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("\n删除缓存文件失败：请检查文件是否被占用!");
            }
        } finally {
            CoreUtils.getMissionMap().remove(key, CoreUtils.getMissionMap().get(key));
        }
    }

    /**
     * 创建任务
     */
    public synchronized static Mission createMission(String md5Key, FileItem item) throws IOException {
        InputStream in = item.getInputStream();
        FileOutputStream fos = new FileOutputStream(FileConstants.PRO_BUFFER_PATH + File.separator + item.getName());
        FileUtils.copyFile(in, fos);

        Mission mission = new Mission();
        mission.setMissionId(md5Key);
        mission.setZipName(item.getName());
        mission.setMissionStatus(VarConstants.MISSION_STATUS_WAIT);

        CoreUtils.getMissionMap().put(md5Key, mission);

        //删除临时文件
        item.delete();

        System.out.println("\n创建任务成功：" + mission.toString() + "\n");

        return mission;
    }
}
