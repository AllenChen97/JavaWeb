package com.javaweb.分发和重定向;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class Redirecter extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");

        //方法一：告知302 并提供新地址
//        resp.setStatus(resp.SC_MOVED_TEMPORARILY);
//        resp.setHeader("Location", "http://localhost:8080/Servlet/worker");

        //方法二(推荐)：req.sendRedirect
        resp.sendRedirect("/Servlet/worker");

    }

}