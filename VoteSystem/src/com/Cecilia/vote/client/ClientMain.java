package com.Cecilia.vote.client;

import com.Cecilia.vote.ClientStart;
import com.Cecilia.vote.bean.UserBean;
import com.Cecilia.vote.login.Login;
import com.Cecilia.vote.server.UserMain;
import com.Cecilia.vote.server.VoteMain;
import com.Cecilia.vote.util.FileUtilImplements;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 客户端主类
 * Created by Cecilia on 2017/8/1.
 */
public class ClientMain {

    public final static String CLIENTPATH = ClientStart.BASEPATH + "iniClient.txt";

    private Socket client;//客户端类

    private UserBean userBean;//用户实体类

    private VoteManager voteManager;//投票管理类

    private UserMain userMain;//用户主类

    private UserManager userManager;//用户管理类

    public ClientMain(){

        userBean = new UserBean();
        voteManager = new VoteManager();
        userMain = new UserMain();
        userManager = new UserManager();
    }
    /**
     * 开启客户端
     *
     * @return 如果启动成功，返回true；否则，返回false
     */
    public boolean startClient() {
        //读取配置文件的IP和端口号
        String[] arr = getMessage().split(":");
        startClientSocket(arr);
        String ip = arr[0];
        int port = Integer.valueOf(arr[1]);
        if (port == -1 || ip == null) {
            return false;
        } else {
            try {
                //将端口号和ip写到配置文件
                boolean flag = writeMessage(ip, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 用户功能选择主菜单
     *
     * @param voteMain
     */
    public void userFuntion(VoteMain voteMain) {

        Scanner sc = new Scanner(System.in);
        if ("manager".equals(userBean.getType())) {
            while (true) {
                System.out.println("请选择您需要的功能操作： 1：用户管理  2：投票管理  3:退出登录   4:退出系统");
                String message = sc.nextLine();
                if ("1".equals(message)) {
                    userManager.userManager(sc, client, userBean.getId());
                    continue;
                } else if ("2".equals(message)) {
                    //投票管理
                    voteManager.managerChange(sc, client, userBean.getId());
                } else if ("3".equals(message)) {
                    System.out.println("退出登录成功！");
                    System.out.println("---------------------------------");
                    userMain.userResponse(client, userBean);
                } else {
                    break;
                }
            }
        } else {
            //普通用户菜单
            while (true) {
                System.out.println("请选择您需要的功能操作： 1：个人信息管理  2：进行投票  3：退出登录   4:退出系统");
                String message = sc.nextLine();
                if ("1".equals(message)) {
                    userManager.OneUserManager(client, sc, userBean.getId());
                    continue;
                } else if ("2".equals(message)) {
                    //进行投票
                    voteManager.voting(client, sc, userBean.getId());
                } else if ("3".equals(message)) {
                    System.out.println("退出登录成功！");
                    System.out.println("---------------------------------");
                    userMain.userResponse(client, userBean);
                } else {
                    break;
                }
            }
        }
        sc.close();
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 尝试再次连接服务器
     *
     * @param arr 端口号和ip组成的数组
     *            此处不需要返回值的目的是：字符串数组是引用传递，当其内容发生改变，传出去的内容也会发生改变
     */
    private void startClientSocket(String[] arr) {

        String ip = arr[0];
        int port = Integer.valueOf(arr[1]);
        System.out.println("准备连接服务器中...");
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("当前默认的IP地址为：" + ip + ",端口号为：" + port);
            System.out.println("请选择您的操作： 1：连接服务器  2：修改端口号和ip  3：退出");
            String message = sc.nextLine();
            if ("1".equals(message)) {
                //连接服务器
                try {
                    client = new Socket(ip, port);
                    break;
                } catch (IOException e) {
                    System.out.println("服务器连接失败，当前默认ip地址为：" + ip + ",端口号为：" + port);
                    e.printStackTrace();
                }
            } else if ("2".equals(message)) {
                //修改端口号和ip
                System.out.println("请输入您想要设置的ip地址:");
                ip = sc.next();
                System.out.println("请输入您想要设置的端口号：");
                if (sc.hasNextInt()) {
                    port = sc.nextInt();
                    sc.nextLine();
                } else {
                    System.out.println("对不起，您输入的端口号不是整数，请输入整数！");
                }
            } else if ("3".equals(message)) {
                //退出
                ip = null;
                port = 0;
                System.out.println("客户端退出成功！");
                System.exit(0);
            } else {
                System.out.println("对不起，您的操作有误，请输入正确的操作选项！");
            }
        }
    }

    /**
     * 从配置文件中读取IP地址和对应的端口号
     *
     * @return IP地址：端口号组成的字符串
     */
    private String getMessage() {
        List<String> iniClientMessage = null;
        String port = null;//端口号
        String ip = null;//ip地址

        //开始读取端口号和ip
        try {
            iniClientMessage = FileUtilImplements.readFile(CLIENTPATH);
            //如果集合中读取到内容
            if (iniClientMessage.size() > 3) {
                System.out.println("读取端口号和ip成功！");
                port = iniClientMessage.get(3);
                ip = iniClientMessage.get(1);
            } else {
                port = "8087";
                ip = "127.0.0.1";
            }
        } catch (IOException e) {
            System.out.println("端口号和ip读取失败,其默认端口号为:" + port + ",ip为：" + ip);
            e.printStackTrace();
            port = "8087";
            ip = "127.0.0.1";
        }
        //匹配端口号必须为匹配数字
        Pattern pattern = Pattern.compile("^[+]?[\\d]*$");//取正整数，正负都取^[-\\+]?[\d]*$
        if (!pattern.matcher(port).matches()) {
            System.out.println("端口号有误，使用默认端口号8087");
            port = "8087";
        }
        return (ip + ":" + port);
    }

    /**
     * 创建初始化客户端配置文件，把最新启动成功的端口号和IP写入磁盘
     *
     * @return 如果创建成功，返回true，反之，false
     * @throws IOException
     * @Param port 端口号
     */
    private boolean writeMessage(String ip, int port) throws IOException {

        List<String> context = new ArrayList<>(4);
        context.add("#IP地址");
        context.add(ip);
        context.add("#端口号");
        context.add(port + "");
        context.add("#上一次连接时间");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        context.add(sdf.format(new Date()));
        //将写文件的结果返回
        return FileUtilImplements.writeFile(CLIENTPATH, context, false);
    }

    public Socket getClient() {
        return client;
    }

    public UserBean getUserBean() {
        return userBean;
    }
}
