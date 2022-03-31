package com.javaweb.servlet;


import com.google.gson.Gson;

import javax.servlet.http.*;
import java.io.IOException;

public class Json extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("收到请求，并开始处理");

        Gson gson = new Gson();
        Person person = new Person("1","小陈");
        String PersonString = gson.toJson(person);

        System.out.println("已处理，正在返回信息");
        response(resp, PersonString);
        System.out.println("请求已完成，返回：" + PersonString);
    }

    private void response(HttpServletResponse resp,String str) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(200);

        resp.getWriter().println(str);
    }

}
