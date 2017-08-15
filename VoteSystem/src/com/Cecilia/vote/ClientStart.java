package com.Cecilia.vote;

import com.Cecilia.vote.client.ClientMain;
import com.Cecilia.vote.server.UserMain;
import com.Cecilia.vote.server.VoteMain;

/**
 * 启动客户端主类
 * Created by Cecilia on 2017/8/1.
 */
public class ClientStart {

    public final static String BASEPATH = "E:\\log\\vote\\";

    public static void main(String[] args) {

        ClientMain clientMain = new ClientMain();
        boolean flag = clientMain.startClient();
        if (!flag) {
            System.out.println("服务器连接失败！");
            return;
        }

        //服务器连接成功后，开始进入登录注册功能
        UserMain userMain = new UserMain();
        if (!userMain.userResponse(clientMain.getClient(), clientMain.getUserBean())) {
            return;
        }
        //投票管理
        VoteMain voteMain = new VoteMain();
        clientMain.userFuntion(voteMain);
    }
}
