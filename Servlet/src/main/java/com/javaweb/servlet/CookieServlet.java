package com.javaweb.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@WebServlet(name = "cookieServlet", value = "/cookie-add")
public class CookieServlet extends HttpServlet {

    public void init() {
        System.out.println("--------------------------------------------");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("有被调用");

        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(200);

        //在页面输出 用户传输过来的cookie
        resp.getWriter().write(getcooie(req));

        //给用户返回cookie
        response(req,resp);
    }

    private void response(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        // set-cookie 如果发现浏览器已经有该key的cookie会更新原有的value
        //如果要修改原本的值的话，也可以遍历req.getCookies 找到并调用cookie.setValue("newValue1")
        //cookie的值不能包含：空值、符号（好像除了下划线可以）
        Cookie cookie = new Cookie("key1","value1");

        // 设置存活时间,单位：秒。默认-1：关浏览器就没，0：马上删除
        cookie.setMaxAge(3600); // 1h
        //设置工程路径，用于筛选cookie应该发给哪些路径的请求
        cookie.setPath(req.getContextPath() + "/cookie");

        resp.addCookie(cookie);

        resp.addDateHeader("Date", new Date().getTime());
        PrintWriter out = resp.getWriter();
        out.write("增加一个cookie");
    }

    private String getcooie(HttpServletRequest req) {
        String msg = "{<br>";
        Cookie[] cookies = req.getCookies();
        for (Cookie c:cookies) {
            msg += c.getName() + " = " + c.getValue() + "; <br>";
        }
        return msg + "} <br><br><br>";
    }
}
