package com.wjr.auto_build.web.servlet;

import com.wjr.auto_build.constance.FileConstants;
import com.wjr.auto_build.domain.Mission;
import com.wjr.auto_build.utils.CoreUtils;
import com.wjr.auto_build.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Encoder;

/**
 * Created by 王金瑞
 * 2018/12/7 0007
 * 14:32
 * ${PACKAGE_NAME}
 */
@WebServlet(name = "DownLoadServlet", urlPatterns = "/downLoadPak")
public class DownLoadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String missionId = request.getParameter("missionId");
        System.out.println("\n任务：" + missionId + "：开始下载\n");

        Mission mission = CoreUtils.getMissionMap().get(missionId);
        if (mission != null) {
            try {
                String fileName = mission.getDownloadName();
                String filePath = FileConstants.PRO_DOWN_LOAD_PATH + File.separator + fileName;

                // 获得请求头里的浏览器内核数据，对文件名重新编码
                String agent = request.getHeader("User-Agent");
                String fileNameEncode;
                if (agent.contains("MSIE")) {
                    // IE
                    fileNameEncode = URLEncoder.encode(fileName, "utf-8");
                    fileNameEncode = fileNameEncode.replace("+", " ");
                } else if (agent.contains("Firefox")) {
                    // 火狐
                    BASE64Encoder base64Encoder = new BASE64Encoder();
                    fileNameEncode = "=?utf-8?B?" + base64Encoder.encode(fileName.getBytes("utf-8")) + "?=";
                } else {
                    // 其他浏览器
                    fileNameEncode = URLEncoder.encode(fileName, "utf-8");
                }

                // 设置MIME类型
                response.setContentType("application/zip");
                // 设置以附件形式打开，并且文件名针对不同平台不同提供解码后的名称
                response.setHeader("Content-Disposition", "attachment; filename=" + fileNameEncode);

                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
                BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());

                FileUtils.copyLen(bis, bos);

                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
                writeErrorData(response);
            }
        } else {
            writeErrorData(response);
        }

    }

    private void writeErrorData(HttpServletResponse response) throws IOException {
        System.out.println("\n任务下载失败，请查看详细信息");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write("下载任务失败了。。。请查看Log中的错误哦");
    }
}
