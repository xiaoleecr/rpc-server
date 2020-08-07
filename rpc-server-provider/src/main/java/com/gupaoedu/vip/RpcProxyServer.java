package com.gupaoedu.vip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcProxyServer {
    ExecutorService executorService = Executors.newCachedThreadPool();

    public void publisher(Object service,int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {//不断接收请求
                Socket socket = serverSocket.accept();
                //每个socket交给一个processorhandler处理
                executorService.execute(new ProcessorHandler(socket,service));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
