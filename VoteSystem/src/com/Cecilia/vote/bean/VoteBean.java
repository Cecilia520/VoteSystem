package com.Cecilia.vote.bean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 投票信息bean
 * Created by Cecilia on 2017/8/1.
 */
public class VoteBean {

    private String id;//投票编号

    private String title;//投票主题

    private String changeType;//选项类型

    private String endType;//结束类型

    private boolean endFlag;//结束标识,true表示结束，false表示未结束

    private boolean anonyMousFlag;//匿名的标识，true表示匿名，false表示未匿名

    private Date startTime;//开始时间

    private Date endTime;//结束时间

    private double endParam;//结束的参数

    private String createId;//投票发起人的账户

    private List<String> options;//选项集合

    private List<VoteDetailBean> voteDetailsList;//投票详情集合

    public VoteBean() {
    }

    public VoteBean(String id, String title, String changeType, String endType, String endFlag,
                    String anonyMousFlag, String endParam, String createId, String startTime, String endTime) {
        this.id = id;
        this.title = title;
        this.changeType = changeType;
        this.endType = endType;
        if ("未结束".equals(endFlag)) {
            this.endFlag = false;
        } else {
            this.endFlag = true;
        }
        if ("匿名".equals(anonyMousFlag)) {
            this.anonyMousFlag = true;
        } else {
            this.anonyMousFlag = false;
        }
        this.endParam = Double.valueOf(endParam);
        this.createId = createId;
        this.startTime = new Date(Long.valueOf(startTime));
        this.endTime = new Date(Long.valueOf(endTime));
        options = new ArrayList<>();
        voteDetailsList = new ArrayList<>();

    }

    public VoteBean(String id, String createId, String title, String endFlag, String anonyMousFlag, String changeType) {

        this.id = id;
        this.title = title;
        if ("false".equals(endFlag)) {
            this.endFlag = false;
        } else {
            this.endFlag = true;
        }
        if ("false".equals(anonyMousFlag)) {
            this.anonyMousFlag = false;
        } else {
            this.anonyMousFlag = true;
        }
        this.createId = createId;
        this.changeType = changeType;
        options = new ArrayList<>(0);
        voteDetailsList = new ArrayList<>(0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getEndType() {
        return endType;
    }

    public void setEndType(String endType) {
        this.endType = endType;
    }

    public boolean isEndFlag() {
        return endFlag;
    }

    public void setEndFlag(boolean endFlag) {
        this.endFlag = endFlag;
    }

    public boolean isAnonyMousFlag() {
        return anonyMousFlag;
    }

    public void setAnonyMousFlag(boolean anonyMousFlag) {
        this.anonyMousFlag = anonyMousFlag;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public double getEndParam() {
        return endParam;
    }

    public void setEndParam(double endParam) {
        this.endParam = endParam;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public List<String> getOptions() {

        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<VoteDetailBean> getvoteDetailsList() {
        return voteDetailsList;
    }

    public void setVoteDetailList(List<VoteDetailBean> voteDetailsList) {
        this.voteDetailsList = voteDetailsList;
    }

}
