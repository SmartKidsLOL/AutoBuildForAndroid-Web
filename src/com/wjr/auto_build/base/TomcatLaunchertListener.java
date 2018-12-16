package com.wjr.auto_build.base;

import com.wjr.auto_build.constance.CustomConstants;
import com.wjr.auto_build.service.CleanCacheTask;
import com.wjr.auto_build.utils.CoreUtils;
import com.wjr.auto_build.utils.FileUtils;
import com.wjr.auto_build.utils.TimeUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;

/**
 * Created by 王金瑞
 * 2018/12/12 0012
 * 14:47
 * com.wjr.auto_build.base
 */
public class TomcatLaunchertListener implements javax.servlet.ServletContextListener {
    private Timer mTimer;

    /**
     * Tomcat启动时，启动定时器，每天凌晨固定进行一次清包路径
     * 定时任务开启时间默认为 间隔时间=明天凌晨 - 当前时间戳
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // 如果服务器有文件存在，则清除
        System.out.println("服务器启动了");
        try {
            FileUtils.delAppAllCache();

            // 开启定时任务，设置守护线程
            mTimer = new Timer(true);
            mTimer.schedule(new CleanCacheTask(), TimeUtils.getCurrentToTomorrowTime(), CustomConstants.DEFAULT_CLEAR_TASK_INTERVAL);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("服务器清除缓存失败，请检查是否有程序正在占用服务器路径！");
        }
    }

    /**
     * Tomcat销毁时，通知线程池关闭任务，清除所有路径内容
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("服务器销毁了");
        ExecutorService threadPools = CoreUtils.getThreadPools();
        try {
            // 取消定时任务
            mTimer.cancel();
            // 通知线程池
            threadPools.shutdown();

            // 一定时间内等待关闭
            if (!threadPools.awaitTermination(10, TimeUnit.SECONDS)) {
                threadPools.shutdownNow();
            }

            FileUtils.delAppAllCache();
            System.out.println("服务器清除缓存成功！");
        } catch (Exception e) {
            e.printStackTrace();
            // 保证被关闭
            threadPools.shutdownNow();
            System.out.println("服务器清除缓存失败，请检查是否有程序正在占用服务器路径！");
        }
    }
}
