import com.spring.d_proxy.Host;
import com.spring.d_proxy.ProxyInvocationHandler;
import com.spring.d_proxy.Rent;

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
