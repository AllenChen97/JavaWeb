package com.javaweb.分发和重定向;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class Worker extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        response(resp);
    }

    private void response(HttpServletResponse resp) throws IOException {
        //解决中文乱码问题
        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(200);

        // reponse a hello
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + "恭喜你啊" + "</h1>");
        out.println("</body></html>");
    }

}