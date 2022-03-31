package com.javaweb.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "sessionServlet", value = "/session-")
public class SessionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        System.out.println("是否新Session: " + session.isNew());
        System.out.println("sessionID: " + session.getId());

        session.setAttribute("keySession","valueSession");
        System.out.println("获取Session域里的数据" + session.getAttribute("keySession"));

        // 设置会话超时
        // 方法一：Session-config，见web.xml
        // 方法二：
        session.setMaxInactiveInterval(3); //秒 -->上面isNew 会3秒后重新变true
        session.getMaxInactiveInterval();

//        session.invalidate();     //马上超时

        // 1. 每次接到新用户的请求，会先调用getSession()创建一个新的Session
        //    该Session里会自带一个key=JSESESSIONID 的cookie，
        // 2. 有了该Cookie只有，用户每次对网站的请求都会携带上
        //    服务端再收到该请求的时候就 知道该请求是否新的Session了
        //   （如果删掉上述cookie，服务端会认为是新的Session创建一个新的对象返回给用户）
        // 3.常用于：免登录

    }


}
