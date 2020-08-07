package com.gupaoedu.vip;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 用于处理请求的类
 */
public class ProcessorHandler implements Runnable {
    private Socket socket;
    private Object service;

    public ProcessorHandler(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            //输入流中应该有什么内容
            //请求哪个类，方法名称，方法入参？
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            //获得方法调用结果
            Object result = invoke(rpcRequest);
            //获得输出流以用于输出
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //进行输出
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }finally {
            //关闭流
            if (objectInputStream != null){
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectOutputStream != null){
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private Object invoke(RpcRequest request) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //反射调用目标方法
        Object[] args = request.getParameters();//获取客户端请求参数
        Class<?>[] types = new Class[args.length];//获得每个参数的类型
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();

        }
        //加载传过来的目标类
        Class clazz = Class.forName(request.getClassName());
        Method method = clazz.getMethod(request.getMethodName(),types);
        Object result = method.invoke(service,args);
        return result;
    }
}
