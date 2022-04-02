<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
    <script type="text/javascript">
        const json = {
            key1:"aa",
            key2:"bb",
            key3:{
                key4:"cc",
                key5:"dd",
            },
            key6:[1,2,3]
        }
        // const str = JSON.stringify(json);
        // const json2 = JSON.parse(str);

        function ajax() {
            // 1. 创建XMLHttpRequest
            const request = new XMLHttpRequest();
            // 2. 设置参数
            request.open("GET","http://localhost:8080/Servlet/json?action=doGet",true);//是否异步

            // 3.定义请求后执行的操作
            request.onreadystatechange= function(){
                // 0-unsend, 1-opened, 2-headers-received, 3-loading, 4-done
                // 在onreadystatechange 里面的代码，从阶段2-4都会执行一次
                console.log("请求阶段: " + request.readyState)
                console.log("状态: " + request.status)
                console.log("返回消息: " + request.responseText)

                // 改变页面内容
                if(request.readyState && request.status==200){
                    // request.responseXML;
                    const obj = JSON.parse(request.responseText);
                    document.getElementById("response1").innerText = "{id: " + obj.id + ",name:" + obj.name + "}";
                }
            }
            // 4.发送请求
            request.send();
        }

    </script>
</head>
    <body>
        <h1><%= "Hello Servlet!" %></h1>
        <br/>
        <a href="get-request">1. GET request</a>
        <br><br>

        2. 登录请求
        <form action="http://localhost:8080/Servlet/post" method="post">
            <label for="username">账号:</label>
<%--            免用户名登录--%>
<%--            <input type="username" id="username" name="username" value="${cookie.username.value}"><br>--%>
            <input type="username" id="username" name="username" value="admin"><br>
            <label for="password">密码:</label>
            <input type="password" id="password" name="password" value="passwd"><br>
            <input type="submit" value="Submit">
        </form>
        <br><br>
        <a href="cookie-add">3. 增加cookie:{key2:value2}</a>
<%--        <a href="/cookie-add?action=doGet">增加cookie:{key2:value2}</a>--%>
        <br><br>

        <button onclick="ajax()"> 4. ajax获取服务器返回的对象</button>

        <div id="response1">
        </div>

        <a href="http://localhost:8080/Servlet/dispatch">
            <button>5. 请求分发</button>
        </a>
        <br>
        <a href="http://localhost:8080/Servlet/redirect">
            <button>6. 重定向</button>
        </a>

    </body>
</html>