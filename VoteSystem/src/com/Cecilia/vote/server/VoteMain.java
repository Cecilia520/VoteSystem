package com.Cecilia.vote.server;

import com.Cecilia.vote.bean.VoteBean;
import com.Cecilia.vote.bean.VoteDetailBean;
import com.Cecilia.vote.ServerStart;
import com.Cecilia.vote.util.FileUtilImplements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投票主函数
 * Created by Cecilia on 2017/8/1.
 */
public class VoteMain {

    private Map<String,VoteBean> voteMap;//投票信息集合

    private int maxVoteNumber;//当前最大的投票编号

    public final static String VOTEPATH =ServerStart.BASEPATH+"vote";//投票信息路径

    public final static String VOTEDETAILPATH = ServerStart.BASEPATH+"votedetail";//投票详细信息路径


    /**
     * 初始化参数
     */
    public VoteMain(){

        voteMap = new HashMap<>();
        maxVoteNumber = 0;
    }

    /**
     * 加载已有的投票信息
     * @return 加载成功，返回true，反之，返回false
     */
    public boolean loadVoteMessage() {

        //获取该文件夹下所有文件数目
        int number = FileUtilImplements.getFileNumber(VOTEPATH);
        if (number<1){
            return false;
        }
        //获取文件列表
        File[] files = new File(VOTEPATH).listFiles();
        for (File tempFile:files){
            //判断是否是文件夹
            if (tempFile.isDirectory()){
                continue;
            }
            try {
                //在是文件的前提下，对投票文件进行读取文件内容，并存储在List集合中
                List<String> voteContext = FileUtilImplements.readFile(tempFile);
                if (voteContext==null||voteContext.size()<22){
                    continue;
                }
                //在是文件的前提下，对投票详情文件进行读取其内容，并存储在list集合中
                String fullPath = FileUtilImplements.getFullPath(VOTEDETAILPATH,tempFile.getName());
                //根据文件夹路径和每个文件名构成的全路径来读取投票详情文件内容
                List<String> voteDetailContext = FileUtilImplements.readFile(fullPath);
                //将两个集合内容组装成voteBean对象加到Map集合中
                voteMap.put(voteContext.get(1),getVoteBean(voteContext,voteDetailContext));

                //此处注意删除投票时，最大投票编号也需要发生改变
                int cntNumber = Integer.valueOf(voteContext.get(1));
                if (cntNumber>maxVoteNumber){
                    maxVoteNumber = cntNumber;
                }
            } catch (IOException e) {
                System.out.println(tempFile.getName()+"文件加载异常！");
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 通过读取的两个文件内容集合组装成一个VoteBean对象
     * @param voteContext  投票选项文件内容集合
     * @param voteDetailContext  投票详情文件内容集合
     * @return  VoteBean对象
     */
    private VoteBean getVoteBean(List<String> voteContext, List<String> voteDetailContext) {

        //按照每个注释进行初始化VoteBean对象
        VoteBean voteBean = new VoteBean(voteContext.get(1),voteContext.get(3),voteContext.get(5),
                voteContext.get(7),voteContext.get(9),voteContext.get(11),voteContext.get(13), voteContext.get(15),voteContext.get(17), voteContext.get(19));
        //将投票选项逐个添加到选项list集合中
        List<String> optionlist = new ArrayList<>();

        for (int cnt = 21;cnt<voteContext.size();cnt+=2){
            optionlist.add(voteContext.get(cnt));
        }

        //将投票选项列表设置到VoteBean对象中
        voteBean.setOptions(optionlist);

        //在投票详情列表集合长度大于1的情况下，删除第一行注释

        if (voteDetailContext.size()>1){
            //获取投票详情集合
            List<VoteDetailBean> voteDetailBeanList = new ArrayList<>(voteDetailContext.size()-1);
            voteDetailContext.remove(0);
            //依次遍历集合，并将其以Tab键分割后成数组，再设置到VoteDetailBean中
            for (String str:voteDetailContext){
                String[] arr = str.split("\t");
                VoteDetailBean voteDetailBean = new VoteDetailBean(arr[0],arr[1],arr[2]);
                //将voteDetailBean对象添加到list集合中
                voteDetailBeanList.add(voteDetailBean);
            }
            //最后利用set方法加到VoteBean对象中
            voteBean.setVoteDetailList(voteDetailBeanList);
        }
        return voteBean;
    }

    public int getMaxVoteNumber() {

        return maxVoteNumber;
    }

    public void setMaxVoteNumber(int maxVoteNumber) {

        this.maxVoteNumber = maxVoteNumber;
    }

    public Map<String, VoteBean> getVoteMap() {

        return voteMap;
    }
}
