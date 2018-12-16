package com.wjr.auto_build.service;

import com.wjr.auto_build.constance.FileConstants;
import com.wjr.auto_build.constance.VarConstants;
import com.wjr.auto_build.domain.Mission;
import com.wjr.auto_build.utils.CoreUtils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 王金瑞
 * 2018/12/7 0007
 * 16:56
 * com.wjr.auto_build.service
 */
public class CreateMissionService {

    public Mission getMission(HttpServletRequest request) {
        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(VarConstants.DEFAULT_BUFFER_SIZE);

            factory.setRepository(new File(FileConstants.PRO_FACTORY_PATH));
            ServletFileUpload upload = new ServletFileUpload(factory);
            //设置上传文件的名称的编码
            upload.setHeaderEncoding("UTF-8");

            //判断表单是否是文件上传的表单
            boolean multipartContent = ServletFileUpload.isMultipartContent(request);
            if (multipartContent) {
                //是文件上传的表单
                //***解析request获得文件项集合
                List<FileItem> parseRequest = upload.parseRequest(request);
                if (parseRequest != null && parseRequest.size() == 1) {
                    // 只取第一个
                    FileItem item = parseRequest.get(0);
                    //判断是不是一个普通表单项
                    boolean formField = item.isFormField();
                    if (!formField) {
                        Mission mission = CoreUtils.getMission(item);
                        if (!mission.isExist()) {
                            // 线程池开启任务
                            mission.setExist(true);
                            CoreUtils.getMissionMap().put(mission.getMissionId(), mission);
                            CoreUtils.getThreadPools().submit(new MissionCore(mission));
                            System.out.println("开启线程!!!!!!!!!!!!!!!!");
                        }
                        return CoreUtils.getMission(item);
                    } else {
                        throw new Exception("此请求必须上传文件！");
                    }

                } else {
                    throw new Exception("未检测到上传文件！");
                }

            } else {
                throw new Exception("此请求必须上传文件！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("任务开启失败：" + e.getMessage());
        }
        return new Mission("null", "null", 2);
    }
}
