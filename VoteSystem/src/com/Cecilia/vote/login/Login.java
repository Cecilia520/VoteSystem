package com.Cecilia.vote.login;

import com.Cecilia.vote.bean.UserBean;
import com.Cecilia.vote.util.SocketUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * 用户登录主类
 * Created by Cecilia on 2017/8/4.
 */
public class Login {

    /**
     * 用户登录主功能
     *
     * @param sc     用户交互流
     * @param client 打开的客户端
     * @return
     */
    public boolean login(Scanner sc, Socket client, UserBean userBean) {
        boolean flag = true;
        while (true) {
            System.out.println("请输入您的用户名：");
            String username = sc.nextLine().trim();
            System.out.println("请输入您的密码：");
            String password = sc.nextLine().trim();
            //进行本地校验输入信息是否合法
            if (username.length() < 1 || password.length() < 1) {
                System.out.println("用户名和密码不能为空！");
                continue;
            }
            //进行服务器信息校验
            if (username.lastIndexOf(" ") > -1 || password.lastIndexOf(" ") > -1) {
                System.out.println("用户名和密码格式错误！");
                continue;
            }
            //向服务器做登录验证，验证成功，则对此进行缓存
            try {
                if (serverLogin(client, username, password, userBean)) {
                    break;
                }
            } catch (IOException e) {
                //此处服务器可能会出现抛出异常，不对此做任何处理，因为服务器已经异常，如果出现，那么返回flag = false，并break,跳出循环
                System.out.println("对不起，服务器异常！");
                flag = false;
                e.printStackTrace();
                break;
            }
        }
        return flag;
    }

    /**
     * 向服务器做登录验证，验证成功，则对此进行缓存
     *
     * @param client   打开的客户端
     * @param username 用户名
     * @param password 密码
     * @return 返回true，表示登陆成功；返回false，表示登陆失败
     */
    private boolean serverLogin(Socket client, String username, String password, UserBean userBean) throws IOException {

        //1.调用SocketUtil工具类中的getOutInAndCheckCode()方法获取输入输出流
        Object[] objects = SocketUtil.getOutInAndCheckCode(client);

        if (objects == null) {
            return false;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];

        //2.给服务器发送功能请求，编号3，即代表登录，匹配需要进行登录操作
        out.println("编号3");
        out.flush();

        //3.向服务器发送用户登录输入的用户名和密码
        out.println(username + "\t" + password);
        out.flush();

        //4.服务器接收客户端传来的信息，并对此进行检测，如果匹配成功，提示当前用户登陆成功，并将客户端传过来的信息进行缓存，否则提示信息输入错误，登陆失败
        String message = input.readLine();
        if (message!=null&&message.startsWith("SUCCESS")) {
            String[] loginMessage = message.split("\t");
            System.out.println("恭喜" + username + "，您登录成功！");
            //登录成功，对信息进行缓存
            cacheUserMessage(username, loginMessage[1], userBean);
        } else {
            System.out.println("对不起，您输入的用户名或密码错误！");
            return false;
        }
        return true;
    }

    /**
     * 缓存当前登录用户信息
     * 其思路是：将当前登录信息添加到UserBean
     *
     * @param username 用户名
     * @param type     用户类型（管理者/普通用户）
     */
    public void cacheUserMessage(String username, String type, UserBean userBean) {
        userBean.setId(username);
        userBean.setType(type);
    }
}
