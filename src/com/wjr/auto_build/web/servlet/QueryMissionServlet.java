package com.wjr.auto_build.web.servlet;

import com.wjr.auto_build.constance.VarConstants;
import com.wjr.auto_build.domain.Mission;
import com.wjr.auto_build.utils.CoreUtils;

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
@WebServlet(name = "QueryMissionServlet", urlPatterns = "/queryMission")
public class QueryMissionServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String missionId = request.getParameter("missionId");
        int status = VarConstants.MISSION_STATUS_FAIL;
        if (CoreUtils.getMissionMap().containsKey(missionId)) {
            Mission mission = CoreUtils.getMissionMap().get(missionId);
            if (mission != null) {
                status = mission.getMissionStatus();
            }
        }

        System.out.println("\n任务：" + missionId + "：查询结果：" + status + "\n");

        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(String.valueOf(status));
    }
}
