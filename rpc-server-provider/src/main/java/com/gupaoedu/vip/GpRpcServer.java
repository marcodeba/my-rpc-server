package com.gupaoedu.vip;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class GpRpcServer implements ApplicationContextAware, InitializingBean {
    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private Map<String, Object> handlerMap = new HashMap();
    private int port;
    private volatile boolean isStopped = false;

    public GpRpcServer(int port) {
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (!isStopped) {
                Socket socket = serverSocket.accept();
                executorService.submit(new ProcessorHandler(socket, handlerMap));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void stop() {
        this.isStopped = true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!serviceBeanMap.isEmpty()) {
            for (Object servcieBean : serviceBeanMap.values()) {
                //拿到注解
                RpcService rpcService = servcieBean.getClass().getAnnotation((RpcService.class));
                if (rpcService == null) continue;
                String serviceName = rpcService.value().getName();//拿到接口类定义
                String version = rpcService.version(); //拿到版本号
                if (!StringUtils.isEmpty(version)) {
                    serviceName += "-" + version;
                }
                handlerMap.put(serviceName, servcieBean);
            }
        }
    }
}
