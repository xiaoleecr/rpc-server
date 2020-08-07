package com.gupaoedu.vip;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //发布服务
        IHelloService helloService = new HelloServiceImpl();
        RpcProxyServer proxyServer = new RpcProxyServer();
        proxyServer.publisher(helloService,8080);//发布到8080端口

    }
}
