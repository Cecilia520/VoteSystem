package com.Cecilia.vote;

import com.Cecilia.vote.server.ServerMain;
import com.Cecilia.vote.server.UserMain;
import com.Cecilia.vote.server.VoteMain;

/**
 * 服务器启动主类
 * Created by Cecilia on 2017/8/1.
 */
public class ServerStart {
    public final static String BASEPATH = "E:\\log\\vote\\";//基本路径

    public static void main(String[] args) {

        ServerMain serverMain = new ServerMain();
        VoteMain voteMain = new VoteMain();
        boolean flag = serverMain.startServer(voteMain);
        if (!flag) {
            System.out.println("服务器启动失败，退出程序");
            return;
        }
        //服务器启动成功
        System.out.println("服务器启动成功,开始加载信息中...");

        //加载用户信息
        UserMain userMain = new UserMain();
        flag = userMain.loadUserMessage();
        if (!flag) {
            System.out.println("用户信息加载失败！");
        } else {
            System.out.println("用户信息加载成功！");
        }

        //加载投票信息
        flag = voteMain.loadVoteMessage();
        if (!flag) {
            System.out.println("投票信息加载失败！");
        } else {
            System.out.println("投票信息加载成功！");

        }
        //所有信息加载完毕后，开始处理客户端发过来的请求
        serverMain.responseClient(userMain, voteMain);
    }
}
