package com.javaweb.servlet;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "getHandler", value = "/get-request")
public class GetHandler extends HttpServlet {

    public void init() {
        System.out.println("--------------------------------------------");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        printRequestInfo(req);
        response(resp);
        System.out.println("--------------------------------------------");
    }

    private void printRequestInfo(HttpServletRequest req) throws UnsupportedEncodingException {
        req.setCharacterEncoding("utf-8");
        // 请求信息
        // https://blog.csdn.net/qq_34666857/article/details/104677407?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522164867561216782092936924%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=164867561216782092936924&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-104677407.142^v5^pc_search_result_control_group,143^v6^control&utm_term=HttpServletRequest&spm=1018.2226.3001.4187
        System.out.println("\n<--------------- 一、请求信息 --------------->");
        System.out.println("请求方法：" + req.getMethod());
        System.out.println("请求协议：" + req.getProtocol());

        System.out.println("请求的工程路径：" + req.getRequestURI());
        System.out.println("请求的绝对路径：" + req.getRequestURL());
        System.out.println("请求的工程路径：" + req.getContextPath());
        System.out.println("请求的资源路径：" + req.getServletPath());
        System.out.println("日期：" + req.getDateHeader("Date"));

        System.out.println("\n<--------------- 二、用户信息 --------------->");
        System.out.println("请求头user-agent：" + req.getHeader("user-agent"));
        System.out.println("请求头referer：" + req.getHeader("referer"));
        System.out.println("请求头cookie：" + req.getHeader("cookie"));

        System.out.println(req.getScheme());
        System.out.println("用户端：" + req.getRemoteUser());
        System.out.println("用户端address：" + req.getRemoteAddr());
        System.out.println("用户端IP：" + req.getRemoteHost());
        System.out.println("用户端Port：" + req.getRemotePort());
        System.out.println("Cookies：" + req.getCookies());
        System.out.println("Session：" + req.getSession());

        System.out.println("\n<--------------- 三、服务器信息 --------------->");
        System.out.println("本地地址：" + req.getLocalAddr());
        System.out.println("本地名：" + req.getLocalName());
        System.out.println("本地端口：" + req.getLocalPort());

        System.out.println("服务器名：" + req.getServerName());
        System.out.println("服务器端口：" + req.getServerPort());

        System.out.println("\n<--------------- 四、POST内容 --------------->");
        System.out.println("请求参数：" + req.getQueryString());     // url ?后面的所有参数，常用于get请求

        System.out.println("参数：" + req.getParameter("username"));   //input框的name，id是用来和label配合使用的
        System.out.println("参数：" + req.getParameter("password"));
//        System.out.println("参数：" + req.getParameterValues(""));   //多选框

    }

    private void response(HttpServletResponse resp) throws IOException {
        //解决中文乱码问题
        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(200);

        // reponse a hello
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + "顶你个肺" + "</h1>");
        out.println("</body></html>");
    }

}