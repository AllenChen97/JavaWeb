import com.spring.d_Proxy.Host;
import com.spring.d_Proxy.ProxyInvocationHandler;
import com.spring.d_Proxy.Rent;

public class ProxyTest {
    public static void main(String[] args) {
        // 生成真实角色
        Host host = new Host();

        ProxyInvocationHandler proxyInvocationHandler = new ProxyInvocationHandler();

        proxyInvocationHandler.setTarget(host);
        // 生成代理类
        Rent proxy = (Rent) proxyInvocationHandler.getProxy();

        proxy.rent();

    }
}
