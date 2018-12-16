package com.wjr.auto_build.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by 王金瑞
 * 2018/2/21
 * 16:10
 * ${PACKAGE_NAME}
 */
@WebFilter(filterName = "Filter0_Encoding", urlPatterns = "/*")
public class Filter0_Encoding implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 转型为与协议相关对象
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        // 对request包装增强
        HttpServletRequest myrequest = new MyRequest(httpServletRequest);
        // 将response返回编码设置utf-8
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setHeader("Content-type", "text/html;charset=UTF-8");
        chain.doFilter(myrequest, httpServletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

}

