package com.Cecilia.vote.server;

import com.Cecilia.vote.ServerStart;
import com.Cecilia.vote.bean.UserBean;
import com.Cecilia.vote.bean.VoteBean;
import com.Cecilia.vote.login.Login;
import com.Cecilia.vote.register.Register;
import com.Cecilia.vote.util.FileUtilImplements;
import com.Cecilia.vote.util.SocketUtil;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 用户信息处理主类
 * Created by Cecilia on 2017/8/1.
 */
public class UserMain {

    public Map<String, UserBean> userMap;//用户信息集合

    public Login login;//用户登录主类

    public final static String userPath = ServerStart.BASEPATH + "userMessage.txt";//用户信息路径

    /**
     * 初始化参数
     */
    public UserMain() {
        userMap = new HashMap<>();
        login = new Login();
    }

    /**
     * 加载用户信息
     *
     * @return 返回true，表示加载成功，反之，加载失败
     */
    public boolean loadUserMessage() {

        List<String> userList = null;
        try {
            userList = FileUtilImplements.readFile(userPath);
        } catch (IOException e) {
            //此处读取文件信息如果抛出异常，需不要进行处理？？（从程序的健壮性开始考虑）
            System.out.println("读取用户信息文件异常！");
            e.printStackTrace();
        }
        if (userList != null && userList.size() > 1) {
            userList.remove(0);//删除第一行带有注解的内容
            for (String user : userList) {
                String[] arr = user.split("\t");
                //System.out.println(arr);
                //将其添加到Map中
                userMap.put(arr[0], new UserBean(arr[0], arr[1], arr[2], arr[3]));
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 用户登录注册功能(客户端使用)
     *
     * @return
     */
    public boolean userResponse(Socket client, UserBean userBean) {

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("请选择您的操作： 1：登录  2：注册 3：退出系统");
            String input = sc.nextLine();
            try {
                if ("1".equals(input)) {
                    //登录
                    if (login.login(sc, client, userBean)) {
                        break;//登陆成功，跳出循环
                    }
                } else if ("2".equals(input)) {
                    //注册
                    Register register = new Register();
                    if (register.userRegister(sc, client, userBean)) {
                        System.exit(0);//注册成功，跳出循环
                    }
                } else if ("3".equals(input)) {
                    System.exit(0);
                } else {
                    System.out.println("对不起，您的操作有误，请按照正确操作操作！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //此处能跳出循环的只有两种情况，一种是登录成功，一种注册成功，就算抛出异常，也不会跳出循环
        return true;
    }



    public Map<String, UserBean> getUserMap() {

        return userMap;
    }

}
