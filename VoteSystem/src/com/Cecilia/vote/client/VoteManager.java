package com.Cecilia.vote.client;

import com.Cecilia.vote.bean.VoteBean;
import com.Cecilia.vote.server.ServerThread;
import com.Cecilia.vote.server.VoteMain;
import com.Cecilia.vote.util.FileUtilImplements;
import com.Cecilia.vote.util.SocketUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.sun.org.glassfish.external.statistics.annotations.Reset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 投票管理
 * Created by Cecilia on 2017/8/4.
 */
public class VoteManager {

    private Socket client;//已打开的客户端连接

    private VoteMain voteMain;

    public VoteManager() {
        voteMain = new VoteMain();
    }

    /**
     * 投票管理主函数
     *
     * @param sc     输入流
     * @param client 已打开的客户端连接
     * @param userid 用户编码
     */
    public void managerChange(Scanner sc, Socket client, String userid) {

        while (true) {
            System.out.println("请选择您需要的功能： 1：发起投票 2：查看投票 3：结束投票 4：删除投票 5：返回上一层");
            String message = sc.nextLine();
            if ("1".equals(message)) {
                //发起投票
                createVote(sc, client, userid);
            } else if ("2".equals(message)) {
                //查看投票
                searchVote(sc, client);
            } else if ("3".equals(message)) {
                searchVote(sc, client);
            } else if ("4".equals(message)) {
                searchVote(sc, client);
            } else if ("5".equals(message)) {
                break;
            } else {
                System.out.println("对不起，您的操作错误，请重新选择！");
            }
        }
    }

    /**
     * 用户投票主函数
     *
     * @param client 已打开的客户端连接
     * @param sc     输入流
     * @param userid 登录用户id
     */
    public void voting(Socket client, Scanner sc, String userid) {
        while (true) {
            System.out.println("请选择您需要的功能操作：1：发起投票   2：查看分类投票信息   3：返回上一层");
            String message = sc.nextLine();
            if ("1".equals(message)) {
                createVote(sc, client, userid);
            } else if ("2".equals(message)) {
                classfiySearchVote(sc, client, userid);
            } else if ("3".equals(message)) {
                break;
            } else {
                System.out.println("对不起，您的操作有误，请重新选择！");
            }
        }
    }

    /**
     * 发起投票
     *
     * @param sc     输入流
     * @param client 已打开的客户端连接
     * @param userid 用户的编号
     */
    private void createVote(Scanner sc, Socket client, String userid) {
        while (true) {
            System.out.println("请输入投票主题：");
            String title = sc.nextLine().trim();
            if (title.length() < 1 || title.indexOf(" ") > -1) {
                System.out.println("输入非法！");
                continue;
            }
            System.out.println("请选择选项类型： 1：用户可添加选项 2：用户不能添加选项");
            String optionalType = sc.nextLine().trim();
            if (!"1".equals(optionalType) && !"2".equals(optionalType)) {
                System.out.println("对不起，您的操作有误！");
                continue;
            }
            System.out.println("请选择结束类型：1：计时结束 2：百分比结束 3：手动结束");
            String endType = sc.nextLine().trim();
            if (!"1".equals(endType) && !"2".equals(endType) && !"3".equals(endType)) {
                System.out.println("对不起，您的操作有误！");
                continue;
            }
            String endParam = saveEndType(endType, sc);//设置结束参数

            System.out.println("请问是否匿名：1:匿名 2：不匿名");
            String anonymousType = sc.nextLine().trim();
            if (!"1".equals(anonymousType) && !"2".equals(anonymousType)) {
                System.out.println("对不起，您的操作有误！");
                continue;
            }
            List<String> options = addOptions(sc);
            //发送投票信息给服务器
            boolean flag = sendVoteServer(title + "\t" + optionalType + "\t" + endType + "\t" + endParam + "\t" + anonymousType + "\t" + userid, options, client);
            if (flag) {
                System.out.println("发送投票信息给服务器保存成功！");
            } else {
                System.out.println("发送投票信息给服务器保存失败！");
            }
            break;
        }
    }

