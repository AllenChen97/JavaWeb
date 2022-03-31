# JavaWeb

----------------------------
## 1. Tomcat
### 1.1. 安装

### 1.2. 配置

### 1.3. 启动

### 1.4. 端口号修改

### 1.5. 添加第三方jar包

----------------------
## 2. Servlet
### 1.1. 启动一个Servlet程序

### 1.2. 生命周期

### 1.3. 请求分发dispatch

### 1.4. Servlet体系

#### 1.4.1. ServletConfig

包含：初始化信息和一个ServletContext

#### 1.4.2. ServletContext
① 获取初始化参数
```xml
<!--配置初始化参数-->
<context-param>
    <param-name>key1</param-name>
    <param-value>value1</param-value>
</context-param>
```
```java
getInitParameter(String key);
```
②域对象，可以在整个web应用范围内共享数据
```java
setAttribute("key","value");
String val = getAttribute("key");
removeAttribute("key");  
```
#### 1.4.3. [HttpServletRequest](https://blog.csdn.net/qq_34666857/article/details/104677407?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522164867561216782092936924%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=164867561216782092936924&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-104677407.142^v5^pc_search_result_control_group,143^v6^control&utm_term=HttpServletRequest&spm=1018.2226.3001.4187)

#### 1.4.4. [HttpServletResponse](https://blog.csdn.net/qq_34666857/article/details/104838171?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522164867614716782248568547%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=164867614716782248568547&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-104838171.142^v5^pc_search_result_control_group,143^v6^control&utm_term=HttpServletResponse&spm=1018.2226.3001.4187)

----------------------------
## 3. Cookie & Session

### 3.1.Cookie
定义：
```java
// set-cookie 如果发现浏览器已经有该key的cookie会更新原有的value
//如果要修改原本的值的话，也可以遍历req.getCookies 找到并调用cookie.setValue("newValue1")
//cookie的值不能包含：空值、符号（好像除了下划线可以）
Cookie cookie = new Cookie("key1","value1");

// 设置存活时间,单位：秒。默认-1：关浏览器就没，0：马上删除
cookie.setMaxAge(3600); // 1h
//设置工程路径，用于筛选cookie应该发给哪些路径的请求
cookie.setPath(req.getContextPath() + "/cookie");

resp.addCookie(cookie);
```
### 3.2.Session
定义：
```java
System.out.println("是否新Session: " + session.isNew());
System.out.println("sessionID: " + session.getId());

session.setAttribute("keySession","valueSession");
System.out.println("获取Session域里的数据" + session.getAttribute("keySession"));

// 设置会话超时
// 方法一：Session-config，见web.xml
// 方法二：
session.setMaxInactiveInterval(3); //秒 -->上面isNew 会3秒后重新变true
session.getMaxInactiveInterval();
```
### 3.3. 用户请求与Session
① 每次接到新用户的请求，会先调用getSession()创建一个新的Session
   该Session里会自带一个key=JSESESSIONID 的cookie，

② 有了该Cookie只有，用户每次对网站的请求都会携带上
   服务端再收到该请求的时候就 知道该请求是否新的Session了
  （如果删掉上述cookie，服务端会认为是新的Session创建一个新的对象返回给用户）

③ 常用于：免登录

----------------------------
## 4. Ajax & Json

```javascript
// 将下面定义的函数绑定到button中，即可在#msg的div中获取接口内容
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
```