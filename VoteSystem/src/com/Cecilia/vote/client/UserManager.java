package com.Cecilia.vote.client;

import com.Cecilia.vote.bean.UserBean;
import com.Cecilia.vote.server.UserMain;
import com.Cecilia.vote.util.SocketUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 用户管理类
 * Created by Cecilia on 2017/8/14.
 */
public class UserManager {

    private UserMain userMain;

    public UserManager (){
        userMain = new UserMain();
    }
    /**
     * 用户管理主函数（客户端）
     *
     * @param sc       输入流
     * @param client   已打开的连接
     * @param username 用户名
     */
    public void userManager(Scanner sc, Socket client, String username) {

        while (true) {
            System.out.println("请选择您需要的功能操作：1：查看用户信息   2：重置密码   3：删除用户   4：返回上一层");
            String message = sc.nextLine();
            if ("1".equals(message)) {
                searchUserMessage(client, sc, username);
            } else if ("2".equals(message)) {
                System.out.println("请输入您想要重置密码用户的用户名：");
                username = sc.nextLine();
                resetPassword(client, username);
            } else if ("3".equals(message)) {
                System.out.println("请输入您想要删除的用户名称：");
                username = sc.nextLine();
                delUser(client, username);
            } else {
                break;
            }
        }
    }

    /**
     * 个人信息管理主函数（客户端）
     *
     * @param client   已打开的客户端连接
     * @param sc       输入流
     * @param username 用户名
     */
    public void OneUserManager(Socket client, Scanner sc, String username) {

        while (true) {
            System.out.println("请选择您需要的功能操作：1：修改昵称   2：修改密码   3：返回上一层");
            String message = sc.nextLine();
            if ("1".equals(message)) {
                System.out.println("对不起，正在建设中...");
            } else if ("2".equals(message)) {
                System.out.println("对不起，正在建设中...");
            } else if ("3".equals(message)) {
                break;
            } else {
                System.out.println("对不起，您的操作有误，请重新输入！");
            }
        }
    }

    /**
     * 查看用户信息，包括所有用户信息和指定的个人信息（客户端）
     *
     * @param client   已打开的客户端连接
     * @param sc       输入流
     * @param username 用户名称
     */
    private void searchUserMessage(Socket client, Scanner sc, String username) {

        while (true) {
            System.out.println("请选择您需要的功能操作： 1：查看所有用户信息   2：查看指定的用户信息  3：返回上一层");
            String message = sc.nextLine();
            if ("1".equals(message)) {
                //查找所有用户信息
                searchAllUsers(client);
            } else if ("2".equals(message)) {
                //查找指定用户信息
                searchUser(client, sc);
            } else if ("3".equals(message)) {
                break;
            } else {
                System.out.println("对不起，您的操作有误，请重新输入！");
            }
        }
    }

    /**
     * 查看所有用户信息(客户端)
     *
     * @param client 已打开的客户端连接
     */
    private void searchAllUsers(Socket client) {

        List<String> userList = new ArrayList<>();
        List<String> userBeanList = getAllUser(client, userList, userMain.getUserMap());//获取所有用户信息
        System.out.println("-----------------------所有的用户信息如下：------------------------");
        System.out.println("用户名" + "\t" + "用户昵称" + "\t" + "用户密码" + "\t" + "用户类型");
        if (userBeanList != null) {
            for (String user : userBeanList) {
                System.out.println(user);
            }
        }
    }

    /**
     * 查找指定用户信息（客户端）
     *
     * @param client   已打开的客户端连接
     * @param username 用户名称
     */
    private void searchUser(Socket client, Scanner sc) {

        while (true) {
            System.out.println("请输入您想要查找的用户的名称：");
            String name = sc.nextLine();
            if (name != null) {
                //此处留有一个bug，如果输入的用户名不存在内存的信息的问题还没解决！！！

                System.out.println("-----------------------" + name + "用户的信息如下：------------------------");
                System.out.println("用户名" + "\t" + "用户昵称" + "\t" + "用户密码" + "\t" + "用户类型");
                String oneUserMessage = getOneUser(client, name);//获取指定用户信息
                System.out.println(oneUserMessage);
                break;
            } else {
                System.out.println("对不起，您的输入有误，请重新输入！");
                continue;
            }
        }
    }

