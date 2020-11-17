package com.gupaoedu.vip;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

public class ProcessorHandler implements Runnable {
    private Socket socket;
    private Map<String, Object> handlerMap;

    public ProcessorHandler(Socket socket, Map<String, Object> handlerMap) {
        this.socket = socket;
        this.handlerMap = handlerMap;
    }

    @Override
    public void run() {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;

        try {
            ois = new ObjectInputStream(socket.getInputStream());
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();

            Object result = this.invokeMethod(rpcRequest);

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(result);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Object invokeMethod(RpcRequest request) {
        String methodName = request.getMethodName();
        String version = request.getVersion();
        String serviceName = StringUtils.isEmpty(version) ? request.getClassName() : request.getClassName() + "-" + version;
        Object[] args = request.getParameters();

        Object service = handlerMap.get(serviceName);
        if (service == null) {
            throw new RuntimeException("service not found : " + serviceName);
        }

        try {
            Class clazz = Class.forName(request.getClassName());
            Method method;
            if (args == null) {
                method = clazz.getMethod(methodName);
            } else {
                Class<?>[] paramTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    paramTypes[i] = args[i].getClass();
                }
                method = clazz.getMethod(methodName, paramTypes);
            }

            return method.invoke(service, args);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
