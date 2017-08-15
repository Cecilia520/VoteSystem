package com.Cecilia.vote.server;

import com.Cecilia.vote.bean.UserBean;
import com.Cecilia.vote.bean.VoteBean;
import com.Cecilia.vote.bean.VoteDetailBean;
import com.Cecilia.vote.client.ClientMain;
import com.Cecilia.vote.util.FileUtilImplements;
import com.Cecilia.vote.util.SocketUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * 服务器客户端处理线程
 * Created by Cecilia on 2017/8/2.
 */
public class ServerThread implements Runnable {

    private Socket socket;

    private UserMain userMain;  //用户主类

    private VoteMain voteMain;//投票信息主类

    public ServerThread(Socket socket, UserMain userMain, VoteMain voteMain) {
        this.socket = socket;
        this.userMain = userMain;
        this.voteMain = voteMain;
    }

    @Override
    public void run() {

        Object[] objects = SocketUtil.getOutAndIn(socket);
        try {
            if (objects != null) {
                PrintWriter out = (PrintWriter) objects[0];
                BufferedReader input = (BufferedReader) objects[1];
                while (true) {
                    //1.首先进行协议检测
                    String message = input.readLine();
                    if (!"我要请求...".equals(message)) {
                        break;
                    } else {
                        out.println("答应您的请求!");
                        out.flush();
                    }
                    //2.接收客户端发送的请求,根据不同请求功能类型对此做相应的响应
                    message = input.readLine();
                    if ("编号1".equals(message)) {
                        //获取所有用户的用户名信息
                        readAllUserName(out);
                    } else if ("编号2".equals(message)) {
                        //保存所有用户信息，并且持久化到磁盘中
                        saveNewUser(input, out);
                    } else if ("编号3".equals(message)) {
                        //进行登录操作
                        userLogin(input, out);
                    } else if ("编号4".equals(message)) {
                        //保存投票信息
                        saveVote(input, out);
                    } else if ("编号5".equals(message)) {
                        //发送投票信息
                        sendAllVoteMessage(input, out);
                    } else if ("编号6".equals(message)) {
                        //发送投票明细
                        sendVoteDetails(input, out);
                    } else if ("编号7".equals(message)) {
                        //结束投票
                        endVote(input, out);
                    } else if ("编号8".equals(message)) {
                        //删除投票
                        delVote(input, out);
                    } else if ("编号9".equals(message)) {
                        //发送投票选项
                        sendVoteOptions(input, out);
                    } else if ("编号10".equals(message)) {
                        //保存投票明细
                        saveVoteDetail(input, out);
                    } else if ("编号11".equals(message)) {
                        //获取投票明细用户ID
                        getVoteDetailUserId(input, out);
                    } else if ("编号12".equals(message)) {
                        //添加选项
                        addOption(input, out);
                    } else if ("编号13".equals(message)) {
                        sendAllUser(input, out);
                    } else if ("编号14".equals(message)) {
                        sendOneUser(input, out);
                    } else if ("编号15".equals(message)) {
                        resetPassowrd(input, out);
                    } else if ("编号16".equals(message)) {
                        delUser(input, out);
                    } else {
                        System.out.println("对不起，您操作的编号不存在！");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 服务器发送所有的用户信息给客户端
     *
     * @param input 输入流
     * @param out   输出流
     */
    private void sendAllUser(BufferedReader input, PrintWriter out) {

        //先得到所有的key
        Set<String> userList = userMain.getUserMap().keySet();

        //每遍历一个key，就得到一个value
        for (String str : userList) {
            UserBean userBean = userMain.getUserMap().get(str);

            if ("user".equals(userBean.getType())) {
                out.println(userBean.getUserName() + "\t" + userBean.getId() + "\t" + userBean.getPassword() + "\t" + "user");
                out.flush();
            }
        }
        out.println("end");
        out.flush();
    }

    /**
     * 发送指定的个人信息给客户端
     *
     * @param input 输入流
     * @param out   输出流
     */
    private void sendOneUser(BufferedReader input, PrintWriter out) {

        String username = null;
        try {
            username = input.readLine();//接收对应用户名的个人信息
            UserBean userBean = userMain.getUserMap().get(username);
            if (userBean != null) {
                out.println(userBean.getUserName() + "\t" + userBean.getId() + "\t" + userBean.getPassword() + "\t" + userBean.getType());
                out.flush();
                out.println("end");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置密码（服务器设定统一密码）
     * 直接在内存中操作，然后重新写入磁盘中
     *
     * @param input 输入流
     * @param out   输出流
     */
    private void resetPassowrd(BufferedReader input, PrintWriter out) {

        String username = "";
        try {
            //接收客户端发过来的用户名信息
            username = input.readLine();

            //根据用户名得到对应的UserBean对象，然后找到对应的密码
            UserBean userBean = userMain.getUserMap().get(username);
            if (!"user".equals(userBean.getType())) {
                return;
            }
            userBean.setPassword("666");//重置密码
            userMain.getUserMap().put(username, userBean);//将修改后的内容重新添加到内存中

            //修改磁盘中信息,即将Map集合中的内容重写全部写入磁盘文件中
            List<String> userList = getUserToStrings(userMain.getUserMap());
            boolean flag = FileUtilImplements.writeFile(UserMain.userPath, userList, false);
            if (flag) {
                out.println("true");
                out.flush();
            } else {
                out.println("false");
                out.flush();
            }
        } catch (IOException e) {
            out.println("false");
            out.flush();
            e.printStackTrace();
        }
    }

    /**
     * 将修改后的内容重新组装成文件内容
     *
     * @return 返回一个存储用户信息的list集合
     */
    private List<String> getUserToStrings(Map usermap) {

        usermap = userMain.getUserMap();
        Set<String> userSet = usermap.keySet();
        List<String> userList = new ArrayList<>(usermap.size() - 1);
        String s = "#用户名\t用户昵称\t密码\t用户类型";
        userList.add(s);
        for (String str : userSet) {
            UserBean userBean1 = userMain.getUserMap().get(str);
            String user = userBean1.getId() + "\t" + userBean1.getUserName() + "\t" + userBean1.getPassword() + "\t" + userBean1.getType();
            userList.add(user);
        }
        return userList;
    }

    /**
     * 删除用户信息(服务端)
     *
     * @param input 输入流
     * @param out   输出流
     * @return 如果删除成功，返回true；否则，返回false
     */
    private void delUser(BufferedReader input, PrintWriter out) {

        String userName = null;
        //先读取用户文件内容
        try {
            userName = input.readLine();
            List<String> fileContext = FileUtilImplements.readFile(UserMain.userPath);//读取文件并将其内容返回存储在list集合中
            //删除磁盘,先遍历文件内容集合，然后匹配并记录该index，最后删除该index项
            if (userName != null) {
                Iterator<String> iterator = fileContext.iterator();
                iterator.next();
                while(iterator.hasNext()){
                    String str = iterator.next();
                    String[] arr = str.split("\t");
                    if (userName.equals(str.substring(0, arr[0].length()))){
                        iterator.remove();
                    }
                }
                boolean flag = FileUtilImplements.writeFile(UserMain.userPath,fileContext,false);
                if (flag) {
                    //删除内存
                    userMain.getUserMap().remove(userName);//同时删除userMap中对应的用户信息
                    out.println("true");
                    out.flush();
                } else {
                    out.println("false");
                    out.flush();
                }
            }
        } catch (IOException e) {
            out.println("false");
            out.flush();
            e.printStackTrace();
        }
    }

    /**
     * 读取所有的用户名,发送所有用户名到客户端
     *
     * @param out 输出流
     * @return 如果读取成功，返回true，否则返回false
     */
    private void readAllUserName(PrintWriter out) {
        //此处信息从内存读取，目的是为了磁盘读取一次，然后加载到内存中，方便以后所有的业务都从内存中获取
        //1.获取所有的Map的key，即所有的用户名集合
        Set<String> usernamelist = userMain.getUserMap().keySet();
        //2.遍历，将所有用户名组装一个字符串
        StringBuffer usernames = new StringBuffer();//此处最好选择使用StringBuffer，因为字符串长度在每次遍历时都会发生改变
        for (String tempStr : usernamelist) {
            usernames.append(tempStr + "\t");
        }
        //3.将所有用户名发送给客户端（注意字符串尾部内容，如果需要，则截取）
        out.println(usernames.substring(0, usernames.length() - 1));
        out.flush();
    }

    /**
     * 持久化新用户注册信息到磁盘中
     *
     * @param input 输入流
     * @param out   输出流
     * @throws IOException
     */
    private void saveNewUser(BufferedReader input, PrintWriter out) throws IOException {

        //1.首先读取客户端用户信息
        String message = input.readLine();
        List<String> list = FileUtilImplements.readFile(UserMain.userPath);
        //2.然后写入磁盘
        list.add(message + "\t" + "user");//将用户类型添加到list集合中
        boolean flag = FileUtilImplements.writeFile(UserMain.userPath, list, false);
        if (flag) {
            //3.将新注册的用户信息添加到注册信息Map中或重新加载用户信息
            String[] arr = message.split("\t");
            UserBean userBean = new UserBean(arr[0], arr[1], arr[2], "user");
            userMain.getUserMap().put(arr[0], userBean);
            //如果保存持久化成功，向客户端发送确认信息
            out.println("true");
        } else {
            out.println("false");
        }
        out.flush();
    }

    /**
     * 用户登录功能
     *
     * @param input 输入流
     * @param out   输出流
     * @throws IOException
     */
    private void userLogin(BufferedReader input, PrintWriter out) throws IOException {
        String returnValue;
        //1.接收客户端发送过来的信息
        String message = input.readLine();
        String[] userMessage = message.split("\t");
        //2.根据key(用户名)来获取value（UserBean对象），并从内存中（userMap）读取用户名和密码
        UserBean userBean = userMain.getUserMap().get(userMessage[0]);
        //3.将获取的用户名和密码分别和UserBean中用户名和密码进行对比，如果校验成功，返回Success+getType,否则返回error
        if (userBean == null) {
            returnValue = null;
        } else if (!userMessage[0].equals(userBean.getId())) {
            returnValue = "ERROR";
        } else if (!userMessage[1].equals(userBean.getPassword())) {
            returnValue = "ERROR";
        } else {
            returnValue = "SUCCESS" + "\t" + userBean.getType();
        }
        //将校验结果返送给客户端
        out.println(returnValue);
        out.flush();
    }

    /**
     * 保存投票信息,以及创建投票详情文件
     *
     * @param input 输入流
     * @param out   输出流
     * @throws IOException
     */
    private void saveVote(BufferedReader input, PrintWriter out) throws IOException {

        int number = voteMain.getMaxVoteNumber() + 1;//最大投票票数
        String numberStr = String.format("%06d", number);//格式化编号
        String voteMessage = input.readLine();//读取从客户端传过来的投票信息
        String voteOptions = input.readLine();//读取从客户端传过来的投票选项信息
        List<String> saveContext = new ArrayList<>();
        //设置投票信息
        setVote(voteMessage, saveContext, numberStr);
        String[] options = voteOptions.split("\t");
        int cnt = 1;
        for (String option : options) {
            saveContext.add("#选项" + cnt + "\t票数");
            saveContext.add(option + "\t0");
            cnt++;
        }

        String fullvotePath = VoteMain.VOTEPATH + "\\" + numberStr + ".txt";
        String fullVoteDetailPath = VoteMain.VOTEDETAILPATH + "\\" + numberStr + ".txt";
        boolean flag1 = FileUtilImplements.writeFile(fullvotePath, saveContext, false);
        String detail = "#用户昵称\t投票选项\t投票时间";
        boolean flag2 = FileUtilImplements.writeFile(fullVoteDetailPath, detail, false);

        if (flag1 && flag2) {
            voteMain.setMaxVoteNumber(number);
            addVoteToMap(saveContext);
            out.println("true");
        } else {
            out.println("false");
        }
        out.flush();
    }

    /**
     * 组装投票信息集合
     *
     * @param voteMessage 投票信息
     * @param saveContext 组装好的信息集合
     */
    private void setVote(String voteMessage, List<String> saveContext, String numberStr) {

        saveContext.add("#投票编号");
        saveContext.add(numberStr);
        String[] arr = voteMessage.split("\t");
        saveContext.add("#投票主题");
        saveContext.add(arr[0]);
        saveContext.add("#选项类型");
        if ("1".equals(arr[1])) {
            saveContext.add("用户可添加");
        } else {
            saveContext.add("用户不可添加");
        }
        saveContext.add("#结束类型");
        if ("1".equals(arr[2])) {
            saveContext.add("计时结束");
        } else if ("2".equals(arr[2])) {
            saveContext.add("手动结束");
        } else {
            saveContext.add("百分比结束");
        }
        saveContext.add("#结束标识");
        saveContext.add("未结束");
        saveContext.add("#匿名标识");
        if ("1".equals(arr[4])) {
            saveContext.add("匿名");
        } else {
            saveContext.add("不匿名");
        }
        saveContext.add("#结束参数");
        saveContext.add(arr[3]);
        saveContext.add("#创建投票人账户");
        saveContext.add(arr[5]);
        saveContext.add("#开始时间");
        saveContext.add(String.valueOf(new Date().getTime()));
        saveContext.add("#结束时间");
        saveContext.add("0");
    }

    /**
     * 保存信息到内存
     *
     * @param saveContext 要保存的信息
     */
    private void addVoteToMap(List<String> saveContext) {

        VoteBean voteBean = new VoteBean(saveContext.get(1), saveContext.get(3), saveContext.get(5), saveContext.get(7),
                saveContext.get(9), saveContext.get(11), saveContext.get(13), saveContext.get(15), saveContext.get(17), saveContext.get(19));

        List<String> options = new ArrayList<>();
        for (int cnt = 21; cnt < saveContext.size(); cnt += 2) {
            options.add(saveContext.get(cnt));
        }
        //将用户的投票选项信息设置到投票实体类中
        voteBean.setOptions(options);
        //将更新后的投票实体类添加到Map集合中
        voteMain.getVoteMap().put(saveContext.get(1), voteBean);
    }

    /**
     * 发送投票信息给客户端
     * 思路：获取存储投票信息的Map，然后遍历这个Map，得到VoteBean对象，进而得到相关信息并组装成字符串一起发送给服务器
     *
     * @param input 输入流
     * @param out   输出流
     */
    private void sendAllVoteMessage(BufferedReader input, PrintWriter out) {

        Map<String, VoteBean> map = voteMain.getVoteMap();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            VoteBean voteBean = map.get(key);
            out.println(voteBean.getId() + "\t" + voteBean.getCreateId() + "\t" + voteBean.getStartTime().getTime() + "\t" + voteBean.getTitle() + "\t" + voteBean.isEndFlag() + "\t" + voteBean.isAnonyMousFlag() + "\t" + voteBean.getChangeType());
            out.flush();
        }
        out.println("end");//发送截止信息
        out.flush();
    }

    /**
     * 发送明细投票信息给客户端
     *
     * @param input
     * @param out
     */
    private void sendVoteDetails(BufferedReader input, PrintWriter out) {

        String number = null;
        try {
            number = input.readLine();//接收对应编号的明细投票信息
            VoteBean voteBean = voteMain.getVoteMap().get(number);
            if (voteBean != null) {
                for (VoteDetailBean bean : voteBean.getvoteDetailsList()) {
                    out.println(bean.getUserId() + "\t" + bean.getChangeNumber() + "\t" + bean.getVoteTime().getTime());
                    out.flush();
                }
            }
            out.println("end");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束投票(服务端)
     *
     * @param input 输入流
     * @param out   输出流
     */
    private void endVote(BufferedReader input, PrintWriter out) {

        //先将修改后的内容保存在内存中
        String number = null;
        try {
            number = input.readLine();
            //修改磁盘中的数据，先读，再修改，最后再写入磁盘
            String fullPath = VoteMain.VOTEPATH + "\\" + number + ".txt";
            List<String> fileList = FileUtilImplements.readFile(fullPath);
            fileList.remove(9);
            fileList.add(9, "已结束");
            fileList.remove(15);
            fileList.add(15, String.valueOf(new Date().toString()));
            boolean flag = FileUtilImplements.writeFile(fullPath, fileList, false);
            if (flag) {
                out.println("true");
                VoteBean voteBean = voteMain.getVoteMap().get(number);
                voteBean.setEndFlag(true);
            } else {
                out.println("false");
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除投票(服务端)
     *
     * @param input 输入流
     * @param out   输出流
     */
    private void delVote(BufferedReader input, PrintWriter out) {

        String number = null;
        try {
            number = input.readLine();
            String fullPath = VoteMain.VOTEPATH + "\\" + number + ".txt";
            boolean flag = FileUtilImplements.delFile(fullPath);
            System.out.println("删除的投票信息文件：" + fullPath);
            if (flag) {
                fullPath = VoteMain.VOTEDETAILPATH + File.separator + number + ".txt";
                System.out.println("删除的明细投票文件为：" + fullPath);
                FileUtilImplements.delFile(fullPath);
                out.println("true");
                voteMain.getVoteMap().remove(number);
            } else {
                out.println("false");
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送投票选项
     * 思路：首先从客户端那获取用户想要的投票选项对应的编号，服务器由此遍历存储投票信息的集合，并根据key得到voteBean对象，成功后回传给客户端
     *
     * @param input
     * @param out
     */
    private void sendVoteOptions(BufferedReader input, PrintWriter out) {
        try {
            //获取从客户端传来的投票编号
            String number = input.readLine();
            VoteBean voteBean = voteMain.getVoteMap().get(number);
            for (String str : voteBean.getOptions()) {
                out.println(str);
                out.flush();
            }
            //发送截止信息
            out.println("end");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存投票选项,至少两件事 保存到内存，保存到文件
     *
     * @param input 输入流
     * @param out   输出流
     */
    private void saveVoteDetail(BufferedReader input, PrintWriter out) {

        try {
            String number = input.readLine();//获取编号
            String cnt = input.readLine();
            String userid = input.readLine();//用户id
            VoteBean voteBean = voteMain.getVoteMap().get(number);
            List<String> saveContext = new ArrayList<>();
            int optionCnt = setVote(voteBean, saveContext, number, Integer.valueOf(cnt));
            String fullPath = VoteMain.VOTEPATH + File.separator + number + ".txt";
            boolean flag = FileUtilImplements.writeFile(fullPath, saveContext, false);
            if (flag) {
                String message = userid + "\t" + cnt + "\t" + new Date().getTime();
                fullPath = VoteMain.VOTEDETAILPATH + File.separator + number + ".txt";
                //写到详情文件中
                flag = FileUtilImplements.writeFile(fullPath, message, true);
            }
            if (flag) {
                String tempStr = voteBean.getOptions().get(optionCnt);
                String[] arr = tempStr.split("\t");
                tempStr = arr[0] + "\t" + (Integer.valueOf(arr[1]) + 1);
                voteMain.getVoteMap().get(number).getOptions().remove(optionCnt);
                voteMain.getVoteMap().get(number).getOptions().add(optionCnt, tempStr);
                voteMain.getVoteMap().get(number).getvoteDetailsList().add(new VoteDetailBean(userid, String.valueOf(cnt), String.valueOf(new Date().getTime())));
                out.println("true");
            } else {
                out.println("fasle");
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 组装投票信息集合
     *
     * @param voteBean    投票信息
     * @param saveContext 组装好的信息集合
     * @param numberStr   最大投票信息编号
     * @param changeCnt   用户选项
     * @return int          被改变的选项下标
     */
    private int setVote(VoteBean voteBean, List<String> saveContext, String numberStr, int changeCnt) {

        int optionCnt = 0;
        saveContext.add("#投票编号");
        saveContext.add(numberStr);
        saveContext.add("投票主题");
        saveContext.add(voteBean.getTitle());
        saveContext.add("选项类型");
        saveContext.add(voteBean.getChangeType());
        saveContext.add("结束类型");
        saveContext.add(voteBean.getEndType());
        saveContext.add("结束标识");
        if (voteBean.isEndFlag()) {
            saveContext.add("已结束");
        } else {
            saveContext.add("未结束");
        }
        saveContext.add("#匿名标识");
        if (voteBean.isAnonyMousFlag()) {
            saveContext.add("匿名");
        } else {
            saveContext.add("不匿名");
        }
        saveContext.add("#结束参数");
        saveContext.add(String.valueOf(voteBean.getEndParam()));
        saveContext.add("#创建者账户");
        saveContext.add(voteBean.getCreateId());
        saveContext.add("#开始时间");
        saveContext.add(String.valueOf(voteBean.getStartTime().getTime()));
        saveContext.add("#结束时间");
        saveContext.add(String.valueOf(voteBean.getEndTime().getTime()));

        int cnt = 1;
        //遍历投票选项
        for (String str : voteBean.getOptions()) {
            if (changeCnt == cnt) {
                String[] arr = str.split("\t");
                int tempCnt = Integer.valueOf(arr[1]) + 1;
                saveContext.add("#选项" + cnt);
                saveContext.add(arr[0] + "\t" + tempCnt);
                optionCnt = cnt - 1;
            } else {
                saveContext.add("#选项" + cnt);
                saveContext.add(str);
            }
            cnt++;
        }
        return optionCnt;
    }

    /**
     * 获取所有投票明细的用户ID
     *
     * @param input 输入流
     * @param out   输出流
     */
    private void getVoteDetailUserId(BufferedReader input, PrintWriter out) {

        String number = null;
        try {
            number = input.readLine();
            VoteBean voteBean = voteMain.getVoteMap().get(number);
            //遍历投票明细列表
            for (VoteDetailBean bean : voteBean.getvoteDetailsList()) {
                out.println(bean.getUserId());
                out.flush();
            }
            out.println("end");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加选项
     *
     * @param input 输入流
     * @param out   输出流
     */
    private void addOption(BufferedReader input, PrintWriter out) {

        String number = null;
        try {
            number = input.readLine();
            String option = input.readLine();
            String fullPath = VoteMain.VOTEPATH + File.separator + number + ".txt";
            boolean flag = FileUtilImplements.writeFile(fullPath, "#选项" + (voteMain.getVoteMap().get(number).getOptions().size() + 1) + "\t票数", true);
            if (flag) {
                flag = FileUtilImplements.writeFile(fullPath, option + "\t0", true);
            }
            if (flag) {
                voteMain.getVoteMap().get(number).getOptions().add(option + "\t0");
                out.println("true");
            } else {
                out.println("false");
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}