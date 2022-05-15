# 一、安装

## 1. 安装依赖
```shell
# 安装pcre
tar -zxvf pcre.tar.gz
cd pcre
make
make install

# 安装其他依赖
yum -y install zlib zliv-devel gcc-c++ libtool openssl openssl-devel
```

## 2. 安装nginx
```shell
tar -zxvf nginx.tar.gz
cd nginx
make
make install

firewall-cmd --add-port=80/tcp --permanent
firewall-cmd --reload
```

## 3. 配置 软链接
```shell
# 添加 /usr/local/bin/nginx 到环境变量
vim ~/.bashrc  # export PATH=/usr/local/bin:$PATH
source /etc/profile

# 将安装好的 nginx 软连接到 /usr/local/bin/ 目录下
ln -s /usr/local/nginx/sbin/nginx /usr/local/bin/
```

# 二、配置说明
## 1. 命令
```shell
# 开、关、重新加载
nginx -v
nginx
nginx -s stop
nginx -s reload
```

## 2. 全局块
- /usr/local/nginx/conf/nginx.conf
- 影响nginx整体运行
```text
work_processes 1; # 处理器越多，支持的并发量越多
```

## 3. event块
- 与用户网络链接相关
```text
work_connections 1024; # 每个处理器支持的最大连接数
```

## 4. http块
- 配置最频繁的地方，也可以配置全局 http块 和 server块

# 三、功能

## 1. 反向代理
1. 安装tomcat服务器，使用默认端口8080
```shell
# 下载、解压、开启
/home/centos/tomcat/sbin/starup.sh
tail -f /home/centos/tomcat/log/catalina.out

# 开放防火墙
firewall-cmd --add-port=8080/tcp --permanent
firewall-cmd --reload
firewall-cmd --list-all # 还可以打开 52.56.61.243:8080看看是否已经启动
```
1. 反向代理服务器 配置
   - 将所有对nginx服务器80端口的请求转发到 http://服务器ip:8080
   - 其中 http://服务器ip:8080 才是真正存有访问资源的，访问服务器只负责转发请求
```text
server{
    listen 80;
    server_name 监听的对象;
    
    location  / {
        root html;
        proxy_server http://服务器ip:8080
        index index.html
    }
}
```

3. 反向代理 某端口服务 配置
    1. 创建多个自定义端口的tomcat服务，在8081 和 8082
   ```shell
   cd tomcat
   vim server.xml # 修改 tomcat connector port="8081" 
   ```
    2. 配置端口转发
       - 将 http://nginx服务器id/test1/ 的请求 转发到 http://服务器ip:8081
       - 在 location 之后加
       - = 不含正则表达，必须完全匹配
       - ~ 包含正则，大小写敏感
       - ~* 包含正则，大小写不敏感
       - ^~ 
   ```text
   server{
       listen 80;
       server_name 监听的对象;
       
       location ~* /test1/ {
           proxy_pass http://服务器ip:8081;
       }
          location ~* /test2/ {
           proxy_pass http://服务器ip:8082;
       }
   }
   ```

## 2. 负载均衡
1. nginx 配置
```text
   upstream myserver{
      server http://服务器ip:8081;
      server http://服务器ip:8082;
   }
   server{
       listen 80;
       server_name 监听的对象;
       
       location / {
            root html;
            proxy_pass http://myserver;
            index index.html
       }
   }
```
2. 分配策略
   - 直接在 upstream 里写
   1. 轮询: 默认，逐一访问，如果服务器宕机，能自动剔除
   2. weight: 权重越高 访问比例越高
   3. ip_hash: 让用户固定只访问一台服务器，可以解决session问题
   4. fair: 第三方，按后端服务器的响应时间来分配请求，响应快的优先


## 3. 动静分离


## 4. 高可用

