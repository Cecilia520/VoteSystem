package com.Cecilia.vote.util;

import java.io.*;
import java.net.Socket;

/**
 * Socket的工具类
 * Created by Cecilia on 2017/8/3.
 */
public class SocketUtil {

    private SocketFactory socketFactory;

    public SocketUtil(){
        socketFactory = new SocketFactory();
    }
    /**
     * 打开Socket的输入输出流，并进行协议检测
     * @param client    已打开的链接
     * @param username  用户名
     * @return 打开流成功且协议检测通过，则返回，第一个是PrintWriter，第二个是BufferReader，否则返回false
     */
    public static Object[] getOutInAndCheckCode(Socket socket){

//        socketFactory.create(ip,);
        PrintWriter out = null;
        Reader reader = null;
        try {
            out = new PrintWriter(socket.getOutputStream());
            reader = new InputStreamReader(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Socket输入输出流打开失败！");
            e.printStackTrace();
        }
        if (out==null||reader==null){
            return null;
        }
        BufferedReader input = new BufferedReader(reader);
        Object[] returnValue = new Object[]{out,input};
        try {
            //协议检测
            out.println("我要请求...");
            out.flush();
            String message = input.readLine();
            if (!"答应您的请求!".equals(message)){
                returnValue = null;
            }
        } catch (IOException e) {
            returnValue = null;
            System.out.println("协议验证失败！");
            e.printStackTrace();
        }
        return returnValue;
    }

    /**
     * 获取输入输出流
     * @param socket 已打开的链接
     * @return       返回对象数组，包括输入流和输出流的数组
     */
    public static Object[] getOutAndIn(Socket socket){

        PrintWriter out = null;
        Reader reader = null;
        try {
            out = new PrintWriter(socket.getOutputStream());
            reader = new InputStreamReader(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Socket输入输出流打开失败！");
            e.printStackTrace();
        }
        if (out==null||reader==null){
            return null;
        }
        BufferedReader input = new BufferedReader(reader);
        Object[] returnValue = new Object[]{out,input};
        return returnValue;
    }
}