    /**
     * 添加选项
     *
     * @param sc 用户输入流
     * @return 用户添加的选项
     */
    private List<String> addOptions(Scanner sc) {

        System.out.println("请添加选项，选项中不能有空格，直接回车则结束！");
        int cnt = 1;
        List<String> options = new ArrayList<>(10);
        while (true) {
            System.out.println("选项" + cnt);
            String tempOption = sc.nextLine().trim();
            if (tempOption.length() < 1) {
                //如果跳出之前，用户什么都没输入，应该提示用户至少有一个选项
                if (cnt == 1) {
                    System.out.println("至少有一个选项！");
                } else {
                    break;
                }
            } else if (tempOption.indexOf(" ") > -1) {
                System.out.println("选项输入非法！");
            } else {
                cnt++;
                options.add(tempOption);
            }
        }
        return options;
    }

    /**
     * 设置结束的参数
     *
     * @param type 结束类型
     * @param sc   输入流
     * @return 结束的参数
     */
    private String saveEndType(String type, Scanner sc) {
        String returnValue = "0";
        if ("1".equals(type)) {
            while (true) {
                System.out.println("请输入结束时间(yyyy-MM-dd HH:mm:ss)：");
                String dateStr = sc.nextLine().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = sdf.parse(dateStr);
                    if ((date.getTime()) - new Date().getTime() < 1000 * 60 * 60) {
                        System.out.println("不能早于当前日期！");
                    } else {
                        returnValue = String.valueOf(date.getTime());
                        break;
                    }
                } catch (ParseException e) {
                    System.out.println("日期格式错误，请重新输入！");
                    e.printStackTrace();
                }
            }
        } else if ("2".equals(type)) {
            //按百分比结束
            while (true) {
                System.out.println("请输入百分比(0.1-1)：");
                if (sc.hasNextDouble()) {
                    double d = sc.nextDouble();
                    sc.nextLine();
                    if (d < 0.1 || d > 1) {
                        System.out.println("对不起，您输入的百分比超出范围.");
                    } else {
                        returnValue = String.valueOf(d);
                        break;
                    }
                } else {
                    System.out.println("请输入一个浮点型数字！");
                }
            }
        }
        return returnValue;
    }

    /**
     * 发送选项给服务端
     *
     * @param message 投票信息主体
     * @param options 投票选项
     * @param client  已打开的客户端
     * @return 如果发送成功，返回true；否则，返回false
     */
    private boolean sendVoteServer(String message, List<String> options, Socket client) {

        StringBuffer sb = new StringBuffer();
        for (String option : options) {
            sb.append(option + "\t");
        }
        Object[] objects = SocketUtil.getOutInAndCheckCode(client);
        if (objects == null) {
            return false;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        out.println("编号4");
        out.flush();
        out.println(message);
        out.println(sb.substring(0, sb.length() - 1));
        out.flush();
        boolean flag = true;
        try {
            message = input.readLine();
            if ("false".equals(message)) {
                flag = false;
            }
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查看投票信息，以及查看对应的投票明细(管理员操作)
     * 注意：此处是针对客户端，但是voteMap是在内存，保存在服务器，不能直接在客户端对Map进行操作
     *
     * @param client 已打开的客户端连接
     * @param sc     输入流
     */
    private void searchVote(Scanner sc, Socket client) {

        Map<String, VoteBean> voteBeanMap = new HashMap<>();
        List<String> voteDetailsList = new ArrayList<>();//明细列表集合
        List<String> endVoteList = new ArrayList<>();//已结束的投票信息
        List<String> votingList = new ArrayList<>(); //正在进行中的投票信息
        List<String> list = new ArrayList<>();
        //获取所有投票信息
        getAllVote(client, endVoteList, votingList, voteBeanMap);
        list.addAll(votingList);
        list.addAll(endVoteList);
        Collections.sort(list, comparator());
        System.out.println("---------------所有投票信息如下：-----------------");
        System.out.println("投票编号" + "\t" + "创建投票者" + "\t" + "开始时间" + "\t" + "\t" + "投票主题" + "\t" + "是否结束" + "\t" + "是否匿名" + "\t" + "添加选项 ");
        for (String str : list) {
            System.out.println(str);
        }
        while (true) {
            System.out.println("请选择您想要的功能操作：1：查看明细  2：结束投票   3：删除投票   4：返回上一层");
            String message = sc.nextLine();
            if ("1".equals(message)) {
                System.out.println("请选择编号：");
                String number = sc.nextLine();
                if (voteBeanMap.get(number) == null) {
                    System.out.println("对不起，您输入的编号错误！");
                    continue;
                }
                //获取投票明细
                getVoteDetailMessage(client, voteDetailsList, number);
                System.out.println("--------------投票编号为" + number + "的明细信息如下：--------------");
                System.out.println("用户  id  选项  投票时间");

                for (String str : voteDetailsList) {
                    System.out.println(str);
                }
            } else if ("2".equals(message)) {
                System.out.println("请选择编号：");
                String number = sc.nextLine();
                //结束投票
                endVote(client, voteBeanMap.get(number));
            } else if ("3".equals(message)) {
                //删除投票
                System.out.println("请选择编号：");
                String number = sc.nextLine();
                delVote(client, voteBeanMap.get(number));
            } else if ("4".equals(message)) {
                //返回上一层
                break;
            } else {
                System.out.println("功能选择错误！");
            }
        }
    }

    /**
     * 从服务器获取所有的投票信息
     *
     * @param client      已打开的客户端
     * @param votingList  正在进行中的投票集合
     * @param voteBeanMap 组装后的投票信息Map集合
     * @param endVoteList 已结束的投票信息集合
     */
    private void getAllVote(Socket client, List<String> endVoteList, List<String> votingList, Map<String, VoteBean> voteBeanMap) {

        endVoteList.clear();//在每一次进行获取所有信息之前都将已结束的投票信息清空
        if (votingList != null) {
            votingList.clear();
        }
        voteBeanMap.clear();
        Object[] objects = SocketUtil.getOutInAndCheckCode(client);
        if (objects == null) {
            return;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        out.println("编号5");
        out.flush();
        String message = null;//在功能不说有影响的条件下，JVM对外面定义的变量进行共用，而在里面定义，会每次都创建一个String，性能降低
        //为了程序安全，此处try-catch最好放在while的外面
        try {
            while (true) {
                message = input.readLine();
                if ("end".equals(message)) {
                    break;
                }
                String[] arr = message.split("\t");
                if (votingList != null) {
                    if ("true".equals(arr[4])) {
                        //如果当前投票显示结束时间已到，那么将其添加到已结束的投票列表中
                        endVoteList.add(conversionMessage(arr));
                    } else {
                        //如果当前投票表示正在进行，那么将其加入到正在进行的投票列表中
                        votingList.add(conversionMessage(arr));
                    }
                }
                voteBeanMap.put(arr[0], new VoteBean(arr[0], arr[1], arr[3], arr[4], arr[5], arr[6]));
            }
        } catch (IOException e) {
            endVoteList.clear();
            if (votingList != null) {
                votingList.clear();
            }
            voteBeanMap.clear();
            e.printStackTrace();
        }
    }

    /**
     * 格式化投票信息
     *
     * @param arr 要输出的信息数组
     * @return 格式化后的信息
     */
    private String conversionMessage(String[] arr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuffer sb = new StringBuffer();
        sb.append(arr[0] + "\t");
        sb.append(arr[1] + "\t");
        sb.append(sdf.format(new Date(Long.valueOf(arr[2]))) + "\t");
        sb.append(arr[3] + "\t");
        if ("true".equals(arr[4])) {
            sb.append("已结束\t");
        } else {
            sb.append("未结束\t");
        }
        if ("true".equals(arr[5])) {
            sb.append("匿名\t");
        } else {
            sb.append("不匿名\t");
        }
        sb.append(arr[6]);
        return sb.toString();
    }

    /**
     * 从服务器获取一个投票明细信息
     *
     * @param client          已打开的客户端连接
     * @param voteDetailsList 投票明细信息集合
     * @param voteId          投票编号
     */
    private void getVoteDetailMessage(Socket client, List<String> voteDetailsList, String voteId) {

        voteDetailsList.clear();//此处避免每次查看完后信息不断地累加，因此在每次查看完一个明细后，都需要将其清空
        Object[] objects = SocketUtil.getOutInAndCheckCode(client);
        if (objects == null) {
            return;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        out.println("编号6");//通知服务器
        out.flush();
        out.println(voteId);//向服务器发送想要查看的明细对应的编号
        out.flush();
        //接收服务器传过来的信息
        String message = null;
        try {
            while (true) {
                message = input.readLine();
                if ("end".equals(message)) {
                    break;
                }
                voteDetailsList.add(conversionVotetails(message));
            }
        } catch (IOException e) {
            //如果出现异常，也需要清空
            voteDetailsList.clear();
            e.printStackTrace();
        }
    }

    /**
     * 格式化明细信息
     *
     * @param message 需要格式化的明细信息
     * @return 格式化后的明细信息
     */
    private String conversionVotetails(String message) {
        String[] arr = message.split("\t");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return arr[0] + "\t" + arr[1] + "\t" + sdf.format(new Date(Long.valueOf(arr[2])));
    }

    /**
     * 发送结束信息到服务端
     *
     * @param client   已打开的客户端连接
     * @param voteBean 要结束的投票信息
     */
    private void endVote(Socket client, VoteBean voteBean) {
        //检测当前投票是否已结束
        if (voteBean.isEndFlag()) {
            System.out.println("对不起，您想要结束的投票已结束！");
        } else {
            Object[] objects = SocketUtil.getOutInAndCheckCode(client);
            if (objects == null) {
                return;
            }
            PrintWriter out = (PrintWriter) objects[0];
            BufferedReader input = (BufferedReader) objects[1];
            //通知服务端
            out.println("编号7");
            out.flush();
            //给服务器发送请求投票编号
            out.println(voteBean.getId());
            out.flush();
            //接收服务端信息
            try {
                String message = input.readLine();
                if ("true".equals(message)) {
                    System.out.println("结束当前投票成功！");
                } else {
                    System.out.println("结束当前投票失败！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送删除投票信息到服务器
     *
     * @param client   已打开的客户端连接
     * @param voteBean 要删除的投票信息
     */
    private void delVote(Socket client, VoteBean voteBean) {
        Object[] objects = SocketUtil.getOutInAndCheckCode(client);
        if (objects == null) {
            return;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        //客户端发送请求
        out.println("编号8");
        out.flush();
        out.println(voteBean.getId());
        out.flush();
        try {
            //接收服务器的信息
            String number = input.readLine();
            if ("true".equals(number)) {
                System.out.println("删除成功！");
            } else {
                System.out.println("删除失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分类查看投票（用户功能操作）
     * 思路：投票一般按照是否结束的标准来进行分类提供给用户查看，因此采用两个list集合分别存储，再依次遍历
     *
     * @param sc     用户输入流
     * @param client 已打开的客户端
     * @param userid 用户id
     */
    private void classfiySearchVote(Scanner sc, Socket client, String userid) {

        List<String> endVoteList = new ArrayList<>();//已结束的投票信息
        List<String> votingList = new ArrayList<>(); //正在进行中的投票信息
        Map<String, VoteBean> tempVoteMap = new HashMap<>();
        List<String> voteDetailsList = new ArrayList<>();//投票详情集合
        while (true) {
            //先获取所有投票信息集合
            getAllVote(client, endVoteList, votingList, tempVoteMap);
            System.out.println("请选择您想查看投票信息的状态：1：投票中  2：已结束");
            String message = sc.nextLine();
            if ("1".equals(message)) {
                System.out.println("----------------进行中的投票信息------------------");
                System.out.println("投票编号" + "\t" + "发起人" + "\t" + " 开始时间 " + "\t" + " 投票主题 " + "\t" + "是否结束" + "\t" + " 是否匿名 " + "\t" + "添加选项");

                Collections.sort(votingList, comparator());

                for (String str : votingList) {
                    System.out.println(str);
                }
            } else {
                System.out.println("-----------------已结束的投票信息-------------------------");
                System.out.println("投票编号" + "\t" + "发起人" + "\t" + " 开始时间 " + "\t" + " 投票主题 " + "\t" + "是否结束" + "\t" + " 是否匿名 " + "\t" + "添加选项");
                Collections.sort(endVoteList, comparator());
                for (String str : endVoteList) {
                    System.out.println(str);
                }
            }
            System.out.println("请选择您需要的功能操作：1：进行投票   2：查看明细  3：添加选项    4.返回上一层");
            message = sc.nextLine();
            if ("4".equals(message)) {
                break;
            }
            System.out.println("请输入编号：");
            String number = sc.nextLine();
            //检测用户名是否重复
            if (!checkUserChange(tempVoteMap, number, message)) {
                continue;
            }
            if ("1".equals(message)) {
                //用户投票
                oneVoting(sc, client, number, userid);
            } else if ("2".equals(message)) {
                //获取投票明细信息
                getVoteDetailMessage(client, voteDetailsList, number);
                System.out.println("-------------投票编号" + number + "明细信息如下：----------");
                for (String str : voteDetailsList) {
                    System.out.println(str);
                }
            } else if ("3".equals(message)) {
                //添加新选项
                addOptions(client, number, sc, tempVoteMap.get(number).getChangeType());
            } else {
                System.out.println("功能选择错误！");
            }
        }
    }


    /**
     * 检查用户选择是否合法
     *
     * @param tempVoteMap 投票信息集合
     * @param number      要操作的编号
     * @param message     用户选择
     * @return 如果合法，返回true，否则返回false
     */
    private boolean checkUserChange(Map<String, VoteBean> tempVoteMap, String number, String message) {
        if (tempVoteMap.get(number) == null) {
            System.out.println("编号输入错误！");
//            return false;
        }
        if ("1".equals(message) || "3".equals(message)) {
            //检测当前投票是否已经结束
            if (tempVoteMap.get(number).isEndFlag()) {
                System.out.println("目前投票已经结束，不能对已经结束的投票进行这些操作！");
                return false;
            }
        }
        //检测当前投票是否会匿名
        if ("2".equals(message) && tempVoteMap.get(number).isAnonyMousFlag()) {
            System.out.println("目前处于匿名状态，是不能查看投票的明细！");
            return false;
        }
        return true;
    }

    /**
     * 用户投票
     *
     * @param sc     用户输入流
     * @param client 已打开的客户端
     * @param number 要操作的投票编号
     * @param userid 用户id
     */
    private void oneVoting(Scanner sc, Socket client, String number, String userid) {

        //检测当前用户是否已经投过该投票
        if (!checkRepeat(client, userid, number)) {
            System.out.println("对不起，您已经投过了，请勿再次投票！");
            return;
        }

        int optionCnt = printVoteOptations(client, number);

        if (optionCnt == 0) {
            return;
        }
        int messageCnt = 0;
        while (true) {
            System.out.println("请选择您想要投的选项（1 ~ " + optionCnt + ")");
            if (!sc.hasNextInt()) {
                System.out.println("对不起，您的操作有误！");
                continue;
            }
            messageCnt = sc.nextInt();
            sc.nextLine();
            if (messageCnt < 1 || messageCnt > optionCnt) {
                System.out.println("对不起，您的操作有误！");
                continue;
            }
            break;
        }
        sendVoteOptionToServer(client, number, messageCnt, userid);
    }

    /**
     * 输出选项信息
     *
     * @param client 已打开的客户端连接
     * @param number 投票编号
     * @return 选项数量
     */
    private int printVoteOptations(Socket client, String number) {

        Object[] objects = SocketUtil.getOutInAndCheckCode(client);
        if (objects == null) {
            return 0;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        out.println("编号9");
        out.flush();

        out.println(number);
        out.flush();

        String str = "";
        int cnt = 0;
        try {
            while (true) {
                str = input.readLine();
                if ("end".equals(str)) {
                    break;
                }
                cnt++;
                System.out.println(str);
            }
        } catch (IOException e) {
            cnt = 0;
            e.printStackTrace();
        }
        return cnt;
    }

    /**
     * 发送投票结果到服务器保存
     *
     * @param client     已打开的客户端
     * @param number     投票编号
     * @param messageCnt 用户选项
     * @param userid     用户id
     */
    private void sendVoteOptionToServer(Socket client, String number, int optionCnt, String userid) {

        Object[] objects = SocketUtil.getOutInAndCheckCode(client);
        if (objects == null) {
            return;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        out.println("编号10");
        out.flush();
        //请求服务器发送对应投票编号的信息
        out.println(number);
        out.flush();
        out.println(optionCnt);
        out.flush();
        out.println(userid);
        out.flush();
        try {
            String str = input.readLine();
            if ("true".equals(str)) {
                System.out.println("投票成功！");
            } else {
                System.out.println("投票失败！");
            }
        } catch (IOException e) {
            System.out.println("投票失败！");
            e.printStackTrace();
        }
    }

    /**
     * 检查投票人是否已经投过票
     *
     * @param client 已打开的客户端连接
     * @param userid 用户编号
     * @param number 要检测的投票编号
     * @return       如果可以投票，那么返回true；否则，返回false
     */
    private boolean checkRepeat(Socket client, String userid, String number) {

        Object[] objects = SocketUtil.getOutInAndCheckCode(client);
        if (objects == null) {
            return false;
        }
        PrintWriter out = (PrintWriter) objects[0];
        BufferedReader input = (BufferedReader) objects[1];
        out.println("编号11");
        out.flush();
        out.println(number);
        out.flush();
        List<String> list = new ArrayList<>();
        String str;
        boolean returnValue = true;//可以投票
        try {
            while (true) {
                //接收服务器传过来的信息
                str = input.readLine();
                if ("end".equals(str)) {
                    break;
                }
                list.add(str);
            }
            if (list.contains(userid)) {
                returnValue = false;
            }
        } catch (IOException e) {
            returnValue = false;
            System.out.println("正在投票的用户名已存在，不能重复投票！");
            e.printStackTrace();
        }
        return returnValue;
    }

    /**
     * 添加选项
     *
     * @param client     已打开的客户端连接
     * @param number     投票编号
     * @param sc         用户输入流
     * @param changeType 需要添加的选项类型
     */
    private void addOptions(Socket client, String number, Scanner sc, String changeType) {

        //检测当前投票是否是可添加选项
        if (!"用户可添加".equals(changeType)) {
            System.out.println("该投票不可添加选项");
            return;
        }
        System.out.println("请输入要添加的选项内容：");
        //输出投票选项
        printVoteOptations(client, number);
        String message = sc.nextLine().trim();
        if (message.length() > 0) {
            Object[] objects = SocketUtil.getOutInAndCheckCode(client);
            if (objects == null) {
                return;
            }
            PrintWriter out = (PrintWriter) objects[0];
            BufferedReader input = (BufferedReader) objects[1];
            out.println("编号12");
            out.flush();
            out.println(number);
            out.flush();
            out.println(message);
            out.flush();
            try {
                message = input.readLine();
                if ("true".equals(message)) {
                    System.out.println("添加成功！");
                } else {
                    System.out.println("添加失败！");
                }
            } catch (IOException e) {
                System.out.println("添加失败！");
                e.printStackTrace();
            }
        }
    }

    /**
     * 排序构造器,根据VoteBean对象的投票编号进行排序
     *
     * @return 排序的构造器对象
     */
    private Comparator<String> comparator() {
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                String[] arr1 = o1.split("\t");
                int number1 = Integer.parseInt(arr1[0]);
                String[] arr2 = o2.split("\t");
                int number2 = Integer.parseInt(arr2[0]);

                return (number1 == number2) ? 0 : (number1 < number2) ? -1 : (1);
            }
        };
        return comparator;
    }
}
