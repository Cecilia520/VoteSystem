package com.Cecilia.vote.server;

import com.Cecilia.vote.ServerStart;
import com.Cecilia.vote.util.FileUtilImplements;
import com.Cecilia.vote.util.ThreadUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 服务端主类
 * Created by Cecilia on 2017/8/1.
 */
public class ServerMain {

    private ServerSocket serverSocket;//服务端类

    public final static String SERVERPATH = ServerStart.BASEPATH+"initServer.txt";
    /**
     * 启动服务端
     * @return 启动成功返回true，反之，返回false
     */
    public boolean startServer(VoteMain voteMain){

        int port = getPortFormFile();
        port = startServerSocket(port);
        if (port==-1){
            return false;
        }else{
            try {
                writePort(port);
            } catch (IOException e) {
                System.out.println("端口号持久化失败，程序继续执行！");
                e.printStackTrace();
            }
        }
        //增加定时器，以实现投票到了结束时间自动停止
        Timer timer = new Timer();
        timer.schedule(new VoteTimertask(voteMain),0,1000*60);
        return true;
    }

    /**
     * 处理客户端的请求
     * @param userMain  用户信息对象
     * @param voteMain  投票信息对象
     */
    public void responseClient(UserMain userMain,VoteMain voteMain){
        while(true){
            try {

                //使服务器阻塞等待直到客户端请求
                Socket socket = serverSocket.accept();
                //启动服务器线程
                ServerThread serverThread = new ServerThread(socket,userMain,voteMain);
                //将其加入线程池中
                ThreadUtil.runCheckRunnable(serverThread);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建初始化服务器配置文件，把最新启动成功的端口号写入磁盘
     * @Param port 端口号
     * @return  如果创建成功，返回true，反之，false
     * @throws IOException
     */
    private boolean writePort(int port) throws IOException {
        //文件内容
        List<String> context = new ArrayList<>(4);
        context.add("#端口号");
        context.add(String.valueOf(port));
        context.add("#上一次启动时间");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        context.add(sdf.format(new Date()));
        //将写文件的结果返回
        return  FileUtilImplements.writeFile(SERVERPATH,context,false);
    }

    /**
     * 尝试启动服务器
     * @param port  要启动的端口号
     * @return  启动成功，返回正常端口号，反之，返回-1
     */
    private int startServerSocket(int port) {
        int returnValue = -1;
        Scanner sc= new Scanner(System.in);
        while(true){
            System.out.println("当前默认的端口号为："+port);
            System.out.println("请输入您的操作： 1:启动服务器  2：修改端口号  3：退出");
            String message = sc.next();
            if ("1".equals(message)) {
                try {

                    serverSocket = new ServerSocket(port);
                    returnValue = port;
                    break;
                } catch (IOException e) {
                    System.out.println("服务器启动失败，当前端口号为："+port);
                    e.printStackTrace();
                }
            }else if("2".equals(message)){
                System.out.println("请输入您需要设置的端口：");
                if (sc.hasNextInt()){
                    port = sc.nextInt();
                    System.out.println("修改端口成功！");
                }else{
                    System.out.println("您输入的端口号有误，抱歉只能输入整数！");
                }
            }else if ("3".equals(message)){
                returnValue = -1;
                break;
            }else{
                System.out.println("功能选择错误！");
            }
        }
        return returnValue;
    }

    /**
     * 从配置文件中读取端口号
     * 读取失败，返回默认的端口号
     * @return  读取的端口号
     */
    private int getPortFormFile() {
        List<String> initMessageList = null;
        String portStr = "8087";
        try{
            //读取文件内容
            initMessageList = FileUtilImplements.readFile(SERVERPATH);
            //此处不需要对initMessageList进行检测，由于readFile犯法中已经对此进行了检测
            if (initMessageList.size() > 1) {
                portStr = initMessageList.get(1);
            }
        }catch(IOException e){
            //此处需要进行异常处理，增强程序的健壮性
            System.out.println("端口号配置文件读取失败，使用默认端口8087");
            e.printStackTrace();
            portStr = "8087";
        }
        // 对于格式化输入不对时，会抛出运行时的NumberFormatException异常，解决端口号不是数字的更好的办法
        //解决方案一：正则表达式
        int cnt = 8087;
        Pattern pattern = Pattern.compile("^[+]?[\\d]*$");
        if (pattern.matcher(portStr).matches()){
           cnt = Integer.valueOf(portStr);
        }
        return cnt;
        /*
        * 解决方案二：直接try—catch
        * int cnt = 0;
        * try{
        *  cnt = Integer.valueOf(portStr);
        * }catch(NumberFormatException e){
        *  System.out.println("读取到的端口号不是数字："+port);
        *   e.printStackTrace();
        *   cnt = 8087;
        * }
        * return cnt;
        * */
    }
}
