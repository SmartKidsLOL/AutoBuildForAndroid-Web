package com.wjr.auto_build.web.servlet;

import com.google.gson.Gson;
import com.wjr.auto_build.domain.Mission;
import com.wjr.auto_build.service.CreateMissionService;
import com.wjr.auto_build.utils.FileUtils;
import com.wjr.auto_build.utils.GsonUtils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 王金瑞
 * 2018/12/7 0007
 * 14:32
 * ${PACKAGE_NAME}
 */
@WebServlet(name = "CreateMissionServlet", urlPatterns = "/createMission")
public class CreateMissionServlet extends HttpServlet {
    private Gson mGson = GsonUtils.getInstance().getGson();

    @Override
    public void init() throws ServletException {
        // 进行文件检索创建
        // 工厂缓存路径
        try {
            FileUtils.createWorkDir();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("创建工作区失败：" + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CreateMissionService service = new CreateMissionService();
        Mission mission = service.getMission(request);
        String missionJson = mGson.toJson(mission);

        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(missionJson);
    }
}