    /**
     * 获取所有用户信息（客户端）
     *
     * @param socket       已打开的连接
     * @param userBeanList 用户列表
     * @param userMap      用户Map
     */
    private List<String> getAllUser(Socket socket, List<String> userList, Map<String, UserBean> userBeanMap) {

        //每一次获取的信息都将其清空
        if (userList != null) {
            userList.clear();
        }
        userBeanMap.clear();
        Object[] objects = SocketUtil.getOutInAndCheckCode(socket);
        if (objects == null) {
            return null;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        out.println("编号13");
        out.flush();
        String message = "";
        try {
            while (true) {
                message = input.readLine();//接收从服务端传过来的信息
                if ("end".equals(message)) {
                    break;
                }
                String[] arr = message.split("\t");
                if (userList != null) {
                    userList.add(formatUserMessage(arr));
                    userBeanMap.put(arr[1], new UserBean(arr[0], arr[1], arr[2], arr[3]));
                }
            }
        } catch (IOException e) {
            if (userList != null) {
                userList.clear();
            }
            userBeanMap.clear();
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * 获取指定用户信息（客户端）
     *
     * @param client   已打开的客户端连接
     * @param username 用户名称
     */
    private String getOneUser(Socket client, String username) {

        Object[] objects = SocketUtil.getOutInAndCheckCode(client);
        if (objects == null) {
            return null;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        out.println("编号14");
        out.flush();
        out.println(username);
        out.flush();

        //接收从服务器回传过来的个人用户信息字符串
        String message = "";
        String str = "";
        try {
            while (true) {
                message = input.readLine();
                if ("end".equals(message)) {
                    break;
                }
                String[] arr = message.split("\t");
                str = arr[1] + "\t" + arr[0] + "\t" + arr[2] + "\t" + arr[3];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 重置密码（客户端）
     *
     * @param socket 已打开的连接
     * @param userid 用户id
     */
    private void resetPassword(Socket socket, String username) {

        Object[] objects = SocketUtil.getOutInAndCheckCode(socket);
        if (objects == null) {
            return;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        out.println("编号15");
        out.flush();
        out.println(username);
        out.flush();

        //接收从服务器回传过来的信息
        String message = "";
        try {
            message = input.readLine();
            if ("true".equals(message)) {
                System.out.println("重置密码成功！");
            } else {
                System.out.println("重置密码失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 客户端发送给服务器删除指定用户信息
     *
     * @param client   已打开的客户端连接
     * @param username 用户名称
     */
    private void delUser(Socket client, String username) {

        // 1.客户端：管理员输入想要删除的用户编号，即客户端向服务器发送一个请求和用户编号；
        // 2.服务端：服务器接收到后，调用删除对应编号的用户信息的方法（删除两个地方，一个是内存usermap中，一个是磁盘文件中），从而将true或者false信息回传给客户端，如果收到true，那么客户端输出删除成功提示语
        Object[] objects = SocketUtil.getOutInAndCheckCode(client);
        if (objects == null) {
            return;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        //客户端发送请求
        out.println("编号16");
        out.flush();
        out.println(username);
        out.flush();

        String message = "";
        try {
            //接收服务器的信息
            message = input.readLine();
            if ("true".equals(message)) {
                System.out.println("删除成功！");
            } else {
                System.out.println("删除失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 格式化用户信息
     *
     * @param arr 信息数组
     * @return 以\t相连接而成的字符串
     */
    private String formatUserMessage(String[] arr) {
        StringBuffer sb = new StringBuffer();
        sb.append(arr[1] + "\t");
        sb.append(arr[0] + "\t");
        sb.append(arr[2] + "\t");
        sb.append("user");
        return sb.toString();
    }
}
