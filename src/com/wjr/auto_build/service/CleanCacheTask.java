package com.wjr.auto_build.service;

import com.wjr.auto_build.utils.CoreUtils;
import com.wjr.auto_build.utils.FileUtils;

import java.io.IOException;
import java.util.TimerTask;

/**
 * Created by 王金瑞
 * 2018/12/12 0012
 * 15:10
 * com.wjr.auto_build.service
 * 用来执行清除缓存操作的定时任务
 */
public class CleanCacheTask extends TimerTask {

    /**
     * 清除文件及缓存
     */
    @Override
    public void run() {
        try {
            System.out.println("\n定时任务清除工作开始");
            FileUtils.delAppAllCache();
            CoreUtils.getMissionMap().clear();
            System.out.println("\n定时任务清除工作完成");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("\n定时任务清除工作异常终止：" + e.getMessage());
        } finally {
            try {
                FileUtils.createWorkDir();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("创建工作区失败：" + e.getMessage());
            }
        }
    }

}
