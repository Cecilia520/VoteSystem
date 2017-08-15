package com.Cecilia.vote.register;

import com.Cecilia.vote.bean.UserBean;
import com.Cecilia.vote.login.Login;
import com.Cecilia.vote.util.SocketUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 用户注册主类
 * Created by Cecilia on 2017/8/4.
 */
public class Register {

    /**
     * 用户注册主方法
     * @param sc   用户交互流
     * @param client  客户端
     * @throws IOException
     */
    public  boolean userRegister(Scanner sc, Socket client, UserBean userBean) throws IOException {

        //tempStr用于存储临时输入数据，防止用户输入的错误信息丢失而再次重新输入
        String username = "",usernick = "",password = "",password1 = "",tempStr;
        System.out.println("用户注册开始，请依次输入用户信息，不能有空格————");
        while(true){
            System.out.println("请输入用户名（1-16位）："+username);
            tempStr = sc.nextLine().trim();
            if (tempStr.length()>0){
                username = tempStr;
            }
            System.out.println("请输入用户昵称（1-16位）："+usernick);
            tempStr = sc.nextLine().trim();
            if (tempStr.length()>0){
                usernick = tempStr;
            }
            System.out.println("请输入用户密码（1-16位）："+password);
            tempStr = sc.nextLine().trim();
            if (tempStr.length()>0){
                password = tempStr;
            }
            System.out.println("请再次输入确认密码（1-16位）："+password1);
            tempStr = sc.nextLine().trim();
            if (tempStr.length()>0){
                password1 = tempStr;
            }

            //本地检测参数是否合法

            int flag = checkParameter(username,usernick,password,password1,client);
            if (flag == 1){
                //注册成功,并保存信息
                Login login = new Login();
                login.cacheUserMessage(username,"普通用户",userBean);
                return true;
            }else if(flag == 2){
                return false;//注册失败
            }
            System.out.println("如不需要修改当前，请回车！");
        }
    }

    /**
     * 本地检测参数是否合法，合法后提交给服务器
     * @param username   用户名
     * @param usernick   用户昵称
     * @param password   用户密码
     * @param password1  用户确认密码
     * @param client     打开的客户端
     * @return 如果返回1，表示注册成功；如果返回2，表示注册失败；如果返回3，表示参数有误
     */
    private int checkParameter(String username, String usernick, String password, String password1, Socket client) throws IOException {

        int returnValue = 3;
        if (username.length()<1||username.length()>16||username.indexOf(" ")>-1){
            System.out.println("用户名必须是1-16位，并且不能为空！");
        }else if (usernick.length()<1||usernick.length()>16||usernick.indexOf(" ")>-1){
            System.out.println("用户昵称必须是1-16位，并且不能为空！");
        }else if (password.length()<1||password.length()>16||password.indexOf(" ")>-1){
            System.out.println("用户密码必须是1-16位，并且不能为空！");
        }else if(!password.equals(password1)){
            System.out.println("两次密码必须输入一致！");

            //检测用户名是否存在已注册的信息中
        }else if (checkUserName(client,username)){
            if (saveUser(client,username,usernick,password)){
                System.out.println("恭喜"+username+"注册成功！");
                returnValue = 1;
            }else{
                System.out.println("用户注册失败，服务器异常！");
                returnValue = 2;
            }
        }else{
            System.out.println("用户名已存在！");
        }
        return returnValue;
    }

    /**
     * 与服务器交互进行检测用户名是否重复
     * @param client  打开的客户端
     * @param username  要检测的用户名
     * @return 检测通过，返回true；否则返回false
     * @throws IOException
     */
    private boolean checkUserName(Socket client,String username) throws IOException {

        //打开输出输入流并协议检测
        Object[] objects = SocketUtil.getOutInAndCheckCode(client);
        if (objects==null){
            return false;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        out.println("编号1");
        out.flush();
        String message = input.readLine();
        String[] arr = message.split("\t");
        //先对字符串数组进行排序
        Arrays.sort(arr);
        //在使用二分法检测索引数组中某个指定值
        if (Arrays.binarySearch(arr,"")==-1){
            return true;
        }
        return false;
    }

    /**
     * 保存用户信息到服务端
     * 其思路就是将用户注册后的用户名、昵称、密码数据信息发送给服务器，然后服务器发送保存成功信息回传个客户端
     * @param client    打开的客户端
     * @param username  用户名
     * @param usernick  用户昵称
     * @param password  用户密码
     * @return  如果保存成功，返回true；否则，返回false
     */
    private boolean saveUser(Socket client,String username,String usernick,String password) throws IOException {

        //打开输入输出流并协议验证
        Object[] objects = SocketUtil.getOutInAndCheckCode(client);
        if (objects==null){
            return false;
        }
        //客户端将注册信息发送给服务器
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        out.println("编号2");
        out.flush();
        out.println(username+"\t"+usernick+"\t"+password);
        out.flush();
        //服务器接收从客户端传来的信息，并且会返给客户端确认信息
        String message = input.readLine();
        if ("true".equals(message)){
            return true;
        }else{
            return false;
        }
    }
}
