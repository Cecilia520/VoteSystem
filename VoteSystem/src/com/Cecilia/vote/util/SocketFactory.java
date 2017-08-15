package com.Cecilia.vote.util;

import java.io.IOException;
import java.net.Socket;

/**
 * Socket创建的工厂类
 * Created by Cecilia on 2017/8/5.
 */
public class SocketFactory{

    public static String ip;//IP地址

    public static int port;//端口号

    public static Socket create() throws IOException {
        return new Socket(ip,port);
    }
}

