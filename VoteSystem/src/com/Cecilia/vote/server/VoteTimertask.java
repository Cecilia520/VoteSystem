package com.Cecilia.vote.server;

import com.Cecilia.vote.bean.VoteBean;
import com.Cecilia.vote.util.FileUtilImplements;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

/**
 * 自动定时设置结束投票
 * Created by Cecilia on 2017/8/4.
 */
public class VoteTimertask extends TimerTask{

    private VoteMain voteMain;//投票主类

    public VoteTimertask(VoteMain voteMain){

        this.voteMain = voteMain;
    }

    @Override
    public void run() {
        System.out.println("投票结束例行检查中...");
        Set<String> keys = voteMain.getVoteMap().keySet();
        for (String key:keys){
            System.out.println("cc"+"\t"+key);
            VoteBean voteBean = voteMain.getVoteMap().get(key);
            if ("计时结束".equals(voteBean.getEndType())
                    &&!voteBean.isEndFlag()){
                Double d = new Double(voteBean.getEndParam());
                Date date = new Date();
                if (d.longValue()<=date.getTime()){
                String path = VoteMain.VOTEPATH+"\\"+key+".txt";
                    try {
                        List<String> list = FileUtilImplements.readFile(path);
                        list.remove(9);
                        list.add(9,"已结束");
                        list.remove(15);
                        list.add(15,String.valueOf(date.getTime()));
                        FileUtilImplements.writeFile(path,list,false);
                        voteBean.setEndFlag(true);
                        voteBean.setEndTime(date);
                    } catch (IOException e) {
                        System.out.println("计时任务停止失败"+key);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
